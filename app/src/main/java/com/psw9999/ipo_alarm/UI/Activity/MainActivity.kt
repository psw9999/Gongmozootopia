package com.psw9999.ipo_alarm.UI.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.psw9999.ipo_alarm.Adapter.MainViewPager
import com.psw9999.ipo_alarm.UI.Fragment.MainFragment
import com.psw9999.ipo_alarm.UI.Fragment.SecondFragment
import com.psw9999.ipo_alarm.UI.Fragment.ThirdFragment
import com.psw9999.ipo_alarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViewPager()
    }
    private fun initViewPager() {
        val pagerAdapter = MainViewPager(this)
        pagerAdapter.fragmentList = listOf(MainFragment(),SecondFragment(),ThirdFragment())
        binding.viewPager2Main.adapter = MainViewPager(this)
    }

    private fun initBottomNavigation() {
        //TODO : OnNavigationItemSelectedListener 왜 Deprecated?
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {

            }
        }
    }


}