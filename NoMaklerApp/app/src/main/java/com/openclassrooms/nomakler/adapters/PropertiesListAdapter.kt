package com.openclassrooms.nomakler.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.openclassrooms.nomakler.models.PropertyFilter
import com.openclassrooms.nomakler.R
import com.openclassrooms.nomakler.activities.MainActivity
import com.openclassrooms.nomakler.fragments.PropertyFragment
import com.openclassrooms.nomakler.models.Property
import java.lang.Exception




/** Custom adapter for the workmates RecyclerView */
open class PropertiesListAdapter(context: Context, resource: Int, list: ArrayList<Property>) : RecyclerView.Adapter<PropertiesListAdapter.ViewHolder>() {

    private var mContext = context
    private var mResource = resource
    private var mList = list
    private var mFilteredList = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_property, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Item (row)
        val item = mFilteredList[position]

        // Property image
        if(item.picturesList.isNotEmpty()){
            Glide.with(mContext).load(item.picturesList[0]).into(holder.itemView.findViewById(R.id.prop_image))
        }

        // Type
        holder.typeTextView.text = item.type

        // Location
        holder.locationTextView.text = item.location

        // Status
        if(item.status){
            holder.statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_dot_green))
        } else {
            holder.statusImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_dot_red))
        }
        
        // Price
        holder.priceTextView.text = mContext.getString(R.string.price_tag, item.price)
        
        // Item click listener
        holder.itemView.setOnClickListener {
            try {
                val mainActivity = mContext as MainActivity
                mainActivity.displayFragment(PropertyFragment.newInstance(item.pid))
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

    }

    /** Sets the list to a filtered list based on the filter and original list provided */
    fun filter(originalList: ArrayList<Property>, filter: PropertyFilter){
        // Resetting the filtered list as a copy of the original
        mFilteredList.clear()
        mFilteredList.addAll(originalList)

        // TODO : Find a cleaner way to apply those filters

        // Availability filter
        if(filter.availability.value != R.id.search_radio_all){
            mFilteredList.removeAll(mFilteredList.filter { it.status == (filter.availability.value == R.id.search_radio_unavailable) })
        }

        // Type filter
        if(filter.type.value != null && filter.type.value != ""){
            mFilteredList.removeAll(mFilteredList.filter { !it.type.contains(filter.type.value!!, true)})
        }

        // Location filter
        if(filter.location.value != null && filter.location.value != ""){
            mFilteredList.removeAll(mFilteredList.filter { !it.location.contains(filter.location.value!!, true)})
        }

        // Price filter
        mFilteredList.removeAll(mFilteredList.filter { it.price < filter.lowPrice.value!! || it.price > filter.highPrice.value!! })

        // Surface filter
        mFilteredList.removeAll(mFilteredList.filter { it.surface < filter.lowSurface.value!! || it.surface > filter.highSurface.value!! })

        // Rooms filter
        mFilteredList.removeAll(mFilteredList.filter { it.roomsCount < filter.lowRooms.value!! || it.roomsCount > filter.highRooms.value!! })

        // Pictures filter
        mFilteredList.removeAll(mFilteredList.filter { it.picturesList.size < filter.lowPictures.value!! || it.picturesList.size > filter.highPictures.value!! })

        // Entry date filter
        if(filter.entryDate?.value != null){
            mFilteredList.removeAll(mFilteredList.filter { it.entryDate.before(filter.entryDate!!.value) })
        }

        // Sale date filter
        if(filter.saleDate?.value != null){
            mFilteredList.removeAll(mFilteredList.filter { it.saleDate.before(filter.saleDate!!.value) })
        }

        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeTextView = itemView.findViewById<TextView>(R.id.prop_type)!!
        val locationTextView = itemView.findViewById<TextView>(R.id.prop_location)!!
        val priceTextView = itemView.findViewById<TextView>(R.id.prop_price)!!
        val statusImageView = itemView.findViewById<ImageView>(R.id.prop_status)!!
    }

}