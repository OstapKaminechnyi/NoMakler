package com.openclassrooms.nomakler.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import com.openclassrooms.nomakler.R
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*


@Parcelize
@Entity
data class Property(
        @PrimaryKey var pid: String, // Property ID generated randomly
        var type: String, // Type (apartment, loft, mansion, etc...)
        var location: String, // Location identifier (City, neighbourhood...)
        var geopoint: @RawValue GeoPoint,
        var address: String, // Full address of the property
        var price: Int, // Price (in UAH)
        var surface: Int, // Surface (in square meters)
        var roomsCount: Int, // Rooms count
        var description: String, // Full description of the property
        var picturesList: List<String>, // List of pictures urls
        var status: Boolean, // Status (True is Оренда, False is Продаж)
        var entryDate: Date, // Date of entry on the market
        var saleDate: Date, // Date of update, refactored
        var agent: String, // Full name of the real estate agent in charge of this property
        var number: String
        ) : Parcelable {
    constructor() : this("", "", "", GeoPoint(.0, .0),"", 0, 0, 0, "", arrayListOf<String>(), false, Calendar.getInstance().time, Date(), "","")
    fun getStateStringId(): Int {
        return if(status){
            R.string.available
        } else {
            R.string.unavailable
        }
    }
    fun getAllParams(): List<Any> {
        return listOf(type, location, geopoint, address, price, surface, roomsCount, description, picturesList, status, entryDate, saleDate, agent,number)
    }
    fun toHashMap() : HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["type"] = type
        map["location"] = location
        map["geopoint"] = geopoint
        map["address"] = address
        map["price"] = price
        map["surface"] = surface
        map["roomsCount"] = roomsCount
        map["description"] = description
        map["picturesList"] = picturesList
        map["status"] = status
        map["entryDate"] = entryDate
        map["saleDate"] = saleDate
        return map
    }
}