package com.jovines.order.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.jovines.order.R
import com.jovines.order.adapter.ItemRecyclerViewAdapter
import com.jovines.order.base.BaseFragment
import com.jovines.order.event.Update
import com.jovines.order.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.fragment_item_list.*


class ItemListFragment : BaseFragment() {

    private lateinit var viewModel: OrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity ?: return
        viewModel = ViewModelProvider(activity!!)[OrderViewModel::class.java]
        with(rv_item_list) {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = ItemRecyclerViewAdapter(viewModel.orderScene.items, viewModel)
        }
    }

    override fun update(update: Update) {
        rv_item_list.adapter?.notifyDataSetChanged()
    }
}
