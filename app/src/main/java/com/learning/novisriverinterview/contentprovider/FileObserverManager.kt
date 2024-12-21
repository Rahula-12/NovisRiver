package com.learning.novisriverinterview.contentprovider

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class FileObserverManager(
    private val context: Context,
    private val contentUri: Uri,
    private val onFileChanged: (String?) -> Unit
) {
    private var contentObserver: ContentObserver? = null

    fun registerObserver(lifecycleOwner: LifecycleOwner) {
        contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                val fileContent = readDataFromContentProvider()
                onFileChanged(fileContent)
            }
        }
        context.contentResolver.registerContentObserver(contentUri, true, contentObserver!!)
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    unregisterObserver()
                }
            }
        })
    }

    private fun readDataFromContentProvider(): String? {
        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val data = it.getString(it.getColumnIndexOrThrow(MyFileProvider.COLUMN_DATA))
                return data
            }
        }
        return null
    }

    private fun unregisterObserver() {
        contentObserver?.let {
            context.contentResolver.unregisterContentObserver(it)
        }
        contentObserver = null
    }
}