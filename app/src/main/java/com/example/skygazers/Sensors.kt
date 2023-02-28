package com.example.skygazers
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.*
import kotlin.math.abs

class Sensors(private val sensorManager: SensorManager) : SensorEventListener {
    private lateinit var accelerometer: Sensor
    private lateinit var magneticField: Sensor
    private var accelerometerValues = FloatArray(3)
    private var magneticFieldValues = FloatArray(3)

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
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    fun getOrientationValues() : FloatArray {
        var values = FloatArray(3)
        var R = FloatArray(16)
        var I = FloatArray(16)
        var outR = FloatArray(16)
        getRotationMatrix(R, I, accelerometerValues, magneticFieldValues)

        remapCoordinateSystem(R, AXIS_X, AXIS_Z, outR)
        getOrientation(outR, values)
        values[0] = values[0] * (360 / (2 * (Math.PI))).toFloat()
        values[1] = values[1] * (360 / (2 * (Math.PI))).toFloat()
        values[2] = values[2] * (360 / (2 * (Math.PI))).toFloat()


//        val convert = {value: Float, degree: Int -> if (value < 0) {abs(value) + degree} else value}
//        values[0] = convert(values[0], 180)
        return values
    }


}