package com.psw9999.gongmozootopia.CustomView

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import com.google.android.material.textview.MaterialTextView
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.dpToPx
import org.joda.time.DateTime

class CalendarLabelView  : MaterialTextView {
    constructor(context: Context) : super(context)

    init {
        gravity = Gravity.CENTER
        setTextColor(context.getColor(R.color.white))
        textSize = dpToPx(context, 3F)
        setTypeface(null, Typeface.BOLD)
        isSingleLine = true
        //ellipsize = TextUtils.TruncateAt.MARQUEE
        isSelected = true

    }

    fun onBind(stockName : String, ipoKinds : Int, rowStart : Int, begin: Int, end: Int) {
        text = stockName
        when(ipoKinds) {
            0 -> setBackgroundColor(ContextCompat.getColor(context, R.color.CalendarLabel_IpoForecastDay))
            1,2 -> setBackgroundColor(ContextCompat.getColor(context, R.color.CalendarLabel_IpoDay))
            3 -> setBackgroundColor(ContextCompat.getColor(context, R.color.CalendarLabel_IpoRefundDay))
            4 -> setBackgroundColor(ContextCompat.getColor(context, R.color.CalendarLabel_IpoDebutDay))
        }
        layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(rowStart, 1),
            GridLayout.spec(begin, end - begin + 1, 1F)
        ).apply {
            width = 0
            topMargin = dpToPx(context, 2F).toInt()
            rightMargin = dpToPx(context, 2F).toInt()
            leftMargin = dpToPx(context, 2F).toInt()
        }
    }
}