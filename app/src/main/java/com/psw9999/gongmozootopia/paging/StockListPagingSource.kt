package com.psw9999.gongmozootopia.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.fmt
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.today
import com.psw9999.gongmozootopia.communication.dbsgAPI
import com.psw9999.gongmozootopia.data.StockResponse
import retrofit2.await

private const val STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 10

class StockListPagingSource(
    private val service : dbsgAPI
) : PagingSource<Int, StockResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StockResponse> {
        // If params.key is null, it is the first load, so we start loading with STARTING_KEY
        //val position = params.key ?: STARTING_PAGE_INDEX
        val position = params.key
        return when (position) {
            null -> {
                val stockList = ArrayList<StockResponse>().apply {
                    val temp = service.getIpoList(todayStockQuery).await()
                    Log.d("temp","$temp, $todayStockQuery")
                    // 1. 오늘 일정있는 종목 불러오기
                    this.addAll(temp)
                    // 2. 청약 예정 종목 불러오기
                    this.addAll(service.getIpoList(ipoStockQuery).await())
                    // 3. 환불 예정 종목 불러오기
                    this.addAll(service.getIpoList(refundStockQuery).await())
                    // 4. 상장 예정 종목 불러오기
                    this.addAll(service.getIpoList(debutStockQuery).await())
                }

                LoadResult.Page(
                    data = stockList,
                    prevKey = null,
                    nextKey = STARTING_PAGE_INDEX
                )
            }
            else -> {
                val response = service.getIpoList(
                    page = position!!,
                    num = params.loadSize,
                    queryString = completeStock
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

    companion object {
        val todayString = today.toString(fmt)
        val filteringQuery = "stock_exchange is not null and stock_kinds is not null and ipo_start_date is not null and  ipo_cancel_bool = \"N\""
        val todayStockQuery =
                "('$todayString' BETWEEN ipo_start_date AND ipo_end_date) "
        val ipoStockQuery =
            "ipo_start_date > $todayString and $filteringQuery"
        val refundStockQuery =
            "ipo_end_date < $todayString and ipo_refund_date > $todayString and $filteringQuery"
        val debutStockQuery =
            "ipo_refund_date < $todayString and ipo_debut_date > $todayString and $filteringQuery"
        val completeStock =
            "ipo_debut_date < $todayString and $filteringQuery"
    }
}