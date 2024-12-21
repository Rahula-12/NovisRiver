package com.learning.novisriverinterview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.learning.novisriverinterview.adapter.EmployeeAdapter
import com.learning.novisriverinterview.contentprovider.JsonFileObserver
import com.learning.novisriverinterview.model.Employee
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

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

        viewmodel = ViewModelProvider(this)[EmployeeViewmodel::class.java]

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

    private fun parseJsonFile(json: String): List<Employee> {
        return Gson().fromJson(json, Array<Employee>::class.java).toList()
    }

}

// /storage/emulated/0/Android/data/com.learning.novisriverinterview/files/data.json
// name:String, id:Int, profileUrl:String
// circle profile image