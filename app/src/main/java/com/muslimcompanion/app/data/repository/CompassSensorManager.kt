package com.muslimcompanion.app.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

data class CompassData(
    val heading: Float = 0f,
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val isFlat: Boolean = true,
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
    val hasSensor: Boolean = true
)

class CompassSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _compassData = MutableStateFlow(
        CompassData(hasSensor = rotationSensor != null || (accelerometer != null && magnetometer != null))
    )
    val compassData: StateFlow<CompassData> = _compassData.asStateFlow()

    // Backward compatibility
    private val _heading = MutableStateFlow(0f)
    val heading: StateFlow<Float> = _heading.asStateFlow()

    private val _hasSensor = MutableStateFlow(rotationSensor != null || (accelerometer != null && magnetometer != null))
    val hasSensor: StateFlow<Boolean> = _hasSensor.asStateFlow()

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private var smoothedHeading = 0f
    private var smoothedPitch = 0f
    private var smoothedRoll = 0f
    private var isFirstReading = true
    private var currentAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH

    fun start() {
        if (sensorManager == null) return
        isFirstReading = true

        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
        } else {
            accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
            magnetometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.accuracy != 0) {
            currentAccuracy = event.accuracy
        }

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            processRotationMatrix(rotationMatrix)
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Low-pass filter for gravity vector
            val alphaAccel = 0.2f
            gravity[0] += alphaAccel * (event.values[0] - gravity[0])
            gravity[1] += alphaAccel * (event.values[1] - gravity[1])
            gravity[2] += alphaAccel * (event.values[2] - gravity[2])
            hasGravity = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            // Low-pass filter for geomagnetic vector
            val alphaMag = 0.2f
            geomagnetic[0] += alphaMag * (event.values[0] - geomagnetic[0])
            geomagnetic[1] += alphaMag * (event.values[1] - geomagnetic[1])
            geomagnetic[2] += alphaMag * (event.values[2] - geomagnetic[2])
            hasGeomagnetic = true
        }

        if (hasGravity && hasGeomagnetic && rotationSensor == null) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                processRotationMatrix(r)
            }
        }
    }

    private fun processRotationMatrix(rotationMatrix: FloatArray) {
        val orientation = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientation)
        val rawPitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
        val rawRoll = Math.toDegrees(orientation[2].toDouble()).toFloat()

        // Remap coordinate system when held tilted up in front of user face
        val finalOrientation = FloatArray(3)
        if (abs(rawPitch) > 50f) {
            val remappedMatrix = FloatArray(9)
            SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z,
                remappedMatrix
            )
            SensorManager.getOrientation(remappedMatrix, finalOrientation)
        } else {
            SensorManager.getOrientation(rotationMatrix, finalOrientation)
        }

        var rawAzimuth = Math.toDegrees(finalOrientation[0].toDouble()).toFloat()
        if (rawAzimuth < 0) rawAzimuth += 360f

        if (isFirstReading) {
            smoothedHeading = rawAzimuth
            smoothedPitch = rawPitch
            smoothedRoll = rawRoll
            isFirstReading = false
        } else {
            // Angular distance calculation across 0/360 boundary
            var delta = (rawAzimuth - smoothedHeading) % 360f
            if (delta < -180f) delta += 360f
            if (delta > 180f) delta -= 360f

            // Dynamic filter coefficient: responsive for fast rotations, stable against jitter
            val alpha = if (abs(delta) > 15f) 0.35f else if (abs(delta) > 4f) 0.22f else 0.12f
            smoothedHeading = (smoothedHeading + delta * alpha + 360f) % 360f

            smoothedPitch += 0.2f * (rawPitch - smoothedPitch)
            smoothedRoll += 0.2f * (rawRoll - smoothedRoll)
        }

        val isFlat = abs(smoothedPitch) < 18f && abs(smoothedRoll) < 18f

        _heading.value = smoothedHeading
        _compassData.value = CompassData(
            heading = smoothedHeading,
            pitch = smoothedPitch,
            roll = smoothedRoll,
            isFlat = isFlat,
            accuracy = currentAccuracy,
            hasSensor = true
        )
    }

    fun setManualHeading(headingDeg: Float) {
        val norm = ((headingDeg % 360f) + 360f) % 360f
        smoothedHeading = norm
        _heading.value = norm
        _compassData.value = _compassData.value.copy(
            heading = norm,
            isFlat = true,
            hasSensor = true
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        currentAccuracy = accuracy
        _compassData.value = _compassData.value.copy(accuracy = accuracy)
    }
}
