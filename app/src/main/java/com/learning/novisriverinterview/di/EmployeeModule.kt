package com.learning.novisriverinterview.di

import android.content.Context
import androidx.core.view.ViewCompat.ScrollIndicators
import androidx.room.Room
import com.learning.novisriverinterview.db.EmployeeDao
import com.learning.novisriverinterview.db.EmployeeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class EmployeeModule {
    @Singleton
    @Provides
    fun providesEmployeedb(@ApplicationContext context: Context):EmployeeDatabase {
        return Room.databaseBuilder(
            context,
            EmployeeDatabase::class.java,
            "employee_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun providesEmployeeDao(employeeDatabase: EmployeeDatabase):EmployeeDao {
        return employeeDatabase.employeeDao()
    }
}