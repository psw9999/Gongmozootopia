package com.psw9999.gongmozootopia.CustomView

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import androidx.core.view.updatePadding
import com.google.android.material.chip.Chip
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.base.BaseApplication

class UnderwriterView : Chip {
    constructor(context: Context) : super(context)

    init {
        setEnsureMinTouchTargetSize(false)
        chipStrokeWidth = BaseApplication.dpToPx(context, 1.5F)
        this.isClickable = false
        this.chipCornerRadius = BaseApplication.dpToPx(context, 8F)
        this.minHeight = 0
        this.minWidth = 0
        this.textEndPadding = BaseApplication.dpToPx(context, 2F)
        this.textStartPadding = BaseApplication.dpToPx(context, 3F)
        this.updatePadding(0, 0, 0, 0)
    }

}