package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.database.Favorite
import com.example.myapplication.respository.FavoriteRepository
import com.example.myapplication.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel(private val repository: FavoriteRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun insertFavorite(favorite: Favorite) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insert(favorite)
                withContext(Dispatchers.Main) {
                    _snackbarText.value = Event("Favorite added successfully")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _snackbarText.value = Event("Error adding favorite: ${e.message}")
                }
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getAllFavorites(): LiveData<List<Favorite>> {
        return repository.getAllFavorites()
    }

    fun deleteFavorite(favorite: Favorite) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.delete(favorite)
                withContext(Dispatchers.Main) {
                    _snackbarText.value = Event("Favorite deleted successfully")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _snackbarText.value = Event("Error deleting favorite: ${e.message}")
                }
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun isFavorite(id: Int): LiveData<Boolean> {
        return repository.isFavorite(id)
    }
}
