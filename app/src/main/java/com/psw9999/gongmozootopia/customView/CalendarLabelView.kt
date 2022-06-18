package com.psw9999.gongmozootopia.customView

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import com.google.android.material.textview.MaterialTextView
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.dpToPx

class CalendarLabelView  : MaterialTextView {
    constructor(context: Context) : super(context)

    init {
        gravity = Gravity.CENTER
        setTextColor(context.getColor(R.color.white))
        textSize = dpToPx(context, 3F)
        setTypeface(null, Typeface.BOLD)
        isSingleLine = true
        isSelected = true
    }

    fun addScheduleLabel(stockName : String, ipoKinds : Int, rowCnt : Int, colBegin: Int, colEnd: Int) {
        text = stockName
        when(ipoKinds) {
            0 -> setBackgroundColor(ContextCompat.getColor(context, R.color.CalendarLabel_IpoForecastDay))
            1,2 -> setBackgroundColor(ContextCompat.getColor(context, R.color.CalendarLabel_IpoDay))
            3 -> setBackgroundColor(ContextCompat.getColor(context, R.color.CalendarLabel_IpoRefundDay))
            4 -> setBackgroundColor(ContextCompat.getColor(context, R.color.CalendarLabel_IpoDebutDay))
        }
        layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(rowCnt, 1),
            GridLayout.spec(colBegin, colEnd - colBegin + 1, 1F)
        ).apply {
            width = 0
            topMargin = dpToPx(context, 2F).toInt()
            rightMargin = dpToPx(context, 2F).toInt()
            leftMargin = dpToPx(context, 2F).toInt()
        }
    }
}