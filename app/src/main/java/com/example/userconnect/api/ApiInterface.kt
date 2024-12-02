package com.example.userconnect.api

import com.example.userconnect.models.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("api/")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("results") results: Int
    ): Response<UserResponse>

    @GET("api/")
    suspend fun getAllUsers(): Response<UserResponse>
}
