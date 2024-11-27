package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.response.EventsItem
import com.example.myapplication.data.response.ListEvent
import com.example.myapplication.data.retrofit.ApiConfig
import com.example.myapplication.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {

    private val _eventDetail = MutableLiveData<ListEvent?>()
    val eventDetail: LiveData<ListEvent?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    // Fungsi untuk mengambil detail event berdasarkan eventId
    fun getEventDetail(eventId: String) {
        _isLoading.value = true
        val client: Call<EventsItem> = ApiConfig.getApiService().getEventDetail(eventId)


        client.enqueue(object : Callback<EventsItem> {
            override fun onResponse(call: Call<EventsItem>, response: Response<EventsItem>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val eventItem = it.event
                        _eventDetail.value = eventItem
                    } ?: run {
                        _snackbarText.value = Event("Received empty response")
                    }
                } else {
                    _snackbarText.value = Event("Failed to fetch event detail: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventsItem>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event("Network error: ${t.message}")
            }
        })
    }
}
