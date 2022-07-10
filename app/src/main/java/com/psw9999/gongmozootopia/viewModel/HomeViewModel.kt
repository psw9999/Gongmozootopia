package com.psw9999.gongmozootopia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.ui.activity.BottomMenuType

class HomeViewModel : ViewModel() {
    private val _currentFragment = MutableLiveData(BottomMenuType.HOME)
    val currentFragment : LiveData<BottomMenuType> = _currentFragment

    private fun getFragmentType(menuItemId : Int) : BottomMenuType =
        when(menuItemId) {
            R.id.menu_home -> BottomMenuType.HOME
            R.id.menu_calendar -> BottomMenuType.CALENDAR
            R.id.menu_comment -> BottomMenuType.COMMENT
            R.id.menu_setting -> BottomMenuType.SETTING
            else -> throw IllegalArgumentException()
        }

    fun setCurrentFragment(menuItemId : Int) : Boolean {
        changeCurrentFragment(getFragmentType(menuItemId))
        return true
    }

    fun changeCurrentFragment(bottomMenuType : BottomMenuType) {
        if(this.currentFragment.value != bottomMenuType) _currentFragment.value = bottomMenuType
    }

}