package com.kanawish.sample.mvi.model

import android.view.View
import com.kanawish.sample.mvi.R
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * For now, sticking all the needed 'view model streams' here.
 *
 * TODO: Create a interface layer on top of this to further separate concerns between Views.
 */
@Singleton
class ViewModel @Inject constructor(val model: Model) {

    // NOTE: Since our ViewModel is simply a dumb layer on top of our Model proper, no "SINKS" can exist.

    // ***** SOURCES [Consumed by the Views] *****

    fun refreshing(): Observable<Boolean> {
        return model.syncState()
                .map { it is SyncState.PROCESS }
    }

    /**
     * Emits String Res Id for Filter Type description.
     */
    fun filterDescription(): Observable<Int> {
        return model.filter().map {
            when (it) {
                FilterType.ANY -> R.string.nav_all
                FilterType.ACTIVE -> R.string.nav_active
                FilterType.COMPLETE -> R.string.nav_completed
            }
        }
    }

    /**
     * View component visibility depends on empty state.
     * returns `Pair( contentView, emptyView )` visibility attributes.
     *
     * NOTE: If using Pair gets confusing, use fully typed data classes to hold view attributes.
     * TODO: Good use case for type aliases
     */
    fun contentVisibility(): Observable<Pair<Int, Int>> {
        return model.tasks()
                .map {
                    if (it.isNotEmpty()) {
                        (View.VISIBLE to View.GONE) // not empty
                    } else {
                        (View.GONE to View.VISIBLE) // empty
                    }
                }
    }

    /**
     * Empty state text/icon changes depending on selected filter.
     *
     * Emits pairs of String and Drawable Res Ids
     */
    fun noTasksMap(): Observable<Pair<Int, Int>> {
        return model.tasks()
                .filter { tasks -> tasks.isEmpty() }
                .flatMap { _ -> model.filter() }
                .map { filterEmpty ->
                    when (filterEmpty) {
                        FilterType.ANY -> (R.string.no_tasks_all to R.drawable.ic_assignment_turned_in_24dp)
                        FilterType.ACTIVE -> (R.string.no_tasks_active to R.drawable.ic_check_circle_24dp)
                        FilterType.COMPLETE -> (R.string.no_tasks_completed to R.drawable.ic_verified_user_24dp)
                    }
                }
    }


    // NOTE: Fine grained mapping gives us fine grain updates.

    // NOTE: Full screen refresh works too. Bundle all view model states in the same Observable, if so desired.

}