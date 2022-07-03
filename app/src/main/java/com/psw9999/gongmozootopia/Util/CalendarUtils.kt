package com.psw9999.gongmozootopia.Util

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class CalendarUtils {
    companion object {
        const val WEEKS_PER_MONTH = 6

        val today = DateTime().toLocalDate()
        val fmt : DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

        fun getMonthList(dateTime: DateTime): List<DateTime> {
            val list = mutableListOf<DateTime>()
            val date = dateTime.withDayOfMonth(1)
            val prev = getPrevOffSet(date)
            val startValue = date.minusDays(prev)
            val totalDay = DateTimeConstants.DAYS_PER_WEEK * WEEKS_PER_MONTH
            for (i in 0 until totalDay) {
                list.add(DateTime(startValue.plusDays(i)))
            }

            return list
        }

        private fun getPrevOffSet(dateTime: DateTime): Int {
            var prevMonthTailOffset = dateTime.dayOfWeek
            if (prevMonthTailOffset >= 7) prevMonthTailOffset %= 7
            return prevMonthTailOffset
        }

        fun isSameMonth(first: DateTime, second: DateTime): Boolean =
            first.year == second.year && first.monthOfYear == second.monthOfYear

        fun isSameDay(first : DateTime) : Boolean =
            first.year == today.year && first.monthOfYear == today.monthOfYear && first.dayOfMonth == today.dayOfMonth

        fun getDayIndex(startDay : DateTime, curDay : DateTime) : Int {
            return Days.daysBetween(startDay,curDay).days
        }

        @ColorInt
        fun getDateColor(@IntRange(from=1, to=7) dayOfWeek: Int): Int {
            return when (dayOfWeek) {
                /* 토요일은 파란색 */
                DateTimeConstants.SATURDAY -> Color.parseColor("#2962FF")
                /* 일요일 빨간색 */
                DateTimeConstants.SUNDAY -> Color.parseColor("#D32F2F")
                /* 그 외 검정색 */
                else -> Color.parseColor("#000000")
            }
        }
    }
}