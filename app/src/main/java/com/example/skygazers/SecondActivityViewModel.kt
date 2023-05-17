package com.example.skygazers

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SecondActivityViewModel: ViewModel() {

    private val _latLong = MutableLiveData<String>()
    var loc : Location? = null
    var year:Int = 0
    var month: Int = 0
    var day: Int = 0
    lateinit var sunPositionObject: SunPosition
    lateinit var elevAzimuth: DoubleArray
    lateinit var sunrise: IntArray
    lateinit var sunset: IntArray
    lateinit var sun: SunObject


    fun updateLatLong(loc: Location, year: Int, month: Int, day: Int,) {
        this.loc = loc
        this.year = year
        this.month = month + 1
        this.day = day

        this.sunPositionObject = SunPosition(loc.latitude, loc.longitude, -8)
        this.elevAzimuth = sunPositionObject.calculateSunPosition(year, month, day, 12, 15)
        this.sunrise = sunPositionObject.getSunrise(year, month, day, 12, 15)
        this.sunset = sunPositionObject.getSunset(year, month, day, 12, 15)

//        string = "year " + year + "month " + month + "day " + day
        _latLong.value = ""
    }

    fun updateTime(hour: Int): DoubleArray {
        return sunPositionObject.calculateSunPosition(year, month, day, hour-1, 0)

    }

    fun listenLatLong(): LiveData<String> {
        return _latLong
    }

    fun setSunObject(sun: SunObject) {
        this.sun = sun
    }
    fun getSunObject(): SunObject {
        return this.sun
    }

    /*fun createImgOverlay(x: Int, y: Int) {
        var params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,100);
        params.leftMargin = x;
        params.topMargin = y;

    }*/
}