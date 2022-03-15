package com.psw9999.ipo_alarm.CustomView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import com.psw9999.ipo_alarm.R
import com.psw9999.ipo_alarm.util.CalendarUtils.Companion.getDateColor
import com.psw9999.ipo_alarm.util.CalendarUtils.Companion.isSameMonth
import org.joda.time.DateTime

class DayItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes private val defStyleAttr: Int = R.attr.itemViewStyle,
    @StyleRes private val defStyleRes: Int = R.style.Calendar_ItemViewStyle,
    private val date: DateTime = DateTime(),
    private val firstDayOfMonth: DateTime = DateTime()
) : View(ContextThemeWrapper(context, defStyleRes), attrs, defStyleAttr) {

    private val bounds = Rect()
    private var textPaint: Paint = Paint()

    init {
        /* Attributes */
        context.withStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, defStyleRes) {
            val dayTextSize = getDimensionPixelSize(R.styleable.CalendarView_dayTextSize, 0).toFloat()

            /* 흰색 배경에 유색 글씨 */
            textPaint = TextPaint().apply {
                isAntiAlias = true
                textSize = dayTextSize
                color = getDateColor(date.dayOfWeek)
                if (!isSameMonth(date, firstDayOfMonth)) {
                    alpha = 50
                }
            }


        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        val date = date.dayOfMonth.toString()
        val linePaint = Paint().apply {
            color = Color.GRAY
            style = Paint.Style.FILL
            strokeWidth = 3F
        }
        val rectPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
            strokeWidth = 5F
        }

        textPaint.getTextBounds(date, 0, date.length, bounds)
        canvas.apply {
            drawText(
                date,
                (width / 2 - bounds.width() / 2).toFloat() - 2,
                (height/4).toFloat(),
                textPaint
            )
            Log.d("canvas","height : $height, bound : ${bounds.height()}")
            drawLine(
                0F,
                height.toFloat(),
                width.toFloat(),
                height.toFloat(),
                linePaint
            )
        }
    }
}