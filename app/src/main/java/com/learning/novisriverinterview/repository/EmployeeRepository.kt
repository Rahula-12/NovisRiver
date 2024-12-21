package com.learning.novisriverinterview.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.learning.novisriverinterview.db.EmployeeDao
import com.learning.novisriverinterview.model.Employee
import javax.inject.Inject

class EmployeeRepository @Inject constructor(
    private val employeeDao: EmployeeDao
) {

    suspend fun insertEmployee(employee: Employee) = employeeDao.insertEmployee(employee)

    fun employees(): LiveData<PagingData<Employee>> = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 200),
        pagingSourceFactory = { employeeDao.employees() }
    ).liveData

    suspend fun deleteAllRecords() = employeeDao.deleteAllRecords()

}