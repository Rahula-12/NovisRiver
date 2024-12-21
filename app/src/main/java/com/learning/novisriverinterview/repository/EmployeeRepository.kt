package com.learning.novisriverinterview.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.learning.novisriverinterview.db.EmployeeDao
import com.learning.novisriverinterview.model.Employee
import com.learning.novisriverinterview.pagingresource.EmployeePagingSource
import javax.inject.Inject

class EmployeeRepository @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val employeePagingSource: EmployeePagingSource
) {

    suspend fun insertEmployee(employee: Employee) = employeeDao.insertEmployee(employee)

    fun employees(): LiveData<PagingData<Employee>> = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 200),
        pagingSourceFactory = { EmployeePagingSource(employeeDao) }
    ).liveData

    suspend fun deleteAllRecords() = employeeDao.deleteAllRecords()

}