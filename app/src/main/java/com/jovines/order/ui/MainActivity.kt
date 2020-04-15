package com.jovines.order.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.jovines.order.R
import com.jovines.order.adapter.OrderContentAdapter
import com.jovines.order.adapter.OrderLineAdapter
import com.jovines.order.adapter.OrderStartAdapter
import com.jovines.order.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    private lateinit var mainDialogHelper: MainDialogHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainDialogHelper = MainDialogHelper(this, viewModel)
        lifecycle.addObserver(mainDialogHelper)
        initActivity()
    }

    private fun initActivity() {
        initData()
        float_button.setOnClickListener {
            mainDialogHelper.mainBottomSheetDialog()?.show()
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
                    mainDialogHelper.specificationSettingDialog().show()
                }
                R.id.number_of_people -> {
                    mainDialogHelper.numberOfPeopleDialog().show()
                }
                R.id.times_setting -> {
                    mainDialogHelper.timeSettingDialog().show()
                }
                R.id.empty -> {
                    mainDialogHelper.clearDataDialog()?.show()
                }
            }
            true
        }
    }

    fun initData() {
        viewModel.orderScene.removeAll()
        viewModel.turnList =
            arrayOfNulls(viewModel.maxColumn * viewModel.maxRow)
        if (viewModel.itemList.isNotEmpty()) {
            viewModel.itemList.forEach {
                viewModel.orderScene.addItem(it.toItem(viewModel.orderScene))
            }
        }
        //排序内容设置
        order_content.adapter = OrderContentAdapter(viewModel.turnList)
        order_content.layoutManager = GridLayoutManager(this, viewModel.maxColumn)
        //顶部个数设置
        order_top.numColumns = viewModel.maxColumn
        order_top.adapter = OrderLineAdapter(viewModel.maxColumn, R.layout.content_top)
        //侧边设置
        order_start.adapter = OrderStartAdapter(viewModel.maxRow)
        viewModel.order {
            (order_content.adapter as OrderContentAdapter).notifyDataSetChanged()
        }
    }
}