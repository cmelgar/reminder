package com.udacity.project4.locationreminders.geofence

import android.content.Context
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R

fun errorMessage(context: Context, errorCode: Int): String {
    val resources = context.resources
    return when (errorCode) {
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> resources.getString(R.string.geofence_not_available)
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> resources.getString(R.string.geofence_too_many_geofences)
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> resources.getString(R.string.geofence_too_many_pending_intents)
        else -> resources.getString(R.string.geofence_unknown_error)
    }
}

data class LandmarkDataObject(val id: String, val name: Int, val latlng: LatLng)

internal object GeofencingConstants {
    val LANDMARK_DATA:Array<LandmarkDataObject> = arrayOf()

    val NUM_LANDMARKS = LANDMARK_DATA.size
    const val GEOFENCE_RADIUS_IN_METERS = 100f
    const val EXTRA_GEOFENCE_INDEX = "GEOFENCE_INDEX"
}