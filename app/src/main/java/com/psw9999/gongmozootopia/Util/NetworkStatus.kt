package com.psw9999.gongmozootopia.Util

import android.app.AlertDialog
import android.content.Context
import kotlin.system.exitProcess

class NetworkStatus(val context : Context) {

    var msgDlg : AlertDialog? = null

    fun showNetworkDialog() {
        val msgBuilder = AlertDialog.Builder(context)
            .setTitle("네트워크 연결 오류")
            .setMessage("네트워크 연결을 확인해주세요.")
            .setPositiveButton("취소") { dialog, which ->
                dialog.cancel()
            }
            .setNegativeButton("종료") { dialog, i ->
                exitProcess(0)
            }
        msgDlg = msgBuilder.create() as AlertDialog
        msgDlg?.show()
    }

    fun closeNetworkDialog() {
        msgDlg?.dismiss()
    }
}