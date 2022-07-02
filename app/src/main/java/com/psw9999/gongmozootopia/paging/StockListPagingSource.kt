package com.psw9999.gongmozootopia.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.psw9999.gongmozootopia.DAO.FollowingDAO
import com.psw9999.gongmozootopia.Util.CalendarUtils
import com.psw9999.gongmozootopia.communication.dbsgAPI
import com.psw9999.gongmozootopia.data.StockResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit2.HttpException
import retrofit2.await
import java.io.IOException
import java.lang.Integer.max

private const val STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 10

class StockListPagingSource(
    private val service : dbsgAPI
) : PagingSource<Int, StockResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StockResponse> {
        // If params.key is null, it is the first load, so we start loading with STARTING_KEY
        //val position = params.key ?: STARTING_PAGE_INDEX
        val position = params.key
//        return try {
//            val response = service.getStockList(
//                page = position!!,
//                num = params.loadSize
//            )
//            val stockList = response.await()
//            val nextKey =
//                if (stockList.isEmpty()) {
//                    null
//                } else {
//                    position + (params.loadSize / NETWORK_PAGE_SIZE)
//                }
//            LoadResult.Page(
//                data = stockList,
//                prevKey = when (position) {
//                    STARTING_PAGE_INDEX -> null
//                    else -> position - 1
//                    },
//                nextKey = nextKey
//            )
//        } catch (exception: IOException) {
//            LoadResult.Error(exception)
//        } catch (exception: HttpException) {
//            LoadResult.Error(exception)
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
        return when (position) {
            null -> {
                val response = service.getStockList()
                val stockList = response.await()
                LoadResult.Page(
                    data = stockList,
                    prevKey = null,
                    nextKey = STARTING_PAGE_INDEX
                )
            }
            else -> {
                val response = service.getStockList(
                    page = position!!,
                    num = params.loadSize
                )
                val stockList = response.await()
                val nextKey =
                    if (stockList.isEmpty()) {
                        null
                    } else {
                        position + (params.loadSize / NETWORK_PAGE_SIZE)
                    }
                LoadResult.Page(
                    data = stockList,
                    prevKey = when (position) {
                        STARTING_PAGE_INDEX -> null
                        else -> position - 1
                    },
                    nextKey = nextKey
                )
            }
        }
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, StockResponse>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}