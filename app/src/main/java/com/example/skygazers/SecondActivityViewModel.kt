package com.example.skygazers

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class SecondActivityViewModel: ViewModel() {

    private val _latLong = MutableLiveData<String>()

    fun updateLatLong(loc: Location, year: Int, month: Int, day: Int) {
        val sunPosition = SunPosition(loc.latitude, loc.longitude, -8)
        val elevAzimuth = sunPosition.calculateSunPosition(year, month+1, day, 12, 15)
        val sunrise = sunPosition.getSunrise(year, month+1, day, 12, 15)
        val sunset = sunPosition.getSunset(year, month+1, day, 12, 15)

        var string = ""

        if(sunrise[1] < 10 && sunset[1] < 10) {
            string = "Elevation: " + elevAzimuth[0] + "\nAzimuth " + elevAzimuth[1] + "\n" +
                    "Sunrise: " + sunrise[0] + ":0" + sunrise[1] + "\nSunset: " + sunset[0] + ":0" + sunset[1]
        }
        else if(sunrise[1] < 10) {
            string = "Elevation: " + elevAzimuth[0] + "\nAzimuth " + elevAzimuth[1] + "\n" +
                    "Sunrise: " + sunrise[0] + ":0" + sunrise[1] + "\nSunset: " + sunset[0] + ":" + sunset[1]
        }
        else if(sunset[1] < 10) {
            string = "Elevation: " + elevAzimuth[0] + "\nAzimuth " + elevAzimuth[1] + "\n" +
                    "Sunrise: " + sunrise[0] + ":" + sunrise[1] + "\nSunset: " + sunset[0] + ":0" + sunset[1]
        }
        else {
            string = "Elevation: " + elevAzimuth[0] + "\nAzimuth " + elevAzimuth[1] + "\n" +
                    "Sunrise: " + sunrise[0] + ":" + sunrise[1] + "\nSunset: " + sunset[0] + ":" + sunset[1]
        }
//        string = "year " + year + "month " + month + "day " + day
        _latLong.value = string
    }

    fun listenLatLong(): LiveData<String> {
        return _latLong
    }
}