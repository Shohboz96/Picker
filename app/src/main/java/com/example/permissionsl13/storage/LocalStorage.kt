package com.example.permissionsl13.storage

import android.content.Context

class LocalStorage private constructor(context: Context) {
    companion object {
        lateinit var instance: LocalStorage; private set

        fun init(context: Context) {
            instance =
                LocalStorage(
                    context
                )
        }
    }

    private val pref = context.getSharedPreferences("LocalStorage", Context.MODE_PRIVATE)
//    private val pref = SecurePreferences(context, "55555", "LocalStorage")

    var countMedia: Int by IntPreference(pref)
    var sizeMedia: Float by FloatPreference(pref)

    var countFile: Int by IntPreference(pref)
    var sizeFile: Float by FloatPreference(pref)

    var dayNight:Boolean by BooleanPreference(pref)

    fun clear() {
        pref.edit().clear().apply()
    }
}