package com.jovines.order.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.jovines.order.R
import com.jovines.order.adapter.MainViewPagerAdapter
import com.jovines.order.base.BaseActivity
import com.jovines.order.event.PageTurningEvent
import com.jovines.order.ui.DialogHelper
import com.jovines.order.util.defaultSharedPreferences
import com.jovines.order.util.storyList
import com.jovines.order.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity() {

    companion object {
        const val ENTER_THE_APP_AFTER_THE_FIRST_AUTHORIZATION =
            "ENTER_THE_APP_AFTER_THE_FIRST_AUTHORIZATION"
    }

    lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        initActivity()
    }

    private fun initActivity() {
        main_view_pager.adapter = MainViewPagerAdapter(this)

        float_button.setOnClickListener {
            DialogHelper.mainBottomSheetDialog(this, viewModel)?.show()
        }
        toolbar.setNavigationOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.specification_setting -> {
                    DialogHelper.specificationSettingDialog(this, viewModel).show()
                }
                R.id.number_of_people -> {
                    DialogHelper.numberOfPeopleDialog(this, viewModel).show()
                }
                R.id.times_setting -> {
                    DialogHelper.timeSettingDialog(this, viewModel).show()
                }
                R.id.empty -> {
                    DialogHelper.clearDataDialog(this, viewModel)?.show()
                }
                R.id.export_as_excel -> {
                    DialogHelper.fileExport(this, viewModel)
                }
                R.id.one_click_import -> {
                    DialogHelper.importDialog(this, viewModel)
                }
                R.id.suggestion_feedback -> {
                    startActivity(Intent(this, FeedbackActivity::class.java))
                }
                R.id.don_t_order_me -> {
                    val tag = "story"
                    var index = defaultSharedPreferences.getInt(tag, 0)
                    if (index < storyList.size)
                        Toast.makeText(this, storyList[index], Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "别点了别点了，真没了", Toast.LENGTH_SHORT).show()
                    defaultSharedPreferences.edit().putInt(tag, ++index).apply()
                }
            }
            true
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun pageTurning(pageTurningEvent: PageTurningEvent) {
        main_view_pager.currentItem = 1
        drawer_layout.closeDrawer(GravityCompat.START)
    }
}