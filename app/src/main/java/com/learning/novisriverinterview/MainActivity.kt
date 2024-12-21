package com.learning.novisriverinterview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.learning.novisriverinterview.adapter.EmployeeAdapter
import com.learning.novisriverinterview.contentprovider.FileObserverManager
import com.learning.novisriverinterview.contentprovider.MyFileProvider
import com.learning.novisriverinterview.model.EmployeeList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var viewmodel: EmployeeViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewmodel=ViewModelProvider(this)[EmployeeViewmodel::class.java]
        val fileObserverManager = FileObserverManager(
            context = this,
            contentUri = MyFileProvider.CONTENT_URI,
            onFileChanged = { content ->
                // Handle file content
                lifecycleScope.launch {
                    content?.let {
                        viewmodel.deleteAllEmployees()
                        val list=GsonBuilder().create().fromJson(it, EmployeeList::class.java)
                        for(employee in list.employeeList) {
                            viewmodel.insertEmployee(employee)
                        }
                    }
                }
            }
        )
        fileObserverManager.registerObserver(this)
//        lifecycleScope.launch {
//            employeeRepository.insertEmployee(Employee("rahul",2,""))
//        }
        val employeelist=findViewById<RecyclerView>(R.id.list_of_employees)
        val adapter=EmployeeAdapter()
        employeelist.adapter=adapter
        employeelist.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        viewmodel.employeeList.observe(this) {
            adapter.submitData(this.lifecycle, it)
        }
    }
}

// name:String, id:Int, profileUrl:String
// circle profile image