package com.example.skygazers

import android.location.Location
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class SecondActivityViewModel: ViewModel() {

    private val _latLong = MutableLiveData<String>()
    var loc : Location? = null
    var year:Int = 0
    var month: Int = 0
    var day: Int = 0
    lateinit var sunPosition: SunPosition
    lateinit var elevAzimuth: DoubleArray
    lateinit var sunrise: IntArray
    lateinit var sunset: IntArray


    fun updateLatLong(loc: Location, year: Int, month: Int, day: Int,) {
        this.loc = loc
        this.year = year
        this.month = month
        this.day = day

        this.sunPosition = SunPosition(loc.latitude, loc.longitude, -8)
        this.elevAzimuth = sunPosition.calculateSunPosition(year, month+1, day, 12, 15)
        this.sunrise = sunPosition.getSunrise(year, month+1, day, 12, 15)
        this.sunset = sunPosition.getSunset(year, month+1, day, 12, 15)

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
        _latLong.value = ""
    }

    fun updateTime(hour: Int): DoubleArray {
        return sunPosition.calculateSunPosition(year, month, day, hour, 0)

    }

    fun listenLatLong(): LiveData<String> {
        return _latLong
    }

    /*fun createImgOverlay(x: Int, y: Int) {
        var params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,100);
        params.leftMargin = x;
        params.topMargin = y;

    }*/
}