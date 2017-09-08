package com.kanawish.mvi.logic

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

// 01
data class Model(val firstName: String = "", val lastName: String = "", val score: Int = 0)

interface ModelStore {
    // Intent consumer
    fun apply(intent: Intent)

    // Model producer
    fun model(): Observable<Model>
}


// 02
interface View {
    // Model consumer
    fun render(model: Model)

    // View Event producer
    fun viewEvents(): Observable<ViewEvent>
}

sealed class ViewEvent {
    data class FirstNameFieldChange(val change: String) : ViewEvent()
    data class LastNameFieldChange(val change: String) : ViewEvent()
    object IncrementScoreClick : ViewEvent()
}


// 03
sealed class Intent(val reducer: (Model) -> Model) {

    class IncrementScoreIntent(increment: Int = 1) : Intent({
        it.copy(score = it.score + increment)
    })

    class EditFirstNameIntent(firstName: String) : Intent({
        it.copy(firstName = firstName)
    })

    class EditLastNameIntent(lastName: String) : Intent({
        it.copy(lastName = lastName)
    })

}

// 04
fun Observable<ViewEvent>.toIntent(): Observable<Intent> =
        map {
            when (it) {
                is ViewEvent.FirstNameFieldChange -> Intent.EditFirstNameIntent(it.change)
                is ViewEvent.LastNameFieldChange -> Intent.EditLastNameIntent(it.change)
                ViewEvent.IncrementScoreClick -> Intent.IncrementScoreIntent()
            }
        }


// 05
class ViewImpl() : View {
    private val viewEvents: PublishRelay<ViewEvent> = PublishRelay.create<ViewEvent>()

    override fun render(model: Model) {
        println("View.render():\t${model.firstName}, ${model.lastName}: ${model.score} pts.\n")
    }

    override fun viewEvents(): Observable<ViewEvent> {
        return viewEvents.hide()
    }

    // Simulates user editing first name
    fun editFirst(f: String) {
        println("ViewEvent:\teditFirst( $f )")
        viewEvents.accept(ViewEvent.FirstNameFieldChange(f))
    }

    // Simulates user editing last name
    fun editLast(l: String) {
        println("ViewEvent:\teditLast( $l )")
        viewEvents.accept(ViewEvent.LastNameFieldChange(l))
    }

    // Simulates user clicking score increment
    fun buttonClick() {
        println("ViewEvent:\tbuttonClick()")
        viewEvents.accept(ViewEvent.IncrementScoreClick)
    }
}



// 06
// Application Store

class ModelStoreImpl : ModelStore {

    private val intents: PublishRelay<Intent> = PublishRelay.create<Intent>()

    override fun apply(intent: Intent) {
        intents.accept(intent)
    }


    private val store: Observable<Model> = intents
            .doOnNext { println("Intent:\t\t${it.javaClass.simpleName}") }
            .map { it.reducer }
            .scan(Model("Bob", "L'Eponge", 0), { old, reducer -> reducer.invoke(old) })
            .doOnNext { println("Model:\t\t${it.firstName}, ${it.lastName}: ${it.score} pts.") }
            .replay(1)
            .apply { storeDisposable = connect() }

    private lateinit var storeDisposable: Disposable

    // NOTE: As we further explore the MVI pattern, we'll create more targeted Observable streams.
    override fun model(): Observable<Model> {
        return store
    }

}

// 07
class MviDemo {

    val modelStore = ModelStoreImpl()

    val view = ViewImpl()

    // We add disposables to composites via the overloaded `+=` operator
    val disposables = CompositeDisposable()

    @Test
    fun mviDemo() {

        // Next we wire up the components. Starting with Model -> View
        disposables += modelStore.model().subscribe(view::render)

        // We build the intent observable, View -> Intent
        val intents: Observable<Intent> = view.viewEvents().toIntent()

        // And subscribe the Model, Intent -> Model
        disposables += intents.subscribe(modelStore::apply)

        // Let's generate a series of view events to validate our implementation
        view.buttonClick()
        view.editLast("Pantalons Carres")
        view.buttonClick()
        view.buttonClick()
        view.buttonClick()
        view.editFirst("Bob L'Eponge")
        view.buttonClick()
        view.buttonClick()

        println("mviDemo() complete.")
    }


}