package com.example.ricardo.kotlinapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

private const val PERMISSION_REQUEST = 10

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var mSensorManager : SensorManager?= null
    lateinit var mLocationManager : LocationManager
    private var hasNetwork = false
    private var hasGps = false
    private var locationNetwork : Location?= null
    private var locationGps : Location?= null

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }

        getLocation()
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER){
            getAccelerometer(event)
        }
    }

    private fun getAccelerometer(event: SensorEvent) {
        val xAxis = event.values[0]
        val yAxis = event.values[1]
        val zAxis = event.values[2]

        text_x.text = "X: ".plus(xAxis.toString())
        text_y.text = "Y: ".plus(yAxis.toString())
        text_z.text = "Z: ".plus(zAxis.toString())
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(hasGps || hasNetwork){
            if(hasGps) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            text_latitude.text = "Latitude: ".plus(locationGps!!.latitude.toString())
                            text_longitude.text = "Latitude: ".plus(locationGps!!.longitude.toString())

                            Log.d("CodeAndroidLocation", " GPS Latitude : " + locationGps!!.latitude)
                            Log.d("CodeAndroidLocation", " GPS Longitude : " + locationGps!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                var localGpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation

            }

            if(hasNetwork) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0f, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            text_latitude.text = "Latitude: ".plus(locationNetwork!!.latitude.toString())
                            text_longitude.text = "Latitude: ".plus(locationNetwork!!.longitude.toString())

                            Log.d("CodeAndroidLocation", " GPS Latitude : " + locationNetwork!!.latitude)
                            Log.d("CodeAndroidLocation", " GPS Longitude : " + locationNetwork!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                var localNetworkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps != null && locationNetwork != null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    text_latitude.text = "Latitude: ".plus(locationGps!!.latitude.toString())
                    text_longitude.text = "Latitude: ".plus(locationGps!!.longitude.toString())

                    Log.d("CodeAndroidLocation", " GPS Latitude : " + locationGps!!.latitude)
                    Log.d("CodeAndroidLocation", " GPS Longitude : " + locationGps!!.longitude)
                }
                else{
                    text_latitude.text = "Latitude: ".plus(locationNetwork!!.latitude.toString())
                    text_longitude.text = "Latitude: ".plus(locationNetwork!!.longitude.toString())

                    Log.d("CodeAndroidLocation", " GPS Latitude : " + locationNetwork!!.latitude)
                    Log.d("CodeAndroidLocation", " GPS Longitude : " + locationNetwork!!.longitude)
                }
            }



        }
        else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    override fun onResume(){
        super.onResume()
        mSensorManager!!.registerListener(this,mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),20000)
    }

    override fun onPause(){
        super.onPause()
        mSensorManager!!.unregisterListener(this)
    }

    fun sendMessage(view : View) : Unit {
        val intent = Intent(this, DisplayMessageActivity::class.java)
        val editText : EditText = findViewById<EditText>(R.id.editText)
        val message : String = editText.text.toString()
        intent.putExtra(EXTRA_MESSAGE, message)
        startActivity(intent)
    }
    companion object {
        val EXTRA_MESSAGE = "com.example.ricardo.kotlinapp.MESSAGE"
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
}
