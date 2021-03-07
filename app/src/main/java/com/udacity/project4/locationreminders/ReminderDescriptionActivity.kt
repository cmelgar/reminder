package com.udacity.project4.locationreminders

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.REQUEST_LOCATION_PERMISSION

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var reminderDataItem: ReminderDataItem

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )

        reminderDataItem = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem

        binding.reminderDataItem = reminderDataItem

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val latitude = reminderDataItem.latitude!!
        val longitude = reminderDataItem.longitude!!
        val zoomLevel = 18f

        val homeLatLng = LatLng(latitude, longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))

        setPoiClick(map)
        setMapStyle(map)
        enableMyLocation()

        Log.i("ReminderDescription","Map initialized")
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                    MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name)
            )

            poiMarker.showInfoWindow()
        }
    }


    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this,
                            R.raw.retro
                    )
            )

            if (!success) {
                Log.e("setMapStyle", "Style parsing failed.")
            }
        } catch (ex: Resources.NotFoundException) {
            Log.e("setMapStyle", "Can't find style. Error: ", ex)
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.setMyLocationEnabled(true)
        }
        else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
