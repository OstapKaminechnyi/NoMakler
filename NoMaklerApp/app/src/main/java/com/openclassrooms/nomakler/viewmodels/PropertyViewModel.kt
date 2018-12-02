package com.openclassrooms.nomakler.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.openclassrooms.nomakler.models.Property


class PropertyViewModel : ViewModel() {
    var property = MutableLiveData<Property>()
}