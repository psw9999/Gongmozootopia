package com.psw9999.gongmozootopia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.psw9999.gongmozootopia.Repository.FollowingListRepository
import com.psw9999.gongmozootopia.Repository.StockRepository
import com.psw9999.gongmozootopia.Room.FollowingDatabase
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.fmt
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.today
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockListItem
import com.psw9999.gongmozootopia.data.StockResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class StockListViewModel(application: Application) : AndroidViewModel(application) {
    private val followingDAO = FollowingDatabase.getDatabase(application)!!.followingDAO()
    private val followingRepository = FollowingListRepository(followingDAO)
    private val stockListRepository  = StockRepository()

    val stockList : Flow<PagingData<StockListItem>>

    val followingList
        = followingRepository.followingListFlow.asLiveData()

    init {
        stockList = searchStockList().cachedIn(viewModelScope)
    }

    private fun searchStockList() : Flow<PagingData<StockListItem>> =
        stockListRepository.getStockDataByPaging()
            .map { pagingData -> pagingData.map { stockData ->
                    scheduleCheck(stockData)
                    underwriterCheck(stockData)
                    StockListItem.StockItem(stockData) } }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }

                    if (before == null) {
                        // we're at the beginning of the list
                        return@insertSeparators StockListItem.SeparatorItem("오늘 일정입니다.")
                    }

                    if (before.stock.scheduleState != after.stock.scheduleState) {
                        when(after.stock.scheduleState) {
                            1 -> StockListItem.SeparatorItem("청약 예정 종목입니다.")
                            2 -> StockListItem.SeparatorItem("환불 예정 종목입니다.")
                            3 -> StockListItem.SeparatorItem("상장 예정 종목입니다.")
                            4 -> StockListItem.SeparatorItem("상장 완료 종목입니다.")
                            else -> null
                        }
                    } else {
                        null
                    }
                }
            }


    fun addFollowing(followingResponse: FollowingResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            followingRepository.addFollowing(followingResponse)
        }
    }

    fun deleteFollowing(ipoIndex: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            followingRepository.deleteFollowing(ipoIndex)
        }
    }

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
            stockData.scheduleState = 1
        }
        else if (ipoStartDday > 0) {
            stockData.currentSchedule = "청약시작일 ${stockData.ipoStartDate!!.slice(5..6)}월 ${stockData.ipoStartDate!!.slice(8..9)}일"
            stockData.scheduleDday = "D-$ipoStartDday"
            stockData.scheduleState = 1
        }
        else if (ipoEndDday >= 0) {
            stockData.currentSchedule = "청약마감일 ${stockData.ipoEndDate!!.slice(5..6)}월 ${stockData.ipoEndDate!!.slice(8..9)}일"
            stockData.scheduleDday = "진행 중"
            stockData.scheduleState = 0
        }
        else if (refundDday >= 0) {
            stockData.currentSchedule = "환불일 ${stockData.ipoRefundDate!!.slice(5..6)}월 ${stockData.ipoRefundDate!!.slice(8..9)}일"
            if (refundDday == 0) {
                stockData.scheduleDday = "진행 중"
                stockData.scheduleState = 0
            }
            else {
                stockData.scheduleDday = "D-$refundDday"
                stockData.scheduleState = 2
            }
        }
        else if (debutDday == -1) {
            stockData.currentSchedule = "상장진행 예정 (상장일 미정)"
            stockData.scheduleDday = ""
            stockData.scheduleState = 3
        }
        else if (debutDday >= 0) {
            stockData.currentSchedule = "상장일 ${stockData.ipoDebutDate!!.slice(5..6)}월 ${stockData.ipoDebutDate!!.slice(8..9)}일"
            if (debutDday == 0) {
                stockData.scheduleDday = "진행 중"
                stockData.scheduleState = 0
            }
            else {
                stockData.scheduleDday = "D-$debutDday"
                stockData.scheduleState = 3
            }
        }
        else {
            stockData.currentSchedule = "상장완료"
            stockData.scheduleDday = ""
            stockData.scheduleState = 4
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