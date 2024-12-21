package com.learning.novisriverinterview.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.learning.novisriverinterview.model.Employee

@Database(entities = [Employee::class], version = 1, exportSchema = false)
abstract class EmployeeDatabase:RoomDatabase() {

    abstract fun employeeDao():EmployeeDao

}