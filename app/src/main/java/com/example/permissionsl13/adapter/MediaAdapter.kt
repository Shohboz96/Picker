package com.example.permissionsl13.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.permissionsl13.R
import com.example.permissionsl13.model.FileData
import com.example.permissionsl13.utils.SingleBlock
import com.example.permissionsl13.utils.bindItem
import com.example.permissionsl13.utils.inflate
import kotlinx.android.synthetic.main.item_media.view.*

class MediaAdapter : ListAdapter<FileData, MediaAdapter.VHolder>(FileData.ITEM_CALLBACK){

    private var listenerAdd:(() -> Unit)? = null
    private var listenerRemove:SingleBlock<FileData>? = null

    fun setOnRemoveListener(block: SingleBlock<FileData>){
        listenerRemove = block
    }
    fun setOnItemAddListener(block: (() -> Unit)?){
        listenerAdd = block
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(parent.inflate(R.layout.item_media))

    override fun onBindViewHolder(holder: VHolder, position: Int)  = holder.bind()

    inner class VHolder(view: View): RecyclerView.ViewHolder(view){

        init {
            itemView.item_image?.setOnClickListener {
                if(adapterPosition == currentList.size - 1){
                    listenerAdd?.invoke()
                }
            }
            itemView.item_delete?.apply {
                setOnClickListener {
                    listenerRemove?.invoke(getItem(adapterPosition))
                }
            }
        }
        fun bind() = bindItem {
            if(adapterPosition != currentList.size - 1){
                layout.visibility = View.VISIBLE
                item_delete.visibility = View.VISIBLE

                val d = getItem(adapterPosition)
                val path = d.path

                Glide.with(context).load(path).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .centerCrop().into(item_image)
            }else{
                item_delete.visibility = View.GONE
                if(currentList[adapterPosition].hide){
                    layout.visibility = View.GONE
                }
            }


        }
    }
}