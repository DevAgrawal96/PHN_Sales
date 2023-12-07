package com.phntechnolab.sales.paging

import android.app.Application
import android.widget.Toast
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.AuthApi
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkUtils
import javax.inject.Inject

private const val PAGE_INDEX = 1

class SchoolPagingSource @Inject constructor(
    private val application: Application,
    private val authApi: AuthApi
) : PagingSource<Int, SchoolData>() {
    override fun getRefreshKey(state: PagingState<Int, SchoolData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SchoolData> {
        val pageIndex = params.key ?: PAGE_INDEX
        return try {
            val result = authApi.getAllSchoolDataPage(pageIndex)
            result.body()?.let {
                PagingSource.LoadResult.Page(
                    data = ArrayList<SchoolData>(it.school?.data?.sortedByDescending { it.updatedAt }),
                    prevKey = if (pageIndex == PAGE_INDEX) null else pageIndex - 1,
                    nextKey = if (result.body()?.school?.nextPageUrl == null) null else pageIndex + 1
                )
            } ?: PagingSource.LoadResult.Page(
                data = ArrayList(),
                prevKey = if (pageIndex == PAGE_INDEX) null else pageIndex,
                nextKey = if (result.body()?.school?.nextPageUrl == null) null else pageIndex + 1
            )

        } catch (e: Exception) {
            if (NetworkUtils.isInternetAvailable(application)) {
                Toast.makeText(
                    application,
                    application.resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    application,
                    application.resources.getString(R.string.please_connection_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            LoadResult.Error(e)
        }

    }
}

