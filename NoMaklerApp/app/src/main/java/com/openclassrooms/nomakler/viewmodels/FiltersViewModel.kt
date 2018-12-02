package com.openclassrooms.nomakler.viewmodels

import android.app.Application
import android.arch.lifecycle.*
import com.openclassrooms.nomakler.models.PropertyFilter


class FiltersViewModel(application: Application) : AndroidViewModel(application) {
    var filter = PropertyFilter()
}