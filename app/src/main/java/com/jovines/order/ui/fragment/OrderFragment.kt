package com.jovines.order.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.jovines.order.R
import com.jovines.order.adapter.OrderContentAdapter
import com.jovines.order.adapter.OrderStartAdapter
import com.jovines.order.adapter.OrderTopAdapter
import com.jovines.order.base.BaseFragment
import com.jovines.order.event.Update
import com.jovines.order.ui.DialogHelper
import com.jovines.order.util.rxjava.setSchedulers
import com.jovines.order.viewmodel.OrderViewModel
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_order.*

class OrderFragment : BaseFragment() {

    lateinit var viewModel: OrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity ?: return
        viewModel = ViewModelProvider(activity!!)[OrderViewModel::class.java]
        initFragment()
        viewModel.order()
    }

    private fun initFragment() {
        order_content.layoutManager = GridLayoutManager(activity!!, viewModel.maxColumn)
        order_top.layoutManager = GridLayoutManager(activity!!, viewModel.maxColumn)
        //排序内容设置
        order_content.adapter = OrderContentAdapter(viewModel)
        //顶部个数设置
        order_top.adapter = OrderTopAdapter(viewModel.orderScene)
        //侧边设置
        order_start.adapter = OrderStartAdapter(viewModel.orderScene)
    }

    override fun update(update: Update) {
        order_content.layoutManager = GridLayoutManager(activity!!, viewModel.maxColumn)
        order_top.layoutManager = GridLayoutManager(activity!!, viewModel.maxColumn)
        order_top.adapter?.notifyDataSetChanged()
        order_content.adapter?.notifyDataSetChanged()
        order_start.adapter?.notifyDataSetChanged()
    }


}
