package com.psw9999.ipo_alarm.base

import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    fun loadingOn() {
        BaseApplication.instance.loadingOn(this)
    }
    fun loadingOff() {
        BaseApplication.instance.loadingOff()
    }


}