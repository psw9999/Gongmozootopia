package com.psw9999.ipo_alarm.UI.Activity

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.psw9999.ipo_alarm.Adapter.MainViewPager
import com.psw9999.ipo_alarm.R
import com.psw9999.ipo_alarm.Repository.AccountRepository
import com.psw9999.ipo_alarm.Repository.StockInfoRepository
import com.psw9999.ipo_alarm.UI.Fragment.CalendarFragment
import com.psw9999.ipo_alarm.UI.Fragment.MainFragment
import com.psw9999.ipo_alarm.UI.Fragment.NotificationFragment
import com.psw9999.ipo_alarm.UI.Fragment.StockInfo.CompanyInfoFragment
import com.psw9999.ipo_alarm.UI.Fragment.ThirdFragment
import com.psw9999.ipo_alarm.base.BaseActivity
import com.psw9999.ipo_alarm.base.BaseApplication
import com.psw9999.ipo_alarm.data.StockInfo
import com.psw9999.ipo_alarm.data.StockInfoResponse
import com.psw9999.ipo_alarm.databinding.ActivityStockInformationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class StockInformationActivity : BaseActivity() {

    val binding by lazy { ActivityStockInformationBinding.inflate(layoutInflater)}
    lateinit var stockInfo : StockInfo
    lateinit var viewPager2 : ViewPager2
    var itemPos : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent?.let { intent ->
            intent.getLongExtra("ipoIndex",-1)?.let { ipoIndex ->
                loadingOn()
                CoroutineScope(Dispatchers.IO).launch() {
                    launch{
                        stockInfo = StockInfoRepository().getStockInfo(ipoIndex)
                        Log.d("StockInfoRepository","OK")
                        true
                    }.join()
                    withContext(Dispatchers.Main) {
                        with(stockInfo)
                        {
                            if (stockInfo.ipoPrice == 0L) {
                                binding.textViewStockPrice.text =
                                    "- 원 [${ipoPriceLow} ~ ${ipoPriceHigh}원]"
                            } else {
                                binding.textViewStockPrice.text =
                                    "${ipoPrice} 원 ${ipoPriceLow} ~ ${ipoPriceHigh}"
                            }
                            var stockState = stockScheduleCheck(ipoForecastDate, ipoStartDate, ipoEndDate, ipoRefundDate, ipoDebutDate)
                            binding.slider.progress = stockState
                            when(stockState) {
                                1 -> binding.textViewForecastDay.setTextAppearance(R.style.textView_stockState)
                                3 -> binding.textViewIpoDay.setTextAppearance(R.style.textView_stockState)
                                5 -> binding.textViewRefundDay.setTextAppearance(R.style.textView_stockState)
                                7 -> binding.textViewDebutDay.setTextAppearance(R.style.textView_stockState)
                            }
                        }
                        stockInfo.isFollowing = intent.getBooleanExtra("isFollowing",false)
                        stockInfo.isAlarm = intent.getBooleanExtra("isAlarm",false)
                        itemPos = intent.getIntExtra("itemPos",0)
                        initToolbar()
                        loadingOff()
                    }
                }
            }
        }
        // 로딩 완료 후 추가
//        with(binding.stockInfoTabLayout) {
//            addTab(this.newTab().setText("회사정보"))
//            addTab(this.newTab().setText("공모정보"))
//            addTab(this.newTab().setText("수요예측"))
//            addTab(this.newTab().setText("주간사"))
//        }
        //initViewPager()
    }

    private fun initViewPager(){
        viewPager2 = binding.viewPager2StockInfo
        val pagerAdapter = MainViewPager(this)
        pagerAdapter.fragmentList = listOf(CompanyInfoFragment(),CompanyInfoFragment(),CompanyInfoFragment(),CompanyInfoFragment())
        viewPager2.adapter = pagerAdapter

        // 유저 스크롤 방지, 네비게이션을 통해서만 제어
        viewPager2.isUserInputEnabled = false
    }

    private fun initToolbar() {
        with(binding.stockInfoAppbar) {
            inflateMenu(R.menu.appbar_stock_info)
            navigationIcon = getDrawable(R.drawable.baseline_navigate_before_24)

            with(menu.findItem(R.id.action_following).actionView) {
                this as ImageButton
                this.setImageResource(R.drawable.imgbtn_favorit_states)
                this.setBackgroundResource(R.color.white)
                //TODO : 다른 사이즈의 화면에서도 정상적으로 적용되는지 체크..
                this.setPadding(20,0,20,0)
                if (stockInfo.isFollowing) this.isSelected = true
                setOnClickListener {
                    this.isSelected = (!this.isSelected)
                    stockInfo.isFollowing = (!stockInfo.isFollowing)
                    BaseApplication.helper.updateSQLiteDB(stockIndex = stockInfo.ipoIndex, isFollowing = stockInfo.isFollowing, isAlarm = stockInfo.isAlarm)
                    if (stockInfo.isFollowing) {
                        Snackbar.make(this, "${stockInfo.stockName}의 팔로잉을 설정하였습니다.",
                            Snackbar.LENGTH_SHORT).show()
                    }else{
                        Snackbar.make(this, "${stockInfo.stockName}의 팔로잉을 해제하였습니다.",
                            Snackbar.LENGTH_SHORT).show()
                    }
                }
            }

            title = stockInfo.stockName
            with(menu.findItem(R.id.action_alarm).actionView) {
                this as ImageButton
                this.setImageResource(R.drawable.imgbtn_alarm_states)
                this.setBackgroundResource(R.color.white)
                this.setPadding(20,0,0,0)
                if (stockInfo.isAlarm) this.isSelected = true
                setOnClickListener {
                    this.isSelected = (!this.isSelected)
                    stockInfo.isAlarm = (!stockInfo.isAlarm)
                    BaseApplication.helper.updateSQLiteDB(stockIndex = stockInfo.ipoIndex, isFollowing = stockInfo.isFollowing, isAlarm = stockInfo.isAlarm)
                    if (stockInfo.isAlarm) {
                        Snackbar.make(this, "${stockInfo.stockName}의 알람을 설정하였습니다.",
                            Snackbar.LENGTH_SHORT).show()
                    }else{
                        Snackbar.make(this, "${stockInfo.stockName}의 알람을 해제하였습니다.",
                            Snackbar.LENGTH_SHORT).show()
                    }
                }
            }

            setNavigationOnClickListener {
                intent.putExtra("itemPos",itemPos)
                intent.putExtra("isFollowing",stockInfo.isFollowing)
                intent.putExtra("isAlarm",stockInfo.isAlarm)
                setResult(Activity.RESULT_OK, intent);
                finish()
            }

            setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.action_following -> {
                        menuItem as ImageButton
                        menuItem.isSelected = (!menuItem.isSelected)
                        true
                    }
                    else -> false
                }
            }
        }
    }
    private fun stockScheduleCheck(ipoForecastDate : String?, ipoStartDate : String?, ipoEndDate : String?, ipoRefundDate : String?, ipoDebutDate : String?) : Int {
        val now = LocalDate.now()

        val ipoForecastDate = ipoForecastDate?.let {
            LocalDate.parse(ipoForecastDate)
        } ?: now.plusDays(1)
        val ipoStartDate = ipoStartDate?.let{
            LocalDate.parse(ipoStartDate)
        } ?: now.plusDays(2)
        val ipoEndDate = ipoEndDate?.let{
            LocalDate.parse(ipoEndDate)
        } ?: now.plusDays(3)
        val ipoRefundDate = ipoRefundDate?.let{
            LocalDate.parse(ipoRefundDate)
        } ?: now.plusDays(4)
        val ipoDebutDate = ipoDebutDate?.let {
            LocalDate.parse(ipoDebutDate)
        } ?: now.plusDays(5)
        when {
            now.isBefore(ipoForecastDate) -> return 0
            now.isEqual(ipoForecastDate) -> return 1
            now.isBefore(ipoStartDate) -> return 2
            now.isBefore(ipoEndDate) || now.isEqual(ipoEndDate) -> return 3
            now.isBefore(ipoRefundDate) -> return 4
            now.isEqual(ipoRefundDate) -> return 5
            now.isBefore(ipoDebutDate) -> return 6
            else -> return 7
        }
    }

}
