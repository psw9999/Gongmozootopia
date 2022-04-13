package com.psw9999.gongmozootopia.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import androidx.appcompat.app.AppCompatDialog
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Repository.SettingRepository
import com.psw9999.gongmozootopia.Room.StockDatabase
import com.psw9999.gongmozootopia.util.SharedPreferences

class BaseApplication : Application(){

    companion object {
        lateinit var preferences : SharedPreferences
        lateinit var settingsManager : SettingRepository
        lateinit var loadingDialog : AppCompatDialog
        lateinit var instance : BaseApplication
        val stockDatabase by lazy {StockDatabase.getDatabase(instance.applicationContext)!!}

        fun dpToPx (context : Context, size : Float) : Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, size,
                context.resources.displayMetrics
            )
        }

        fun ApplicationContext() : Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferences = SharedPreferences(applicationContext)
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
