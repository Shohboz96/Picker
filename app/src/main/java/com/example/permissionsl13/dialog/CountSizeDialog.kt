package com.example.permissionsl13.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.permissionsl13.R
import kotlinx.android.synthetic.main.item_dialog.*
import kotlinx.android.synthetic.main.item_dialog.view.*

class CountSizeDialog(context: Context, count:Int, size : Int) : AlertDialog(context){
    private val countdownView = LayoutInflater.from(context).inflate(R.layout.item_dialog,null,false)
    private var listener:((Int, Int) -> Unit)? = null

    init {
        setView(countdownView)
        countdownView.item_dialog_count.setText(count.toString())
        countdownView.item_dialog_size.setText(size.toString())

        countdownView.btn_item_ok.setOnClickListener {
            val count1 = item_dialog_count.text.toString()
            val size1 = item_dialog_size.text.toString()
            if(count1.isEmpty()){
                item_dialog_count.error = "Enter Count"
                return@setOnClickListener
            }
            val countInt = count1.toInt()

            if(size1.isEmpty()){
                item_dialog_size.error = "Enter Size"
                return@setOnClickListener
            }
            val sizeInt = size1.toInt()

            listener?.invoke(countInt,sizeInt)
            dismiss()
        }

        countdownView.btn_item_cancel.setOnClickListener { dismiss() }
    }


    fun setOnClickListener(block:(Int, Int) -> Unit){
        listener = block
    }
}