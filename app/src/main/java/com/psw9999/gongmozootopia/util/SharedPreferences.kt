package com.psw9999.gongmozootopia.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {
    private val preferenceFileName = "gomozootopia"
    private val preferencesKakaoIdToken = "kakaoIdToken"
    private val preferencesJWS = "JWS"
    private val preferencesKakaoUserLogined = "kakaouserLogined"

    private val preferences: SharedPreferences? =
        context.getSharedPreferences(preferenceFileName, 0)
    /* 파일 이름과 EditText를 저장할 Key 값을 만들고 prefs 인스턴스 초기화 */

    var kakaoIdToken: String?
        get() = preferences!!.getString(preferencesKakaoIdToken, "")
        set(value) = preferences!!.edit().putString(preferencesKakaoIdToken, value).apply()
    /* get/set 함수 임의 설정. get 실행 시 저장된 값을 반환하며 default 값은 ""
     * set(value) 실행 시 value로 값을 대체한 후 저장 */

    var JWS : String?
        get() = preferences!!.getString(preferencesJWS, null)
        set(value) = preferences!!.edit().putString(preferencesJWS, value).apply()

    var isUserLogined : Boolean
        get() = preferences!!.getBoolean(preferencesKakaoUserLogined, false)
        set(value) = preferences!!.edit().putBoolean(preferencesKakaoUserLogined, value).apply()

    fun clearPreferences() {
        preferences!!.edit().clear().apply();
    }

    interface OnPreferenceChangeListener {
        fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String)
    }

    lateinit var mPreferenceChangeListener: OnPreferenceChangeListener

    fun setOnPreferenceChangeListener(preferenceChangeListener: OnPreferenceChangeListener) {
        this.mPreferenceChangeListener = preferenceChangeListener
    }


}