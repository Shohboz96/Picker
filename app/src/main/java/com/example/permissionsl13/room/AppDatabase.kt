package com.example.permissionsl13.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.permissionsl13.app.App
import com.example.permissionsl13.model.FileData
import com.example.permissionsl13.room.dao.FileDao

@Database(entities = [FileData::class],version = 1)
abstract class AppDatabase :RoomDatabase(){
    abstract fun fileDao():FileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    App.instance,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}