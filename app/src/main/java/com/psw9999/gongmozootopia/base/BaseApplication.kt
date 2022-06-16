package com.psw9999.gongmozootopia.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatDialog
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Repository.SettingRepository

class BaseApplication : Application(){
    companion object {
        lateinit var settingsManager : SettingRepository
        lateinit var loadingDialog : AppCompatDialog
        lateinit var instance : BaseApplication
        fun dpToPx (context : Context, size : Float) : Float {
            return (size * (context.resources
                .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
        }
        fun dpToPx(size : Float, context : Context): Int {
            return (size * (context.resources
                .displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        settingsManager = SettingRepository(applicationContext)
    }

    fun loadingOn(activity : Activity) {
        loadingDialog = AppCompatDialog(activity)
        with(loadingDialog) {
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            setContentView(R.layout.loading_animation)
            show()
        }
    }


    fun loadingOff() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

}
