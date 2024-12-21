package com.learning.novisriverinterview

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.learning.novisriverinterview.adapter.EmployeeAdapter
import com.learning.novisriverinterview.contentprovider.JsonFileObserver
import com.learning.novisriverinterview.contentprovider.MyFileProvider
import com.learning.novisriverinterview.model.Employee
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var viewmodel: EmployeeViewmodel
    lateinit var fileObserver: JsonFileObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkAndRequestPermissions()

        viewmodel = ViewModelProvider(this)[EmployeeViewmodel::class.java]

        // Using app-specific directory for the file
        val filePath = "${getExternalFilesDir(null)}/data.json"
        fileObserver = JsonFileObserver(filePath) {
            viewmodel.deleteAllEmployees()
            val data = parseJsonFile(File(filePath).readText())
            data.forEach { employee ->
                viewmodel.insertEmployee(employee)
            }
        }
        fileObserver.startWatching()

        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            delay(5000)
            withContext(Dispatchers.IO) {
                viewmodel.deleteAllEmployees()
                // Read data from Content Provider
                val data = readFileFromContentProvider(this@MainActivity, MyFileProvider.CONTENT_URI)
                val list = parseJsonFile(data)
                list.forEach { employee ->
                    viewmodel.insertEmployee(employee)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fileObserver.stopWatching()
    }

    private fun setupRecyclerView() {
        val employeelist = findViewById<RecyclerView>(R.id.list_of_employees)
        val adapter = EmployeeAdapter()
        employeelist.adapter = adapter
        employeelist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewmodel.employeeList.observe(this) {
            adapter.submitData(this.lifecycle, it)
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                100
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun parseJsonFile(json: String): List<Employee> {
        return Gson().fromJson(json, Array<Employee>::class.java).toList()
    }

    private fun readFileFromContentProvider(context: Context, uri: Uri): String {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val content = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            content.append(line).append('\n')
        }

        reader.close()
        inputStream?.close()

        return content.toString()
    }
}

// /storage/emulated/0/Android/data/com.learning.novisriverinterview/files/data.json
// name:String, id:Int, profileUrl:String
// circle profile image