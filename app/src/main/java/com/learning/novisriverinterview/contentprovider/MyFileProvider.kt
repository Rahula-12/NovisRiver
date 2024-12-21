package com.learning.novisriverinterview.contentprovider

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.FileObserver
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileNotFoundException

class MyFileProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.learning.novisriverinterview.contentprovider.fileprovider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/files/data.json")
        const val COLUMN_DATA = "data"
    }

    private var fileObserver: FileObserver? = null

    override fun onCreate(): Boolean {
        startFileObserver()
        return true
    }

    private fun startFileObserver() {
        val file = File(context?.getExternalFilesDir(null), "data.json")
        if (!file.exists()) return
        fileObserver = object : FileObserver(file.absolutePath, MODIFY or CREATE or DELETE) {
            override fun onEvent(event: Int, path: String?) {
                if (event == MODIFY || event == CREATE || event == DELETE) {
                    context?.contentResolver?.notifyChange(CONTENT_URI, null)
                }
            }
        }
        fileObserver?.startWatching()
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val file = File(context?.getExternalFilesDir(null), "data.json")
        if (!file.exists()) return null
        val data = file.readText()
        val cursor = MatrixCursor(arrayOf(COLUMN_DATA))
        cursor.addRow(arrayOf(data))
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.item/vnd.$AUTHORITY.file"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Insert operation not supported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Delete operation not supported")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("Update operation not supported")
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        if (uri != CONTENT_URI) throw FileNotFoundException("Invalid URI: $uri")
        val file = File(context?.getExternalFilesDir(null), "data.json")
        if (!file.exists()) throw FileNotFoundException("File not found")
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override fun shutdown() {
        fileObserver?.stopWatching()
        super.shutdown()
    }
}
