package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.response.EventResponse
import com.example.myapplication.data.response.ListEventsItem
import com.example.myapplication.data.retrofit.ApiConfig
import com.example.myapplication.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {


    // List untuk acara upcoming
    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    // List untuk acara finished
    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    // Loading indicator
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    companion object {
        private const val TAG = "MainViewModel"
    }

    // Fungsi untuk mendapatkan acara upcoming
    fun getUpcomingEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        _upcomingEvents.value = responseBody.listEvents
                    } ?: run {
                        Log.e(TAG, "Response body for upcoming events is null")
                        _snackbarText.value = Event("Received empty response for upcoming events")
                    }
                } else {
                    Log.e(TAG, "Failed to fetch upcoming events: ${response.message()}")
                    _snackbarText.value = Event("Failed to fetch upcoming events: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Network error while fetching upcoming events: ${t.message}")
                _snackbarText.value = Event("Network error: ${t.message}")
            }
        })
    }

    // Fungsi untuk mendapatkan acara finished
    fun getFinishedEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        _finishedEvents.value = responseBody.listEvents
                    } ?: run {
                        Log.e(TAG, "Response body for finished events is null")
                        _snackbarText.value = Event("Received empty response for finished events")
                    }
                } else {
                    Log.e(TAG, "Failed to fetch finished events: ${response.message()}")
                    _snackbarText.value = Event("Failed to fetch finished events: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Network error while fetching finished events: ${t.message}")
                _snackbarText.value = Event("Network error: ${t.message}")
            }
        })
    }

}
