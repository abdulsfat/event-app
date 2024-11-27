package com.example.myapplication.data.retrofit

import com.example.myapplication.data.response.EventResponse
import com.example.myapplication.data.response.EventsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getEventDetail(
        @Path("id") active: String
    ): Call<EventsItem>

}
