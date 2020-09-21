package com.example.permissionsl13.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.permissionsl13.model.FileData

@Dao
interface FileDao :BaseDao<FileData>{
    @Query("select * from FileData where type = 0")
    fun getAllMedia():List<FileData>

    @Query("select * from FileData where type = 1")
    fun getAllFile():List<FileData>
}