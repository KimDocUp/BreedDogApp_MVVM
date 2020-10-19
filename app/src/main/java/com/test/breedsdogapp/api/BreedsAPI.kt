package com.test.breedsdogapp.api

import com.test.breedsdogapp.models.MessageResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface BreedsAPI {
    @GET("breeds/list/")
    suspend fun getBreeds(): Response<MessageResponse>

    @GET("/breed/{name}/list")
    suspend fun getSubBreeds(@Path("name") breedName: String): Response<MessageResponse>

    @GET("/breed/{name}/images")
    suspend fun getBreedImages(@Path("name") breedName: String): Response<MessageResponse>

    @GET("/breed/{name}/{subName}/images")
    suspend fun getSubBreedImages(@Path("name") breedName: String, @Path("subName") subBreedName: String): Response<MessageResponse>


    companion object{
        operator fun invoke() : BreedsAPI {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.dog.ceo")
                .build()
                .create(BreedsAPI::class.java)
        }
    }
}