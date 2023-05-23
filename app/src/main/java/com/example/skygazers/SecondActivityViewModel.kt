package com.example.skygazers

import android.location.Location
import android.util.Log
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
    val suns: MutableLiveData<ArrayList<SunObject?>> = MutableLiveData<ArrayList<SunObject?>>()



    fun updateLatLong(loc: Location, year: Int, month: Int, day: Int) {
        this.loc = loc
        this.year = year
        this.month = month + 1
        this.day = day
        val sun = SunObject(loc, this.year, this.month, this.day, 0)
        var array = ArrayList<SunObject?>(1)
        array.add(sun)
        suns.value = array
        Log.d("Array checking", suns.value.toString())
        Log.d("Array checking", sun.toString())

    }

    fun updateTime(hour: Int){
        // THIS IS WRONG FIX LATER
        suns.value?.get(0)?.updateHour(hour)
    }

    fun getSunObject(): LiveData<ArrayList<SunObject?>> {
        return this.suns
    }

    /*fun createImgOverlay(x: Int, y: Int) {
        var params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,100);
        params.leftMargin = x;
        params.topMargin = y;

    }*/
}