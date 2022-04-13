package com.psw9999.gongmozootopia.UI.Activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.psw9999.gongmozootopia.Adapter.MainViewPager
import com.psw9999.gongmozootopia.Data.StockFollowingResponse
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Repository.StockInfoRepository
import com.psw9999.gongmozootopia.base.BaseActivity
import com.psw9999.gongmozootopia.Data.StockInfoResponse
import com.psw9999.gongmozootopia.ViewModel.StockFollowingViewModel
import com.psw9999.gongmozootopia.databinding.ActivityStockInformationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class StockInformationActivity : BaseActivity() {

    val binding by lazy { ActivityStockInformationBinding.inflate(layoutInflater)}
    val stockFollowingViewModel : StockFollowingViewModel by viewModels()
    lateinit var stockInfo : StockInfoResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadingOn()
        CoroutineScope(Dispatchers.IO).launch() {
            launch {
                stockInfo =
                    StockInfoRepository().getStockInfo(intent!!.getLongExtra("ipoIndex", -1))
                true
            }.join()
            withContext(Dispatchers.Main) {
                with(stockInfo) {
                    isFollowing = intent!!.getBooleanExtra("isFollowing", false)
                    initToolbar()
                    if (ipoPrice == 0L) binding.textViewConfirmStockPrice.text = "미정"
                    else binding.textViewConfirmStockPrice.text = "${ipoPrice}원"
                    binding.textViewStockPriceBand.text = "${ipoPriceLow} ~ ${ipoPriceHigh}원"
                    binding.textViewIpoForecastDay.text = ipoForecastDate
                    binding.textViewIpoStartDay.text = ipoStartDate
                    binding.textViewIpoDebutDay.text = ipoDebutDate
                    binding.textViewIpoRefundDay.text = ipoRefundDate
                    if (stockKinds == "스팩주") binding.cardViewCompanyInfo.visibility = View.GONE
                    else {
                        if (sales == 0L) binding.textViewCompanySales.text = "확인필요"
                        else binding.textViewCompanySales.text = unitCalculate(sales)
                        if (profits == 0L) binding.textViewCompanyProfit.text = "확인필요"
                        else binding.textViewCompanyProfit.text = unitCalculate(profits)
                    }
                    binding.textViewSector.text = sector
                    binding.textViewStockMarket.text = stockExchange
                    loadingOff()
                }
            }
        }
    }

    private fun unitCalculate(value : Long) : String {
        var tempStr : String = ""
        var value = value
        var share = 1000000000000L
        val unitArray = arrayOf<String>("조","억","만")
        // 조, 억, 만원까지 표시
        repeat(3) { i ->
            var temp = value/share
            if(temp != 0L) {
                tempStr += " ${temp}${unitArray[i]}"
                value %= share
                if (value < 0) value *= -1
            }
            share /= 10000L
        }
        tempStr += "원"
        return tempStr
    }

    private fun initToolbar() {
        with(binding.stockInfoAppbar) {
            inflateMenu(R.menu.appbar_stock_info)
            navigationIcon = getDrawable(R.drawable.baseline_navigate_before_24)
            setNavigationOnClickListener {
                finish()
            }
            title = stockInfo.stockName
            isTitleCentered = true
            with(menu.findItem(R.id.action_following).actionView) {
                this as ImageButton
                this.setImageResource(R.drawable.imgbtn_favorit_states)
                this.setBackgroundResource(R.color.white)
                if (stockInfo.isFollowing) this.isSelected = true
                setOnClickListener {
                    this.isSelected = (!this.isSelected)
                    stockInfo.isFollowing = (!stockInfo.isFollowing)
                    if (stockInfo.isFollowing) {
                        this.isSelected = true
                        stockFollowingViewModel.addStock(StockFollowingResponse(ipoIndex = stockInfo.ipoIndex, stockName = stockInfo.stockName, isFollowing = true))
                        Snackbar.make(this, "${stockInfo.stockName}의 팔로잉을 설정하였습니다.", Snackbar.LENGTH_SHORT).show()
                    }else{
                        this.isSelected = false
                        stockFollowingViewModel.deleteStock(stockInfo.ipoIndex)
                        Snackbar.make(this, "${stockInfo.stockName}의 팔로잉을 해제하였습니다.", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
