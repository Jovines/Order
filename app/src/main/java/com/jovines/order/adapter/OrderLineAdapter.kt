package com.jovines.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.content_item.view.*

/**
 * @author Jovines
 * @create 2020-04-14 2:20 PM
 *
 * 描述:
 *
 */
class OrderLineAdapter(private val row: Int, val layout: Int) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder =
            (convertView?.tag ?: LayoutInflater.from(parent?.context).inflate(
                layout, parent, false
            ).apply {
                tag = ViewHolder(this)
            }.tag as ViewHolder) as ViewHolder
        viewHolder.itemView.apply {
            val s = "${position + 1}"
            tv_item.text = s
        }
        return viewHolder.itemView
    }

    override fun getItem(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = row

}