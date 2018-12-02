package com.openclassrooms.nomakler.fragments


import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.dmallcott.dismissibleimageview.DismissibleImageView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.openclassrooms.nomakler.Manifest
import com.openclassrooms.nomakler.R
import com.openclassrooms.nomakler.R.id.*
import com.openclassrooms.nomakler.activities.MainActivity
import com.openclassrooms.nomakler.fragments.PropertiesMapFragment.Companion.TAG
import com.openclassrooms.nomakler.fragments.PropertyFragment.Companion.MAP_ZOOM
import com.openclassrooms.nomakler.models.Property
import com.openclassrooms.nomakler.utils.Utils.dateFormat
import kotlinx.android.synthetic.main.fragment_property.*
import java.io.IOException
import java.text.DateFormat



class PropertyFragment : Fragment() {

    companion object {
        /** Default zoom level for the embedded map */
        const val MAP_ZOOM = 20f

        /** Property id key for the instance save */
        const val PID_KEY = "propertyid"

        /** Returns a new instance of the fragment including the pid in arguments */
        fun newInstance(propertyPid: String): PropertyFragment {
            val myFragment = PropertyFragment()
            val args = Bundle()
            args.putString(PID_KEY, propertyPid)
            myFragment.arguments = args
            return myFragment
        }
    }

    /** DateFormat object */
    private lateinit var dateFormat: DateFormat

    /** Fragment root view */
    private lateinit var fragmentView: View

    /** Property */
    private lateinit var prop : Property

    /** Property id */
    private lateinit var pid: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //retainInstance = true

        dateFormat = android.text.format.DateFormat.getDateFormat(context?.applicationContext)
        return inflater.inflate(R.layout.fragment_property, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(PID_KEY, pid)
        super.onSaveInstanceState(outState)
    }

//    @OnClick(R.id.property_number)
//    fun onCallButtonClick() {
//        initCallIntent(prop.number)
//
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view

        if(savedInstanceState != null){
            // Recover pid from the saved instance
            pid = savedInstanceState.getString(PID_KEY)
        } else {
            // Get pid from the arguments
            pid = arguments?.getString(PID_KEY)!!
            arguments?.remove(PID_KEY)
        }

        // Get data from Firestore
        MainActivity.colRef.document(pid).addSnapshotListener { doc, _ ->
            if(doc != null){
                val property = doc.toObject(Property::class.java)
                if(property != null){
                    updateUIFromProperty(property)
                }
            }
        }
        // If clicking besides the fragment, close it
        property_overlay.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }



        // Close button listener
        property_close_button.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }



        fragmentView.findViewById<TextView>(R.id.property_number).setOnClickListener{
            fragmentView.findViewById<TextView>(R.id.property_number).text= prop.number
            var intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",prop.number,null) )
            startActivity(intent)
        }



    }

    /** Update UI based on a Property object */
    private fun updateUIFromProperty(prop: Property) {
        this.prop = prop
        if(::fragmentView.isLateinit){
            try {
                // Setting UI elements according to the property object
                fragmentView.findViewById<TextView>(R.id.property_location).text = prop.location
                fragmentView.findViewById<TextView>(R.id.property_type).text = prop.type
                fragmentView.findViewById<TextView>(R.id.property_address).text = prop.address
                fragmentView.findViewById<TextView>(R.id.property_desc).text = prop.description
                fragmentView.findViewById<TextView>(R.id.property_surface).text = getString(R.string.sqm_value, prop.surface)
                fragmentView.findViewById<TextView>(R.id.property_rooms).text = getString(R.string.rooms_count, prop.roomsCount)
                fragmentView.findViewById<TextView>(R.id.property_price).text = getString(R.string.price_tag, prop.price)
                fragmentView.findViewById<TextView>(R.id.property_entryDate).text = dateFormat.format(prop.entryDate)
                fragmentView.findViewById<TextView>(R.id.property_saleDate).text = dateFormat.format(prop.saleDate)
                fragmentView.findViewById<TextView>(R.id.property_agent).text = prop.agent
                fragmentView.findViewById<TextView>(R.id.property_number).text= prop.number
                // Status
                val statusView = fragmentView.findViewById<TextView>(R.id.property_status)
                if(prop.status){
                    statusView.text = this.getString(R.string.available)
                    statusView.setTextColor(Color.parseColor("#4caf50"))
                    property_saleDateTitle.visibility = View.GONE
                    property_saleDate.visibility = View.GONE
                } else {
                    statusView.text = this.getString(R.string.unavailable)
                    statusView.setTextColor(Color.RED)
                }

                // Pictures
                val picturesLayout = fragmentView.findViewById<LinearLayout>(R.id.pictures_layout)
                if(picturesLayout != null){
                    picturesLayout.removeAllViews()
                    for(url in prop.picturesList){
                        val img = DismissibleImageView(context)
                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 800)
                        //params.weight = 1.0f
                        //params.gravity = Gravity.FILL
                        params.setMargins(0, 0, 0, 0)
                        //img.layoutParams = params
                        picturesLayout.addView(img)
                        Glide.with(context!!).load(url).into(img)
                    }
                }

                // Map
                val mapFragment = childFragmentManager.findFragmentById(R.id.property_map) as? SupportMapFragment
                if(mapFragment != null){
                    if(prop.geopoint.latitude != 0.0){
                        setMapWithGeopoint(mapFragment, prop.geopoint)
                    } else {
                        setMapWithProperty(mapFragment, prop)
                    }
                }

            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    /** Get geopoint from address, set up map and upload geopoint into firestore */
    private fun setMapWithProperty(map: SupportMapFragment, prop: Property) {
        map.getMapAsync {
            val coder = Geocoder(context)
            val addresses: List<Address>?
            var latlng: LatLng? = null
            try {
                addresses = coder.getFromLocationName(prop.address, 5)
                if (addresses != null && addresses.isNotEmpty()) {
                    latlng = LatLng(addresses[0].latitude, addresses[0].longitude)
                    val geoPoint = GeoPoint(addresses[0].latitude, addresses[0].longitude)
                    setUpMap(it, latlng)
                    FirebaseFirestore.getInstance().collection("properties").document(prop.pid).update("geopoint", geoPoint)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /** Set up map with a geopoint */
    private fun setMapWithGeopoint(map: SupportMapFragment, geo: GeoPoint) {
        val latlng = LatLng(geo.latitude, geo.longitude)
        map.getMapAsync {
            setUpMap(it, latlng)
        }
    }

    /** Main map set-up */
    private fun setUpMap(map: GoogleMap, latlng: LatLng){
        map.addMarker(MarkerOptions().position(latlng))
        map.moveCamera(CameraUpdateFactory.newLatLng(latlng))
        map.setMinZoomPreference(MAP_ZOOM)
        map.setOnMapClickListener {
            displayPropertyMap(prop.pid)
        }
        map.setOnMarkerClickListener {
            displayPropertyMap(prop.pid)
            true
        }
    }

//    private fun initCallIntent(number: String) {
//        val callIntent = Intent(Intent.ACTION_CALL)
//        callIntent.data = Uri.parse("tel:$number")
//        if (ActivityCompat.checkSelfPermission(context!!,
//                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//        startActivity(callIntent)
//    }

    /** Show the property location in a PropertiesMapFragment */
    private fun displayPropertyMap(pid: String){
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        (activity as MainActivity).displayFragment(PropertiesMapFragment.newInstance(pid))
    }

}