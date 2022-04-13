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
import com.google.android.material.textview.MaterialTextView
import com.psw9999.gongmozootopia.R
import org.joda.time.DateTime

class CalendarLabelView  : MaterialTextView {
    constructor(context: Context) : super(context)

    init {
        gravity = Gravity.CENTER
        setTextColor(context.getColor(R.color.white))
        setTypeface(null, Typeface.BOLD)

        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.MARQUEE
        isSelected = true
    }

    fun onBind(begin: Int, end: Int) {
        text = "LG에너지솔루션"
        setBackgroundColor(ContextCompat.getColor(context, R.color.textView_IPO))
        layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED, 1),
            GridLayout.spec(begin, end - begin + 1, 1F)
        ).apply {
            width = 0
        }
    }
}