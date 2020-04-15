package com.jovines.order.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jovines.order.R
import com.jovines.order.order.Item
import com.jovines.order.ui.MainDialogHelper
import com.jovines.order.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.recycle_view_fragment_item.view.*

class ItemRecyclerViewAdapter(private val mValues: List<Item>, val viewModel: OrderViewModel) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_view_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mValues[position]
        with(holder.itemView) {
            tag = data
            item_name.text = data.name
            val totalTimes = "总：${data.totalTimeCount}"
            val usedTime = "已排：${data.fixedCount}"
            val unusedTime = "未排：${data.freeTimeCount}"
            item_content_total_count.text = totalTimes
            item_content_not_yet_order.text = unusedTime
            item_content_already_order.text = usedTime
            if (data.fixedCount < viewModel.orderScene.itemShouldBeFixed) {
                item_content_already_order.setTextColor(Color.RED)
            } else item_content_already_order.setTextColor(Color.WHITE)
            setOnClickListener {
                MainDialogHelper.itemBottomSheetDialog(context, viewModel, data)?.show()
            }
            setOnLongClickListener {
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle("提示")
                dialog.setMessage("是否删除该Item？")
                dialog.setPositiveButton("确认") { dialogInterface: DialogInterface, i: Int ->
                    viewModel.removeItem(mValues[position])
                }
                dialog.show()
                true
            }
        }
    }

    fun buildColorText(color: String, data: String) =
        String.format("<font color=\"${color}\">%s", data)

    override fun getItemCount(): Int = mValues.size
}
