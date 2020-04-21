package com.jovines.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jovines.order.R
import com.jovines.order.order.OrderScene
import kotlinx.android.synthetic.main.content_item.view.*
import kotlinx.android.synthetic.main.content_top.view.*

/**
 * @author Jovines
 * @create 2020-04-14 2:20 PM
 *
 * 描述:
 *
 */
class OrderTopAdapter(private val orderScene: OrderScene) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.content_top, parent, false
        )
    )

    override fun getItemCount() = orderScene.maxColumn

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            val s = "${position + 1}"
            tv_item.text = s
        }
    }
}