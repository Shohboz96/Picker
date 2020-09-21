package com.example.permissionsl13.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.permissionsl13.R
import com.example.permissionsl13.adapter.FileAdapter
import com.example.permissionsl13.adapter.MediaAdapter
import com.example.permissionsl13.dialog.CountSizeDialog
import com.example.permissionsl13.model.FileData
import com.example.permissionsl13.room.AppDatabase
import com.example.permissionsl13.storage.LocalStorage
import com.example.permissionsl13.utils.PathUtil
import com.example.permissionsl13.utils.checkPermission
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val fileAdapter = FileAdapter()
    private val mediaAdapter = MediaAdapter()
    private val room = AppDatabase.getDatabase().fileDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val storage = LocalStorage.instance


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.WHITE

        list_media.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        list_media.adapter = mediaAdapter

        list_file.layoutManager = LinearLayoutManager(this)
        list_file.adapter = fileAdapter

        runOnWorkerThread {
            val ls = room.getAllMedia()
            runOnUiThread {
                
                media_max_count.text = storage.countMedia.toString()
                media_max_size.text = storage.sizeMedia.toString() + " MB"
                getTotalSizeString(ls) {
                    media_size.text = it
                }
                media_count.text = ls.size.toString()
                val l = ls.toMutableList()
                l.add(FileData(hide = storage.countMedia == ls.size))
                mediaAdapter.submitList(l)
            }
        }
        runOnWorkerThread {
            val ls = room.getAllFile()
            runOnUiThread {
                file_max_count.text = storage.countFile.toString()
                file_max_size.text = storage.sizeFile.toString() + " MB"
                getTotalSizeString(ls) {
                    file_size.text = it
                }
                file_count.text = ls.size.toString()
                fileAdapter.submitList(ls)
            }
        }

        fileAdapter.setOnRemoveListener {
            runOnWorkerThread {
                room.delete(it)
                val ls = room.getAllFile()
                runOnUiThread {
                    file_count.text = ls.size.toString()
                    getTotalSizeString(ls) { s ->
                        file_size.text = s
                    }
                }
            }
            val ls = fileAdapter.currentList.toMutableList()
            ls.remove(it)
            fileAdapter.submitList(ls)
        }

        mediaAdapter.setOnRemoveListener {
            runOnWorkerThread {
                room.delete(it)
                val ls = room.getAllMedia()
                runOnUiThread {
                    media_count.text = ls.size.toString()
                    getTotalSizeString(ls) { s ->
                        media_size.text = s
                    }
                    val l = ls.toMutableList()
                    l.add(FileData(hide = storage.countMedia == ls.size))
                    mediaAdapter.submitList(l)
                }
            }
        }

        setting_media.setOnClickListener {
            val dialog = CountSizeDialog(this, storage.countMedia, storage.sizeMedia.toInt())
            dialog.setOnClickListener { count, size ->
                storage.countMedia = count
                media_max_count.text = count.toString()

                storage.sizeMedia = size.toFloat()
                media_max_size.text = size.toFloat().toString() + " MB"

                runOnWorkerThread {
                    val ls = room.getAllMedia()
                    runOnUiThread {
                        val l = ls.toMutableList()
                        l.add(FileData(hide = storage.countMedia == ls.size))
                        mediaAdapter.submitList(l)
                    }
                }
            }

            dialog.show()
        }
        setting_file.setOnClickListener {

            val dialog = CountSizeDialog(this, storage.countFile, storage.sizeFile.toInt())
            dialog.setOnClickListener { count, size ->
                storage.countFile = count
                file_max_count.text = count.toString()

                storage.sizeFile = size.toFloat()
                file_max_size.text = size.toFloat().toString() + " MB"
            }
            dialog.show()
        }


        mediaAdapter.setOnItemAddListener {
            if (media_count.text.toString().toInt() == media_max_count.text.toString().toInt()) {
                showMessage("Siz maximal image yuklab bo'lgansiz")
                return@setOnItemAddListener
            }
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                openMediaGallery()
            }
        }

        btn_file.setOnClickListener {
            if (file_count.text.toString().toInt() == file_max_count.text.toString().toInt()) {
                showMessage("Siz maximal file yuklab bo'lgansiz")
                return@setOnClickListener
            }
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                openFileGallery()
            }
        }
    }

    private fun getTotalSizeString(ls: List<FileData>, block: (String) -> Unit) {

        runOnWorkerThread {
            var size = 0f
            ls.forEach {
                val path = it.path
                val file = File(path)
                size += file.length()
            }

            val m = (size / 1024) / 1024
            val dec = DecimalFormat("0.00")
            runOnUiThread {
                val s = if (m > 1) {
                    dec.format(m).plus(" MB")
                } else {
                    dec.format(size / 1024).plus(" KB")
                }
                block(s)
            }
        }
    }

    private fun openMediaGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)

    }

    private fun openFileGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            val path = PathUtil.getPath(this, uri)

            runOnWorkerThread {
                var ls = room.getAllMedia()
                getTotalSizeInMb(ls) {
                    val currentSize = it
                    val itemSize = getSizeInMb(path)
                    if (currentSize + itemSize <= storage.sizeMedia) {
                        val f = FileData(path = path)
                        val id = room.insert(f)
                        f.id = id
                        ls = room.getAllMedia()
                        runOnUiThread {
                            media_count.text = ls.size.toString()
                            getTotalSizeString(ls) { s ->
                                media_size.text = s
                            }

                            val l = ls.toMutableList()
                            l.add(FileData(hide = storage.countMedia == ls.size))
                            mediaAdapter.submitList(l)
                        }
                    } else {
                        runOnUiThread {
                            showMessage("Tanlangan file xotiraga sig'maydi: max size: ${storage.sizeMedia} MB!")
                        }
                    }
                }
            }
        }


        if (requestCode == 2 && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            val path = PathUtil.getPath(this, uri)

            runOnWorkerThread {
                var ls = room.getAllFile()
                getTotalSizeInMb(ls) {
                    val currentSize = it
                    val itemSize = getSizeInMb(path)
                    if (currentSize + itemSize <= storage.sizeFile) {
                        val f = FileData(path = path, type = 1)
                        val id = room.insert(f)
                        f.id = id
                        val l = room.getAllFile()
                        runOnUiThread {
                            val adl = fileAdapter.currentList.toMutableList()
                            adl.add(f)
                            fileAdapter.submitList(adl)
                            file_count.text = adl.size.toString()
                            getTotalSizeString(l) { s ->
                                file_size.text = s
                            }
                        }
                    } else {
                        runOnUiThread { showMessage("Tanlangan file xotiraga sig'maydi: max size ${storage.sizeFile} MB!") }
                    }
                }
            }
        }
    }

    private fun getSizeInMb(path: String?): Float {
        val file = File(path)
        val size = file.length()
        return (size / 1024) / 1024f
    }

    private fun getTotalSizeInMb(ls: List<FileData>, function: (Float) -> Unit) {
        runOnWorkerThread {
            var size = 0f
            ls.forEach {
                val path = it.path
                val file = File(path)
                size += file.length()
            }
            function((size / 1024) / 1024f)
        }
    }

    private fun showMessage(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun runOnWorkerThread(block: () -> Unit) {
        executor.execute(block)
    }
}