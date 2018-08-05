package com.kanawish.sample.mvi.view

import io.reactivex.Observable

/**
 * A view outputs VIEW_EVENT types, and
 * expects STATE in input.
 */
interface ViewContract<VIEW_EVENT, STATE> {
    /**
     * View outputs are exposed via an Observable<VIEW_EVENT> stream.
     */
    fun events(): Observable<VIEW_EVENT>

}
