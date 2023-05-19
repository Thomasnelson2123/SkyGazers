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
    val sun = MutableLiveData<SunObject>()


    fun updateLatLong(loc: Location, year: Int, month: Int, day: Int) {
        this.loc = loc
        this.year = year
        this.month = month + 1
        this.day = day
        sun.value = SunObject(loc, year, month, day, 0)

    }

    fun updateTime(hour: Int){
        sun.value?.updateHour(hour)
    }

    fun getSunObject(): LiveData<SunObject?> {
        return this.sun
    }

    /*fun createImgOverlay(x: Int, y: Int) {
        var params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,100);
        params.leftMargin = x;
        params.topMargin = y;

    }*/
}