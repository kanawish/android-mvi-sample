package com.kanawish.sample.mvi.model.backend

import com.kanawish.sample.mvi.model.Task
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import toothpick.ProvidesSingletonInScope
import toothpick.config.Module
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Simple typing for DI bindings.
 */
typealias BaseUrl = String

/**
 * Module defining bindings for the TasksRestApi
 */
object TasksRestApiModule : Module() {
    init {
        bind(BaseUrl::class.java).toInstance("https://casterdemoendpoints.firebaseio.com/")
        bind(TasksRestApi::class.java).toProvider(TasksRestApiProvider::class.java)
    }
}

interface TasksRestApi {
    /**
     * Get a Map of (read only) tasks from our demo Firebase sample.
     */
    @GET("tasks.json")
    fun getTasks(): Observable<Map<String, Task>>

    /**
     * NOTE: For now, left as an exercise for reader. You'll need your own Firebase instance.
     */
    @POST("tasks")
    @FormUrlEncoded
    fun postTask(@Body task: Task): Observable<PushResponse>
}

/**
 * Firebase "push()/POST-to-list" response.
 *
 * NOTE: see [Firebase REST docs](https://firebase.google.com/docs/reference/rest/database/)
 */
data class PushResponse(val name: String)



@Singleton
@ProvidesSingletonInScope
class TasksRestApiProvider @Inject constructor(baseUrl: BaseUrl) : Provider<TasksRestApi> {
    override fun get(): TasksRestApi = retrofit.create(TasksRestApi::class.java)

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}