package com.kanawish.mvi.logic

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import org.junit.Test
import timber.log.Timber

// 01
data class Player(
        val firstName: String = "",
        val lastName: String = "",
        val score: Int = 0)

interface Model {
    // Player producer
    fun player(): Observable<Player>

    // Intent consumer
    fun accept(intent: Intent)
}


// 02
interface View {
    // Player consumer
    fun render(player: Player)

    // View Event producer
    fun viewEvents(): Observable<ViewEvent>
}

sealed class ViewEvent {
    data class FirstNameFieldChange(val change: String) : ViewEvent()
    data class LastNameFieldChange(val change: String) : ViewEvent()
    object IncrementScoreClick : ViewEvent()
}


// 03
sealed class Intent(val reducer: (Player) -> Player) {

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

    override fun render(player: Player) {
        println("View.render():\t${player.firstName}, ${player.lastName}: ${player.score} pts.\n")
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

class ModelImpl : Model {

    private val intents: PublishRelay<Intent> = PublishRelay.create<Intent>()

    override fun accept(intent: Intent) {
        intents.accept(intent)
    }

    private lateinit var storeDisposable: Disposable

    private val store: Observable<Player> = intents
            .doOnNext { println("Intent:\t\t${it.javaClass.simpleName}") }
            .map { it.reducer }
            .scan(Player("Bob", "L'Eponge", 0), { old, reducer -> reducer.invoke(old) })
            .doOnNext { println("Player:\t\t${it.firstName}, ${it.lastName}: ${it.score} pts.") }
            .replay(1)
            .apply { storeDisposable = connect() }


    // NOTE: As we further explore the MVI pattern, we'll create more targeted Observable streams.
    override fun player(): Observable<Player> = store.hide()

}

// 07
class MviDemo {

    val modelStore = ModelImpl()

    val view = ViewImpl()

    // We add disposables to composites via the overloaded `+=` operator
    val disposables = CompositeDisposable()

    @Test
    fun mviDemo() {

        Timber.d("mviDemo()")

        // Next we wire up the components. Starting with Player -> View
        disposables += modelStore.player().subscribe(view::render)

        // We build the intent observable, View -> Intent
//        val intents: Observable<Intent> = view.viewEvents().toIntent()

        // And subscribe the Player, Intent -> Player
//        disposables += intents.subscribe(modelStore::accept)

        // One-liner equivalent:
        disposables += view.viewEvents().toIntent().subscribe(modelStore::accept)

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