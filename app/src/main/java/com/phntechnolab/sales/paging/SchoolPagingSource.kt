package com.phntechnolab.sales.paging

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.AuthApi
import com.phntechnolab.sales.model.SchoolData
import javax.inject.Inject

private const val TMDB_STARTING_PAGE_INDEX = 1

class SchoolPagingSource @Inject constructor(private val application: Application, private val authApi: AuthApi): PagingSource<Int, SchoolData>() {
    override fun getRefreshKey(state: PagingState<Int, SchoolData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SchoolData> {
        val pageIndex = params.key ?: TMDB_STARTING_PAGE_INDEX
        try {
            val result = authApi.getAllSchoolDataPage(pageIndex)
            var nextKey: Int? = null
            if (result.isSuccessful && result?.body() != null) {
                nextKey = if(result.body()?.school?.nextPageUrl != null) pageIndex +1 else null
//                nextKey = pageIndex +1
//                nextKey = if (result.body()?.school?.data?.isEmpty() != false) null else pageIndex + 1
//                nextKey = pageIndex + (params.loadSize / 5)

                return result.body()?.let {
                    LoadResult.Page(
                        data = ArrayList<SchoolData>(it.school?.data?.sortedByDescending { it.updatedAt }),
                        prevKey = if (pageIndex == TMDB_STARTING_PAGE_INDEX) null else pageIndex- 1,
                        nextKey = nextKey
                    )
                }?: LoadResult.Page(ArrayList(), if (pageIndex == TMDB_STARTING_PAGE_INDEX) null else pageIndex, nextKey)
//                    schoolDataMutableLiveData.postValue(NetworkResult.Success(result.body()))
            } else if (result.errorBody() != null) {
                Toast.makeText(
                    application,
                    application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                    Toast.LENGTH_LONG
                ).show()
//                LoadResult.Error(NumberFormatException())
            } else {
                Toast.makeText(
                    application,
                    application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            Toast.makeText(
                application,
                application.resources.getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT
            ).show()
//            LoadResult.Error(e)
        }

        return LoadResult.Page(ArrayList(),1,1)
    }
}