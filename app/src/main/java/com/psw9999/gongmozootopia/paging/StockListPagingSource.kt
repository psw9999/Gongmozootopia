package com.psw9999.gongmozootopia.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.psw9999.gongmozootopia.data.StockResponse
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.lang.Integer.max

private const val STARTING_KEY = 0
private const val LOAD_DELAY_MILLIS = 3_000L

class StockListPagingSource : PagingSource<Int, StockResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StockResponse> {
        // If params.key is null, it is the first load, so we start loading with STARTING_KEY
        val startKey = params.key ?: STARTING_KEY

        // We fetch as many articles as hinted to by params.loadSize
        val range = startKey.until(startKey + params.loadSize)

        // Simulate a delay for loads adter the initial load
        if (startKey != STARTING_KEY) delay(LOAD_DELAY_MILLIS)

        return try {
            LoadResult.Page(
                data = response,
                prevKey = when (startKey) {
                    STARTING_KEY -> null
                    else -> when (val prevKey =
                        ensureValidKey(key = range.first - params.loadSize)) {
                        // We're at the start, there's nothing more to load
                        STARTING_KEY -> null
                        else -> prevKey
                    }
                },
                nextKey = range.last + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (e: Exception) {
            LoadResult.Error(e)
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