package com.jovines.order.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.jovines.order.R
import com.jovines.order.bean.FeedbackBean
import com.jovines.order.viewmodel.FeedbackViewModel
import kotlinx.android.synthetic.main.recycle_item_feed_back.view.*

/**
 * @author Jovines
 * @create 2020-04-23 12:05 PM
 *
 * 描述:
 *
 */
class FeedbackAdapter(
    private val dataList: List<FeedbackBean>,
    private val feedbackViewModel: FeedbackViewModel
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycle_item_feed_back,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.itemView.apply {
            tv_function_suggestions.text = data.title
            tv_suggestions_content.text = data.content
            tv_proposal_status.text = when (data.status) {
                1 -> {
                    tv_proposal_status.setTextColor(Color.GREEN)
                    "开发者已读"
                }
                2 -> {
                    tv_proposal_status.setTextColor(Color.RED)
                    "开发者已回复"
                }
                else -> {
                    tv_proposal_status.setTextColor(Color.WHITE)
                    "开发者未读"
                }
            }
            setOnLongClickListener {
                MaterialDialog.Builder(context)
                    .title("提示")
                    .content("是否确认删除该反馈?")
                    .negativeText("取消")
                    .positiveText("确认删除")
                    .onPositive { _, _ ->
                        data.id?.let { feedbackViewModel.deleteFeedback(it) }
                    }.show()
                true
            }
        }
    }
}