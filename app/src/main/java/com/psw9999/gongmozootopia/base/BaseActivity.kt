package com.psw9999.gongmozootopia.base

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    fun loadingOn() {
        BaseApplication.instance.loadingOn(this)
    }
    fun loadingOff() {
        BaseApplication.instance.loadingOff()
    }


}