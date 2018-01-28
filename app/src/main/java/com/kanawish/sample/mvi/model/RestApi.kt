package com.kanawish.sample.mvi.model

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

fun retrofitBuilder(): Retrofit = Retrofit.Builder()
        .baseUrl(" https://casterdemoendpoints.firebaseio.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

/**
 * https://casterdemoendpoints.firebaseio.com/demo/$id
 */
interface RestApi {
    @GET("demo.json")
    fun tasksGet(): Single<List<Task>>

    @PUT("demo/{id}.json")
    fun taskPut(@Path("id") id: String, @Body task: Task): Single<ResponseBody>

    @DELETE("demo/{id}.json")
    fun taskDelete(@Path("id") id: String): Single<ResponseBody>
    // TODO: Look into Retrofit's Response as well.
}