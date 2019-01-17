package com.kanawish.sample.mvi.model.backend

import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.TaskEditorState
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import timber.log.Timber
import toothpick.ProvidesSingletonInScope
import toothpick.Toothpick
import toothpick.config.Module
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

typealias BaseUrl = String // TODO: Validate this idea works with Toothpick

object TaskBackendModule : Module() { init {
    // TODO: Map to a real 'demo' 'dev/null' firebase url.
    bind(BaseUrl::class.java).toInstance("https://example.com")
    // TODO: Binding implicit with annotations below? TBD / double check.
    bind(TaskRestApi::class.java).toProvider(TaskRestBackend::class.java)//.providesSingletonInScope()
} }

/**
 * Firebase "push()/POST-to-list" response.
 *
 * see [Firebase REST docs](https://firebase.google.com/docs/reference/rest/database/)
 * for more details.
 */
data class PushResponse(val name:String)

interface TaskRestApi {
    @POST("tasks") @FormUrlEncoded
    fun postTask(@Body task: Task): Observable<PushResponse>
}

@Singleton @ProvidesSingletonInScope
class TaskRestBackend @Inject constructor(baseUrl: BaseUrl): Provider<TaskRestApi> {
    override fun get(): TaskRestApi = retrofit.create(TaskRestApi::class.java)

    private val retrofit:Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun main() {
    val scope = Toothpick.openScope("BOGUS").apply {
        installModules(TaskBackendModule)
    }

    val backend = scope.getInstance(TaskRestApi::class.java)

    val dummy = backend
        // TODO: If we use Firebase, we'll need to adjust the id scheme.
        .postTask(Task())
        .map <TaskEditorState> {

            // `name` is the unique key for this new ToDo
            TaskEditorState.Editing( Task(id = it.name ) )
        }
        .startWith(TaskEditorState.Creating)
        .onErrorReturn { TaskEditorState.Closed }

    Timber.i("Done")
}