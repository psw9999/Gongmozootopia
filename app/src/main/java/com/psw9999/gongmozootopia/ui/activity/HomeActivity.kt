package com.psw9999.gongmozootopia.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.databinding.ActivityHomeBinding
import com.psw9999.gongmozootopia.ui.fragment.CalendarFragment
import com.psw9999.gongmozootopia.ui.fragment.CommentFragment
import com.psw9999.gongmozootopia.ui.fragment.ConfigurationFragment
import com.psw9999.gongmozootopia.ui.fragment.StockListFragment
import com.psw9999.gongmozootopia.viewModel.HomeViewModel

class HomeActivity : AppCompatActivity() {
    val homeViewModel : HomeViewModel by viewModels()

    val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        observe()
    }

    private fun changeFragment(bottomMenuType : BottomMenuType) {
        val transaction = supportFragmentManager.beginTransaction()
        var targetFragment = supportFragmentManager.findFragmentByTag(bottomMenuType.tag)

        if (targetFragment == null) {
            targetFragment = getFragment(bottomMenuType)
            transaction.add(R.id.home_container, targetFragment, bottomMenuType.tag)
        }
        transaction.show(targetFragment)
        BottomMenuType.values()
            .filterNot { it == bottomMenuType }
            .forEach { type ->
                supportFragmentManager.findFragmentByTag(type.tag)?.let {
                    transaction.hide(it)
                }
            }
        transaction.commitAllowingStateLoss()
    }

    private fun getFragment(bottomMenuType : BottomMenuType) : Fragment =
        when(bottomMenuType) {
            BottomMenuType.HOME -> StockListFragment()
            BottomMenuType.CALENDAR -> CalendarFragment()
            BottomMenuType.COMMENT -> CommentFragment()
            BottomMenuType.SETTING -> ConfigurationFragment()
        }

    private fun initViews() {
        // BottomNavi Init
        with(binding.bottomNavigationMain) {
            setOnItemSelectedListener { menuItem ->
                homeViewModel.setCurrentFragment(menuItem.itemId)
            }
            selectedItemId = homeViewModel.currentFragment.value?.itemId ?: R.id.menu_home
        }
    }

    private fun observe() {
        homeViewModel.currentFragment.observe(this) {
            changeFragment(it)
        }
    }
}

enum class BottomMenuType(val tag : String, val itemId : Int) {
    HOME("HOME", R.id.menu_home),
    CALENDAR("CALENDAR",R.id.menu_calendar),
    COMMENT("COMMENT",R.id.menu_comment),
    SETTING("SETTING",R.id.menu_setting)
}
