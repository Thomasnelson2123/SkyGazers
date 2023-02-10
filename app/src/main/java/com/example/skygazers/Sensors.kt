package com.example.skygazers
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

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

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerValues = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> magneticFieldValues = event.values.clone()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }


}