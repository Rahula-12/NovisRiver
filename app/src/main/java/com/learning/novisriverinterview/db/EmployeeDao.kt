package com.learning.novisriverinterview.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.learning.novisriverinterview.model.Employee

@Dao
interface EmployeeDao {

    @Insert
    suspend fun insertEmployee(employee: Employee)

    @Query("Select * from employee_db")
    suspend fun employees():List<Employee>

    @Query("delete from employee_db")
    suspend fun deleteAllRecords():Unit

}