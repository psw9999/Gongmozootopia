package com.psw9999.gongmozootopia.CustomView

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.psw9999.gongmozootopia.R
import org.joda.time.DateTime

class CalendarLabelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes private val defStyleAttr: Int = R.attr.itemViewStyle,
    @StyleRes private val defStyleRes: Int = R.style.Calendar_ItemViewStyle,
    private val date: DateTime = DateTime(),
    private val firstDayOfMonth: DateTime = DateTime()
) : View(ContextThemeWrapper(context, defStyleRes), attrs, defStyleAttr) {
}