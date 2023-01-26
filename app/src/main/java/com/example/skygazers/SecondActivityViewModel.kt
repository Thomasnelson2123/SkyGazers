package com.example.skygazers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SecondActivityViewModel: ViewModel() {

    private val _latLong = MutableLiveData<String>()

    fun updateLatLong(latlong: String) {
        _latLong.value = latlong
    }

    fun listenLatLong(): LiveData<String> {
        return _latLong
    }
}