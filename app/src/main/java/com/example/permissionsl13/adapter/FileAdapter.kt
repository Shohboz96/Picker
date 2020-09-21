package com.example.permissionsl13.adapter

import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.permissionsl13.R
import com.example.permissionsl13.model.FileData
import com.example.permissionsl13.utils.SingleBlock
import com.example.permissionsl13.utils.bindItem
import com.example.permissionsl13.utils.inflate
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.File

class FileAdapter : ListAdapter<FileData,FileAdapter.VHolder>(FileData.ITEM_CALLBACK){

    private var listener:SingleBlock<FileData>? = null

    fun setOnRemoveListener(block: SingleBlock<FileData>){
        listener = block
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(parent.inflate(R.layout.item_file))

    override fun onBindViewHolder(holder: VHolder, position: Int)  = holder.bind()

    inner class VHolder(view:View):RecyclerView.ViewHolder(view){

        init {
            itemView.btn_file_remove.setOnClickListener {
                listener?.invoke(getItem(adapterPosition))
            }
        }
        fun bind() = bindItem {
            val d = getItem(adapterPosition)
            val path = d.path
            val f = File(path)
            text_file_name.text = f.name

            val  mimeTypeMap = MimeTypeMap.getSingleton().getMimeTypeFromExtension(path)

            val image = when{

                (mimeTypeMap ?: "").startsWith("video") -> R.drawable.ic_baseline_slow_motion_video_24
                (mimeTypeMap ?: "").startsWith("image") -> R.drawable.ic_baseline_image_24
                path.endsWith(".pdf") -> R.drawable.ic_file_pdf
                path.endsWith(".doc") -> R.drawable.ic_baseline_text_format_24
                path.endsWith(".docx") -> R.drawable.ic_baseline_text_format_24
                path.endsWith(".txt") -> R.drawable.ic_baseline_text_format_24
                path.endsWith(".apk") -> R.drawable.ic_baseline_android_24
                path.endsWith(".mp3") -> R.drawable.ic_file_music
                path.endsWith(".mp4") -> R.drawable.ic_baseline_videocam_24
                path.endsWith(".m4a") -> R.drawable.ic_file_music
                path.endsWith(".jpg") -> R.drawable.ic_baseline_image_24
                else ->{R.drawable.ic_baseline_attach_file_24}
            }
                btn_file_type.setImageResource(image)

        }
    }
}