package com.psw9999.gongmozootopia.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.fmt
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.today
import com.psw9999.gongmozootopia.communication.dbsgAPI
import com.psw9999.gongmozootopia.data.StockListItem
import com.psw9999.gongmozootopia.data.StockResponse
import org.joda.time.Days
import retrofit2.await

private const val STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 10

class StockListPagingSource(
    private val service : dbsgAPI
) : PagingSource<Int, StockListItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StockListItem> {
        // If params.key is null, it is the first load, so we start loading with STARTING_KEY
        //val position = params.key ?: STARTING_PAGE_INDEX
        val position = params.key
        return when (position) {
            null -> {
                val stockListItem = ArrayList<StockListItem>().apply {
                    StockScheduleQuery.values()
                        .forEach { stockScheduleQuery ->
                            this.add(StockListItem.SeparatorItem(stockScheduleQuery.title))
                            val stockList = service.getIpoList(stockScheduleQuery.query).await()
                            stockList.forEach { stockResponse ->
                                scheduleCheck(stockResponse)
                                underwriterCheck(stockResponse)
                                this.add(StockListItem.StockItem(stockResponse))
                            }
                        }
                    this.add(StockListItem.SeparatorItem("상장 완료"))
                }
                LoadResult.Page(
                    data = stockListItem,
                    prevKey = null,
                    nextKey = STARTING_PAGE_INDEX
                )
            }
            else -> {
                val response = service.getIpoList(
                    page = position!!,
                    num = params.loadSize,
                    queryString = completeQuery
                )
                val stockListItem = ArrayList<StockListItem>().apply {
                    response.await().forEach { stockResponse ->
                        scheduleCheck(stockResponse)
                        underwriterCheck(stockResponse)
                        this.add(StockListItem.StockItem(stockResponse))
                    }
                }
                val nextKey =
                    if (stockListItem.isEmpty()) {
                        null
                    } else {
                        position + (params.loadSize / NETWORK_PAGE_SIZE)
                    }
                LoadResult.Page(
                    data = stockListItem,
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
    override fun getRefreshKey(state: PagingState<Int, StockListItem>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    // TODO : 간단히 수정 필요
    private fun scheduleCheck(stockData : StockResponse) {
        val ipoStartDday : Int =
            stockData.ipoStartDate?.let { Days.daysBetween(today, fmt.parseDateTime(stockData.ipoStartDate).toLocalDate()).days}?:-1
        val ipoEndDday : Int =
            stockData.ipoEndDate?.let { Days.daysBetween(today, fmt.parseDateTime(stockData.ipoEndDate).toLocalDate()).days}?:-1
        val refundDday : Int =
            stockData.ipoRefundDate?.let{ Days.daysBetween(today, fmt.parseDateTime(stockData.ipoRefundDate).toLocalDate()).days}?:-1
        val debutDday : Int =
            stockData.ipoDebutDate?.let { Days.daysBetween(today, fmt.parseDateTime(stockData.ipoDebutDate).toLocalDate()).days}?:-1

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
            stockData.scheduleDday = "진행 중"
        }
        else if (refundDday >= 0) {
            stockData.currentSchedule = "환불일 ${stockData.ipoRefundDate!!.slice(5..6)}월 ${stockData.ipoRefundDate!!.slice(8..9)}일"
            if (refundDday == 0) {
                stockData.scheduleDday = "진행 중"
            }
            else {
                stockData.scheduleDday = "D-$refundDday"
            }
        }
        else if (debutDday == -1) {
            stockData.currentSchedule = "상장진행 예정 (상장일 미정)"
            stockData.scheduleDday = ""
        }
        else if (debutDday >= 0) {
            stockData.currentSchedule = "상장일 ${stockData.ipoDebutDate!!.slice(5..6)}월 ${stockData.ipoDebutDate!!.slice(8..9)}일"
            if (debutDday == 0) {
                stockData.scheduleDday = "진행 중"
            }
            else {
                stockData.scheduleDday = "D-$debutDday"
            }
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

    companion object {
        val todayString: String = today.toString(fmt)
        const val filteringQuery = "stock_exchange is not null AND stock_kinds is not null AND ipo_start_date is not null AND ipo_cancel_bool = \"N\""
        val completeQuery = "ipo_debut_date < '$todayString' and $filteringQuery"
    }
}

enum class StockScheduleQuery(val title : String, val query : String, val emptyGuide : String) {
    TodaySchedule("오늘의 진행 일정",
        "('${StockListPagingSource.todayString}' BETWEEN ipo_start_date AND ipo_end_date) OR ('${StockListPagingSource.todayString}' = ipo_refund_date) OR ('${StockListPagingSource.todayString}' = ipo_debut_date) AND ${StockListPagingSource.filteringQuery}", "오늘의 진행 일정이 없습니다."),
    IpoExpectedSchedule("청약 예정 일정", "ipo_start_date > '${StockListPagingSource.todayString}' AND ${StockListPagingSource.filteringQuery}", "청약 예정 일정이 없습니다."),
    RefundExpectedSchedule("환불 예정 일정", "ipo_end_date < '${StockListPagingSource.todayString}' AND ipo_refund_date > '${StockListPagingSource.todayString}' AND ${StockListPagingSource.filteringQuery}", "환불 예정 일정이 없습니다."),
    DebutExpectedSchedule("상장 예정 일정","ipo_refund_date < '${StockListPagingSource.todayString}' AND ipo_debut_date > '${StockListPagingSource.todayString}' AND ${StockListPagingSource.filteringQuery}","상장 예정 일정이 없습니다.")
}