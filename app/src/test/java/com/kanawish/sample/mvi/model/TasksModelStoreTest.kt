package com.kanawish.sample.mvi.model

import com.kanawish.sample.mvi.intent.intent
import com.kanawish.sample.mvi.model.FilterType.ANY
import com.kanawish.sample.mvi.model.SyncState.IDLE
import com.kanawish.sample.mvi.model.SyncState.PROCESS
import com.kanawish.sample.mvi.model.SyncState.PROCESS.Type.REFRESH
import com.kanawish.sample.mvi.util.ReplaceMainThreadSchedulerRule
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import toothpick.testing.ToothPickRule
import javax.inject.Inject

class TasksModelStoreTest {

    // Swap out AndroidSchedulers.mainThread() for trampoline scheduler.
    @get:Rule val schedulerRule = ReplaceMainThreadSchedulerRule()

    // Injects any @Mock reference properties in this test class.
    @get:Rule val mockitoRule:MockitoRule = MockitoJUnit.rule()

    // Uses @Mock as dependencies for injection, and resets Toothpick at the end of each test.
    @get:Rule val toothPickRule = ToothPickRule(this, this)

    // Instance under test.
    @Inject lateinit var tasksModelStore: TasksModelStore

    @Before
    fun setUp() {
        toothPickRule.inject(this)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @Test
    fun startingState() {
        val testObserver = TestObserver<TasksState>()

        tasksModelStore.modelState().subscribe(testObserver)

        testObserver.assertValue(
                TasksState(
                        emptyList(),
                        ANY,
                        IDLE
                )
        )
    }

    @Test
    fun changes() {
        val testObserver = TestObserver<TasksState>()

        // Process a mock intent
        tasksModelStore.process( intent {
            // Simulates a pull to refresh
            copy(syncState = PROCESS(REFRESH))
        } )

        // We subscribe after this to validate replay works correctly.
        tasksModelStore.modelState().subscribe(testObserver)

        // Expected stated when refresh is running after mock intent above.
        testObserver.assertValueCount(1)
        testObserver.values().last().let {
            assert(it.filter == ANY)
            assert(it.syncState == PROCESS(REFRESH))
        }

        // Simulate a successful refresh call, that returned 1 task.
        tasksModelStore.process(intent {
            copy(tasks = listOf(Task()), syncState = IDLE)
        })
        // Validate expectations
        testObserver.assertValueCount(2)
        testObserver.values().last().let {
            assert(it.tasks.size == 1)
            assert(it.filter == ANY)
            assert(it.syncState == IDLE)
        }
    }
}

