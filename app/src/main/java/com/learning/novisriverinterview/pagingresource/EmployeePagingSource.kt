package com.learning.novisriverinterview.pagingresource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.learning.novisriverinterview.db.EmployeeDao
import com.learning.novisriverinterview.model.Employee
import javax.inject.Inject

class EmployeePagingSource @Inject constructor(
    private val employeeDao: EmployeeDao
): PagingSource<Int, Employee>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Employee> {
        return try {
            val position = params.key ?: 1
            val response = employeeDao.employees()

            if (response.isNotEmpty()) {
                LoadResult.Page(
                    data = response,
                    prevKey = if (position == 1) null else (position - 1),
                    nextKey = if (position == response.size) null else (position + 1)
                )
            } else {
                LoadResult.Error(throw Exception("No Response"))
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Employee>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}