package com.example.permissionsl13.app

import android.app.Application
import com.example.permissionsl13.storage.LocalStorage

class App :Application(){

    override fun onCreate() {
        super.onCreate()
        instance = this

        LocalStorage.init(this)
    }

    companion object{
        lateinit var instance :App
    }
}