package com.kanawish.sample.mvi.view

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import timber.log.Timber

/**
 * A view outputs VIEW_EVENT types, and
 * expects STATE in input.
 */
interface ViewContract<VIEW_EVENT, STATE> {
    /**
     * View processes inputs by subscribing to a Store of STATE.
     *
     * By exposing a `subscribeView()` extension function instead of an
     * Observer, we allow views to create fine-grained subscriptions.
     *
     * For example, `map(::firstName).distinctUntilChanged().subscribe(textView:: TODO
     *
     * Since it is valid for a view to only be used as an event source, we default to
     * returning an inert disposable. We override this extension function if
     * our view reflects changes to store state.
     */
    fun Observable<STATE>.subscribeView(): Disposable = Disposables.fromAction {
        Timber.i("No views were subscribed, no disposal needed.")
    }

    /**
     * View outputs are exposed via an Observable<VIEW_EVENT> stream.
     */
    fun events(): Observable<VIEW_EVENT>
}
