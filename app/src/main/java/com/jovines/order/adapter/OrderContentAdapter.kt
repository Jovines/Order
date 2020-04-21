package com.jovines.order.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jovines.order.R
import com.jovines.order.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.content_item.view.*

/**
 * @author Jovines
 * @create 2020-04-14 1:56 PM
 *
 * 描述:
 *   内容GridView的Adapter
 */
class OrderContentAdapter(private val orderViewModel: OrderViewModel) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.content_item, parent, false)
        )
    }

    override fun getItemCount() = orderViewModel.turnList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            val stringBuilder = StringBuilder()
            orderViewModel.turnList[position]?.fixedPeople?.forEach {
                stringBuilder.append(it.toString() + "\n")
            }
            tv_item_1.text = (orderViewModel.turnList[position]?.fixedPeople?.size ?: 0).toString()
            tv_item_3.text = orderViewModel.turnCanFixed.toString()
            if ((orderViewModel.turnList[position]?.fixedPeople?.size
                    ?: 0) == orderViewModel.turnCanFixed
            ) {
                tv_item_1.setTextColor(Color.GREEN)
            } else {
                tv_item_1.setTextColor(Color.WHITE)
            }
            setOnClickListener {
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle("当前位置的ITEM：")
                dialog.setMessage(stringBuilder)
                dialog.setPositiveButton("确认") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                }
                dialog.show()
            }
        }
    }
}