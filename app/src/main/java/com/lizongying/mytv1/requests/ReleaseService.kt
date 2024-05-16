package com.lizongying.mytv1.requests

import retrofit2.Call
import retrofit2.http.GET

interface ReleaseService {
    @GET("main/version.json")
    fun getRelease(
    ): Call<ReleaseResponse>
}