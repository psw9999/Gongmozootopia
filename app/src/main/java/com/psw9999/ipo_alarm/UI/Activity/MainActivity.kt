package com.psw9999.ipo_alarm.UI.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.psw9999.ipo_alarm.Adapter.MainViewPager
import com.psw9999.ipo_alarm.R
import com.psw9999.ipo_alarm.UI.Fragment.MainFragment
import com.psw9999.ipo_alarm.UI.Fragment.NotificationFragment
import com.psw9999.ipo_alarm.UI.Fragment.SecondFragment
import com.psw9999.ipo_alarm.UI.Fragment.ThirdFragment
import com.psw9999.ipo_alarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    private lateinit var viewPager2 : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewPager()
        initBottomNavigation()
    }
    private fun initViewPager() {
        viewPager2 = binding.viewPager2Main
        val pagerAdapter = MainViewPager(this)
        pagerAdapter.fragmentList = listOf(MainFragment(),SecondFragment(),ThirdFragment(),NotificationFragment())
        viewPager2.adapter = pagerAdapter
        // 유저 스크롤 방지, 네비게이션을 통해서만 제어
        viewPager2.isUserInputEnabled = false
    }

    private fun initBottomNavigation() {
        //TODO : TEST
        createBadge(R.id.notification)
        //TODO : OnNavigationItemSelectedListener 왜 Deprecated?
        binding.bottomNavigationMain.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.notification -> {
                    setPageIndex(3)
                    createBadge(R.id.notification)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.setting -> {
                    setPageIndex(2)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.calendar -> {
                    setPageIndex(1)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    setPageIndex(0)
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }

    private fun setPageIndex(index: Int) {
        viewPager2.currentItem = index
    }

    //TEST
    private fun createBadge(itemID : Int) {
        var badge = binding.bottomNavigationMain.getOrCreateBadge(itemID)
        badge.isVisible = !(badge.isVisible)
    }
}