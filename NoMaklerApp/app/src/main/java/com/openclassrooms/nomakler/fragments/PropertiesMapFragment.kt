package com.openclassrooms.nomakler.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.nomakler.utils.Utils
import com.openclassrooms.nomakler.activities.MainActivity
import com.openclassrooms.nomakler.models.Property

class PropertiesMapFragment : SupportMapFragment() {
    companion object {
        val TAG: String = PropertiesMapFragment::class.java.simpleName

        fun newInstance(propertyPid: String): PropertiesMapFragment {
            val myFragment = PropertiesMapFragment()
            val args = Bundle()
            args.putString(PropertyFragment.PID_KEY, propertyPid)
            myFragment.arguments = args
            return myFragment
        }
    }


    // Camera position (initialized by default to show NYC)
    val cameraLatLng = LatLng(49.843629, 24.026517)

    // Default zoom level
    val defaultZoom = 8f

    // Default zoom level when showing a marker
    val markerZoom = 16f

    // Markers list
    val markers = mutableMapOf<Marker, Property>()

    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)
        if(context != null) {
            // Get property pid from the arguments
            val pid = arguments?.getString(PropertyFragment.PID_KEY)

            this.getMapAsync { map ->
                // Set default if no property is focused
                if (pid == null) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLatLng, defaultZoom))
                }
                // Check permissions
                if (context != null && ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Enables location button
                    map.isMyLocationEnabled = true
                    map.uiSettings.isMyLocationButtonEnabled = true
                    // Enables map toolbar
                    map.uiSettings.isMapToolbarEnabled = true
                    // Enables defaultZoom buttons
                    map.uiSettings.isZoomControlsEnabled = true

                    // Download data from Firestore
                    MainActivity.colRef.get().addOnCompleteListener {querySnapshot ->

                        // For each document in properties list
                        for(it: Property in Utils.documentsToPropertyList(querySnapshot.result.documents)) {
                            // Marker set-up
                            val marker = map.addMarker(MarkerOptions()
                                    .position(Utils.geoPointToLatLng(it.geopoint))
                                    .title(it.type + " - " + it.location)
                                    .snippet("$" + it.price + " - " + getString(it.getStateStringId())))

                            // Add marker to the list
                            markers[marker] = it

                            // If the property should be focused
                            if (pid != null && it.pid == pid) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(Utils.geoPointToLatLng(it.geopoint), markerZoom))
                            }
                        }
                    }
                    // Markers info click listener
                    map.setOnInfoWindowClickListener {
                        (context as MainActivity).displayFragment(PropertyFragment.newInstance(markers[it]?.pid!!))
                    }
                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
                }

            }
        }
    }
}
