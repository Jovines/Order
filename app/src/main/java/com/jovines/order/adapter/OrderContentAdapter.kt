package com.jovines.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jovines.order.R
import com.jovines.order.order.Turn
import kotlinx.android.synthetic.main.content_item.view.*

/**
 * @author Jovines
 * @create 2020-04-14 1:56 PM
 *
 * 描述:
 *   内容GridView的Adapter
 */
class OrderContentAdapter(private val dataList: Array<Turn?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.content_item, parent, false)
        ) {}
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            val stringBuilder = StringBuilder()
            dataList[position]?.fixedPeople?.forEach {
                stringBuilder.append(it.toString() + "\n")
            }
            tv_item.text = stringBuilder
        }
    }
}