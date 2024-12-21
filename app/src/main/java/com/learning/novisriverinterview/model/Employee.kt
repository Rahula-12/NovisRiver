package com.learning.novisriverinterview.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee_db")
data class Employee(
    val name:String,
    @PrimaryKey
    val id:Int,
    val profileUrl:String
)
