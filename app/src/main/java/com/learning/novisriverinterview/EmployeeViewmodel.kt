package com.learning.novisriverinterview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.learning.novisriverinterview.model.Employee
import com.learning.novisriverinterview.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewmodel @Inject constructor(private val employeeRepository: EmployeeRepository):ViewModel() {

    val employeeList: LiveData<PagingData<Employee>> =
        employeeRepository.employees()
            .cachedIn(viewModelScope)

    fun insertEmployee(employee: Employee) {
        viewModelScope.launch(Dispatchers.IO) {
            employeeRepository.insertEmployee(employee)
        }
    }

    fun deleteAllEmployees() {
        viewModelScope.launch(Dispatchers.IO) {
            employeeRepository.deleteAllRecords()
        }
    }


}