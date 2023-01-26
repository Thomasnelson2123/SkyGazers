package com.example.skygazers

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SecondActivityViewModel: ViewModel() {

    private val _latLong = MutableLiveData<String>()

    fun updateLatLong(loc: Location) {
        val sunPosition = SunPosition(loc.latitude, loc.longitude, -8)
        val elevAzimuth = sunPosition.calculateSunPosition(2023, 1, 26, 12, 15)
        val sunrise = sunPosition.getSunrise(2023, 1, 26, 12, 15)
        val sunset = sunPosition.getSunset(2023, 1, 26, 12, 15)
        val string = "Elevation: " + elevAzimuth[0] + "\nAzimuth " + elevAzimuth[1] + "\n" +
                "Sunrise: " + sunrise[0] + ":" + sunrise[1] + "\nSunset: " + sunset[0] + ":" + sunset[1]
        _latLong.value = string
    }

    fun listenLatLong(): LiveData<String> {
        return _latLong
    }
}