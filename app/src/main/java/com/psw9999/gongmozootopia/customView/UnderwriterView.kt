package com.psw9999.gongmozootopia.customView

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.view.updatePadding
import com.google.android.material.chip.Chip
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.base.BaseApplication

class UnderwriterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = R.style.Widget_MaterialComponents_Chip_Choice
) : Chip(context, attrs, defStyle) {
    init {
        setEnsureMinTouchTargetSize(false)
        isClickable = false
        chipStrokeWidth = BaseApplication.dpToPx(context, 1.5F)
        chipCornerRadius = BaseApplication.dpToPx(context, 8F)
        textEndPadding = BaseApplication.dpToPx(context, 2F)
        textStartPadding = BaseApplication.dpToPx(context, 3F)
        setChipStrokeColorResource(R.color.chip_underwriter)
        setChipBackgroundColorResource(R.color.white)
        setTextAppearance(R.style.Chip_Unregistered_StockFirm_TextTheme)
        updatePadding(0, 0, 0, 0)
    }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        if (checked) {
            this.setChipBackgroundColorResource(R.color.chip_underwriter)
            this.setTextAppearance(R.style.Chip_Registered_StockFirm_TextTheme)
        }else {
            this.setChipBackgroundColorResource(R.color.white)
            this.setTextAppearance(R.style.Chip_Unregistered_StockFirm_TextTheme)
        }
    }
}