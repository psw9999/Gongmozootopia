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
private const val TAG = "STOCK_LIST_PAGING_SOURCE"

class StockListPagingSource(
    private val service : dbsgAPI
) : PagingSource<Int, StockResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StockResponse> {
        // If params.key is null, it is the first load, so we start loading with STARTING_KEY
        val position = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = service.getStockList(
                page = position,
                num = params.loadSize
            )

            val stockList = response.await().onEach {
                //TODO : default 단에서 수행하도록 수정하기
                scheduleCheck(it)
                underwriterCheck(it)
            }

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

    private fun scheduleCheck(stockData : StockResponse) {
        val fmt : DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val today = DateTime()
        val ipoStartDday : Int =
            stockData.ipoStartDate?.let { Days.daysBetween(today, fmt.parseDateTime(stockData.ipoStartDate)).days}?:-1
        val ipoEndDday : Int =
            stockData.ipoEndDate?.let { Days.daysBetween(today, fmt.parseDateTime(stockData.ipoEndDate)).days}?:-1
        val refundDday : Int =
            stockData.ipoRefundDate?.let{ Days.daysBetween(today, fmt.parseDateTime(stockData.ipoRefundDate)).days}?:-1
        val debutDday : Int =
            stockData.ipoDebutDate?.let { Days.daysBetween(today, fmt.parseDateTime(stockData.ipoDebutDate)).days}?:-1

        if (ipoStartDday == -1) {
            stockData.currentSchedule = "청약시작일 미정"
            stockData.scheduleDday = ""
        }
        else if (ipoStartDday > 0) {
            stockData.currentSchedule = "청약시작일 ${stockData.ipoStartDate!!.slice(5..6)}월 ${stockData.ipoStartDate!!.slice(8..9)}일"
            stockData.scheduleDday = "D-$ipoStartDday"
        }
        else if (ipoEndDday >= 0) {
            stockData.currentSchedule = "청약마감일 ${stockData.ipoEndDate!!.slice(5..6)}월 ${stockData.ipoEndDate!!.slice(8..9)}일"
            stockData.scheduleDday = "진행 중!"
        }
        else if (refundDday >= 0) {
            stockData.currentSchedule = "환불일 ${stockData.ipoRefundDate!!.slice(5..6)}월 ${stockData.ipoRefundDate!!.slice(8..9)}일"
            if (refundDday == 0) stockData.scheduleDday = "진행 중!"
            else stockData.scheduleDday = "D-$refundDday"
        }
        else if (debutDday == -1) {
            stockData.currentSchedule = "상장진행 예정 (상장일 미정)"
            stockData.scheduleDday = ""
        }
        else if (debutDday >= 0) {
            stockData.currentSchedule = "상장일 ${stockData.ipoDebutDate!!.slice(5..6)}월 ${stockData.ipoDebutDate!!.slice(8..9)}일"
            if (refundDday == 0) stockData.scheduleDday = "진행 중!"
            else stockData.scheduleDday = "D-$debutDday"
        }
        else {
            stockData.currentSchedule = "상장완료"
            stockData.scheduleDday = ""
        }
    }

    private fun underwriterCheck(stockData : StockResponse) {
        stockData.underwriter?.let{ underwriter ->
            val regex = Regex("증권|투자|금융")
            var sb = StringBuilder()
            underwriter.split(",").forEach { temp ->
                if (temp.contains(regex)) {
                    sb.append(regex.replace(temp,"").plus(","))
                }
            }
            if (sb.isEmpty()) stockData.underwriter = null
            else stockData.underwriter = sb.substring(0, sb.length-1)
        }
    }
}