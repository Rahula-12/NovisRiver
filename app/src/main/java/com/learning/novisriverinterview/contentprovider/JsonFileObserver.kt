package com.learning.novisriverinterview.contentprovider

import android.os.FileObserver

class JsonFileObserver(path: String,private val onFileChanged:()->Unit) : FileObserver(path, CLOSE_WRITE) {
    override fun onEvent(event: Int, path: String?) {
        if (event == CLOSE_WRITE) {
//             File has been modified
            onFileChanged()
        }
    }
}