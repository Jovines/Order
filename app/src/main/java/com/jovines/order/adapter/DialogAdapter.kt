package com.jovines.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.jovines.order.R
import com.jovines.order.order.Item
import kotlinx.android.synthetic.main.dialog_grid_item.view.*

/**
 * @author Jovines
 * @create 2020-04-14 3:36 PM
 *
 * 描述:
 *
 */
class DialogAdapter(
    val item: Item,
    private val maxColumn: Int,
    private val maxRow: Int
) :
    BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder =
            (convertView?.tag ?: LayoutInflater.from(parent?.context).inflate(
                R.layout.dialog_grid_item, parent, false
            ).apply {
                tag = ViewHolder(this)
            }.tag as ViewHolder) as ViewHolder
        viewHolder.itemView.apply {
            val row = position / maxColumn + 1
            val column = position % maxColumn + 1
            checkbox.isChecked = item.totalSchedule[column - 1][row - 1]
            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    item.add(column, row)
                } else {
                    item.remove(column, row)
                }
            }
        }
        return viewHolder.itemView
    }

    override fun getItem(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = maxRow * maxColumn

}