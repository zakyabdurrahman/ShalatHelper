package com.martabak.shalathelper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private var mutableCoordinate : MutableLiveData<Coordinate> = MutableLiveData<Coordinate>()


    fun updateCoordinate(coordinate : Coordinate) {
        mutableCoordinate.value = coordinate
    }

    val liveCoordinate : LiveData<Coordinate> get() = mutableCoordinate




}