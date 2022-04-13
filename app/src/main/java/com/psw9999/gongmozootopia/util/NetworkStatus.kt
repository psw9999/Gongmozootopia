package com.psw9999.gongmozootopia.util

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.lang.Exception
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

    // 네트워크 상태를 체크하는 콜백 함수
    @RequiresApi(Build.VERSION_CODES.N)
    fun registerNetworkCallback() {
        try {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            connMgr.registerDefaultNetworkCallback(
                object:ConnectivityManager.NetworkCallback(){
                    override fun onAvailable(network: Network) {
                        // 네트워크 연결시 실행되는 콜백
                        closeNetworkDialog()
                    }

                    override fun onLost(network: Network) {
                        // 네트워크 연결이 끊길시 발생하는 콜백
                        showNetworkDialog()
                    }

                })
        }catch(e : Exception){
            showNetworkDialog()
        }

    }
}