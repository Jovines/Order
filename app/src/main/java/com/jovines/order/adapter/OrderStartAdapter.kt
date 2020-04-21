package com.jovines.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jovines.order.R
import com.jovines.order.order.OrderScene
import kotlinx.android.synthetic.main.content_start.view.*

/**
 * @author Jovines
 * @create 2020-04-14 9:22 PM
 *
 * 描述:
 *
 */
class OrderStartAdapter(val size: OrderScene) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.content_start, parent, false)
        )
    }

    override fun getItemCount() = size.maxRow

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            val s = "${position + 1}"
            tv_item.text = s
        }
    }
}