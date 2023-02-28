package com.example.skygazers
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.*
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.Math.abs
import kotlin.math.acos


class Sensors(private val sensorManager: SensorManager) : SensorEventListener {
    private lateinit var accelerometer: Sensor
    private lateinit var magneticField: Sensor
    private var accelerometerValues = FloatArray(3)
    private var magneticFieldValues = FloatArray(3)
    val TWENTY_FIVE_DEGREE_IN_RADIAN = 0.436332313f
    val ONE_FIFTY_FIVE_DEGREE_IN_RADIAN = 2.7052603f
    private var mFacing: Float = Float.NaN
    private var DEBUG = true
    private var TAG = "Debugging"
    private val mRotHist: MutableList<FloatArray> = mutableListOf()
    private var mRotHistIndex = 0
    var R = FloatArray(9)
    var I = FloatArray(9)
    var outR = FloatArray(9)
    private val mHistoryMaxLength = 40
    private val oreintationData = MutableStateFlow(FloatArray(3))

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    fun startSensors() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopSensors() {
        sensorManager.unregisterListener(this)
    }

    fun getAccelerometerValues(): FloatArray {
        return accelerometerValues
    }

    fun getMagneticFieldValues(): FloatArray {
        return magneticFieldValues
    }

    fun getRotationMatrixValues(): FloatArray {
        var R = FloatArray(16)
        var I = FloatArray(16)
        getRotationMatrix(R, I, accelerometerValues, magneticFieldValues)
        return R
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerValues = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> magneticFieldValues = event.values.clone()
        }
        val current = getOrientationValues()
        oreintationData.value = current

    }

    fun subscribeOrientation(): Flow<FloatArray> {
        return oreintationData
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    fun getOrientationValues() : FloatArray {
        var values = FloatArray(3)
        var R = FloatArray(16)
        var I = FloatArray(16)
        var outR = FloatArray(16)
        getRotationMatrix(R, I, accelerometerValues, magneticFieldValues)

        // inclination is the degree of tilt by the device independent of orientation (portrait or landscape)
        // if less than 25 or more than 155 degrees the device is considered lying flat
        val inclination = acos(R[8])
        if (inclination < TWENTY_FIVE_DEGREE_IN_RADIAN
            || inclination > ONE_FIFTY_FIVE_DEGREE_IN_RADIAN
        ) {
            // mFacing is undefined, so we need to clear the history
            clearRotHist()
            mFacing = Float.NaN
        } else {
            setRotHist()
            // mFacing = azimuth is in radian
            mFacing = findFacing()
        }

        remapCoordinateSystem(R, AXIS_X, AXIS_Z, outR)
        getOrientation(outR, values)
        if(mFacing.isNaN()) {
            values[0] = ((Math.toDegrees(values[0].toDouble())+ 360) % 360).toFloat()
        }
        else {
            values[0] = ((Math.toDegrees(mFacing.toDouble())+ 360) % 360).toFloat()
        }
//        values[0] = ((Math.toDegrees(mFacing.toDouble())  + 360) % 360).toFloat()
        values[1] = Math.toDegrees(values[1].toDouble()).toFloat()
        values[2] = Math.toDegrees(values[2].toDouble()).toFloat()


//        val convert = {value: Float, degree: Int -> if (value < 0) {
//            kotlin.math.abs(value) + degree} else value}
//        values[0] = convert(values[0], 180)
        return values
    }

    private fun clearRotHist() {
        if (DEBUG) {
            Log.d(TAG, "clearRotHist()")
        }
        mRotHist.clear()
        mRotHistIndex = 0
    }

    private fun setRotHist() {
        if (DEBUG) {
            Log.d(TAG, "setRotHist()")
        }
        val hist: FloatArray = R.clone()
        if (mRotHist.size == mHistoryMaxLength) {
            mRotHist.removeAt(mRotHistIndex)
        }
        mRotHist.add(mRotHistIndex++, hist)
        mRotHistIndex %= mHistoryMaxLength
    }

    private fun findFacing(): Float {
        if (DEBUG) {
            Log.d(TAG, "findFacing()")
        }
        val averageRotHist: FloatArray? = average(mRotHist)
        return Math.atan2(-averageRotHist?.get(2)?.toDouble()!!, -averageRotHist?.get(5)!!.toDouble()).toFloat()
    }

    fun average(values: List<FloatArray>): FloatArray? {
        val result = FloatArray(9)
        for (value in values) {
            for (i in 0..8) {
                result[i] += value[i]
            }
        }
        for (i in 0..8) {
            result[i] = result[i] / values.size
        }
        return result
    }


}