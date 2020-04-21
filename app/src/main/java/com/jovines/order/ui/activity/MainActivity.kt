package com.jovines.order.ui.activity

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.jovines.order.R
import com.jovines.order.adapter.MainViewPagerAdapter
import com.jovines.order.base.BaseActivity
import com.jovines.order.event.PageTurningEvent
import com.jovines.order.ui.MainDialogHelper
import com.jovines.order.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity() {

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
            MainDialogHelper.mainBottomSheetDialog(this, viewModel)?.show()
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
                    MainDialogHelper.specificationSettingDialog(this, viewModel).show()
                }
                R.id.number_of_people -> {
                    MainDialogHelper.numberOfPeopleDialog(this, viewModel).show()
                }
                R.id.times_setting -> {
                    MainDialogHelper.timeSettingDialog(this, viewModel).show()
                }
                R.id.empty -> {
                    MainDialogHelper.clearDataDialog(this, viewModel)?.show()
                }
                R.id.export_as_excel -> {
                    MainDialogHelper.fileExport(this, viewModel)
                }
                R.id.one_click_import -> {
                    MainDialogHelper.importDialog(this, viewModel)
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