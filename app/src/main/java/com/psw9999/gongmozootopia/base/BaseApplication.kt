package com.psw9999.gongmozootopia.base

import android.app.Activity
import android.app.Application
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatDialog
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.util.SharedPreferences
import com.psw9999.gongmozootopia.util.sqliteHelper

class BaseApplication : Application(){

    companion object {
        lateinit var preferences : SharedPreferences
        lateinit var instance : BaseApplication
        lateinit var helper: sqliteHelper
        val stockListKey : String = "stockListKey"
        val stockInfoKey : String = "stockInfoKey"

        lateinit var loadingDialog : AppCompatDialog
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferences = SharedPreferences(applicationContext)
        helper = sqliteHelper(this,"stockDB",2)
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