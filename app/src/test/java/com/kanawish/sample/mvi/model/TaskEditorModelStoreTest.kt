package com.kanawish.sample.mvi.model

import com.kanawish.sample.mvi.intent.AddEditTaskIntentFactory.Companion.editorIntent
import com.kanawish.sample.mvi.utils.ReplaceMainThreadSchedulerRule
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import toothpick.testing.ToothPickRule
import javax.inject.Inject

class TaskEditorModelStoreTest {

    // Swaps out AndroidSchedulers.mainThread() for trampoline scheduler.
    @get:Rule val schedulerRule = ReplaceMainThreadSchedulerRule()

    // Injects any @Mock references properties in this test class.
    @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

    // Uses @Mock as dependencies for injection, and resets ToothPick at the end of each test.
    @get:Rule val toothPickRule = ToothPickRule(this, this)

    // Instance under test.
    @Inject lateinit var taskEditorModelStore: TaskEditorModelStore

    @Before
    fun setUp() {
        toothPickRule.inject(this)
    }

    @Test
    fun startingState() {
        val testObserver = TestObserver<TaskEditorState>()

        taskEditorModelStore.modelState().subscribe(testObserver)

        testObserver.assertValue(TaskEditorState.Closed)
        testObserver.assertNoErrors()
    }

    @Test
    fun validTransitions() {
        val testObserver = TestObserver<TaskEditorState>()

        val addedTask = Task(id = "TEST-SUCCESS")
        val addIntent = editorIntent<TaskEditorState.Closed> {
            addTask(addedTask)
        }
        taskEditorModelStore.process(addIntent)
        taskEditorModelStore.modelState().subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.values().last().let {
            when (it) {
                is TaskEditorState.Editing -> assert(it.task == addedTask) { "Edited task doesn't match what was added in." }
                else -> assert(false) { "Expected type 'Editing', was $it" }
            }
        }

        val cancelIntent = editorIntent<TaskEditorState.Editing> { cancel() }
        taskEditorModelStore.process(cancelIntent)

        testObserver.assertValueCount(2)
        testObserver.values().last().let {
            assert(it is TaskEditorState.Closed) { "Expected type 'Closed', was $it" }
        }

        testObserver.assertNoErrors()
    }

    @Test
    fun invalidTransitions() {
        val testObserver = TestObserver<TaskEditorState>()

        // This is invalid, starting state should always be "TaskEditorState.Closed".
        val invalidIntent = editorIntent<TaskEditorState.Editing> {
            edit { copy(title = "This intent is invalid and won't reduce.") }
        }

        taskEditorModelStore.modelState().subscribe(testObserver)

        // InvalidStateException expected.
        taskEditorModelStore.process(invalidIntent)

        testObserver.assertValueCount(1)
        testObserver.assertValue(TaskEditorState.Closed)
        testObserver.assertError(IllegalStateException::class.java)
    }

}