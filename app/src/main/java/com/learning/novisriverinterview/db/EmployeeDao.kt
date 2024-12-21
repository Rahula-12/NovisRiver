package com.learning.novisriverinterview.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.learning.novisriverinterview.model.Employee

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee)

    @Query("Select * from employee_db")
    fun employees(): PagingSource<Int, Employee>

    @Query("delete from employee_db")
    suspend fun deleteAllRecords():Unit

}