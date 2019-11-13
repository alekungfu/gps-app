package com.example.myfirstapp

import android.Manifest.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    val REQUEST_CODE = 1000

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when(requestCode) {
                REQUEST_CODE->{
                    if(grantResults.size > 0){
                        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                            Toast.makeText(this@MainActivity, "Permission granted", Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check permission
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, arrayOf(permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        else
        {
            buildLocationRequest()
            buildLocationCallback()

            // Create FuseProviderClient
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            // Set event
            btn_start_updates.setOnClickListener(View.OnClickListener {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE
                    )
                    return@OnClickListener
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )

                // Change state of button
                btn_start_updates.isEnabled = !btn_start_updates.isEnabled
                btn_stop_updates.isEnabled = !btn_stop_updates.isEnabled
            })

            btn_stop_updates.setOnClickListener(View.OnClickListener {
                if(ActivityCompat.checkSelfPermission(this@MainActivity, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this@MainActivity, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                    return@OnClickListener
                }
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)

                // Change state of button
                btn_start_updates.isEnabled = !btn_start_updates.isEnabled
                btn_stop_updates.isEnabled = !btn_stop_updates.isEnabled

            })
        }
    }

    private fun buildLocationCallback() {
        locationCallback = object :LocationCallback() {

            // Ctrl+O
            override fun onLocationResult(p0: LocationResult?) {
                var location = p0!!.locations.get(p0!!.locations.size-1)
                txt_location.text = location.latitude.toString() + "/" + location.longitude.toString()
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    /** Callled when the user taps the Send button */
    fun sendMessage(view: View) {
        val editText = findViewById<EditText>(R.id.editText)
        val message = editText.text.toString()
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }
}
