package com.example.permissionsl13.model

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FileData(
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0,
    var type:Int = 0,
    var path:String = "",
    var hide: Boolean = false
){


    companion object{
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<FileData>(){
            override fun areItemsTheSame(oldItem: FileData, newItem: FileData) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: FileData, newItem: FileData) = oldItem.path == newItem.path

        }
    }
}