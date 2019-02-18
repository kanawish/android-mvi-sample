package com.kanawish.sample.mvi.model

import com.kanawish.sample.mvi.intent.intent
import com.kanawish.sample.mvi.model.FilterType.ACTIVE
import com.kanawish.sample.mvi.model.FilterType.COMPLETE
import com.kanawish.sample.mvi.model.SyncState.IDLE
import com.kanawish.sample.mvi.model.SyncState.PROCESS
import com.kanawish.sample.mvi.model.SyncState.PROCESS.Type.REFRESH
import com.kanawish.sample.mvi.utils.ReplaceMainThreadSchedulerRule
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import toothpick.testing.ToothPickRule
import javax.inject.Inject

class TasksModelStoreTest {

    // Swaps out AndroidSchedulers.mainThread() for trampoline scheduler.
    @get:Rule val schedulerRule = ReplaceMainThreadSchedulerRule()

    // Injects any @Mock references properties in this test class.
    @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

    // Uses @Mock as dependencies for injection, and resets ToothPick at the end of each test.
    @get:Rule val toothPickRule = ToothPickRule(this, this)

    // Instance under test.
    @Inject lateinit var tasksModelStore: TasksModelStore

    @Before
    fun setUp() {
        toothPickRule.inject(this)
    }

    @Test
    fun startingState() {
        val testObserver = TestObserver<TasksState>()

        tasksModelStore.modelState().subscribe(testObserver)

        testObserver.assertValue(
                TasksState(
                        emptyList(),
                        FilterType.ANY,
                        SyncState.IDLE
                )
        )
    }

    @Test
    fun changes() {
        val testObserver = TestObserver<TasksState>()

        // Process a mock intent
        tasksModelStore.process(intent {
            copy(filter = COMPLETE, syncState = PROCESS(REFRESH))
        })

        // We subscribeToState after this, to validate our replay works correctly.
        tasksModelStore.modelState().subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.values().last().let {
            assert(it.filter == COMPLETE)
            assert(it.syncState == PROCESS(REFRESH))
        }

        tasksModelStore.process(intent {
            copy(tasks = listOf(Task()), filter = ACTIVE, syncState = IDLE)
        })
        testObserver.assertValueCount(2)
        testObserver.values().last().let {
            assert(it.tasks.size == 1)
            assert(it.filter == ACTIVE)
            assert(it.syncState == IDLE)
        }
    }

}