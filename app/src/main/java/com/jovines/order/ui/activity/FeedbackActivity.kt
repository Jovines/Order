package com.jovines.order.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.jovines.order.App
import com.jovines.order.R
import com.jovines.order.adapter.FeedbackAdapter
import com.jovines.order.base.BaseViewModelActivity
import com.jovines.order.ui.DialogHelper
import com.jovines.order.viewmodel.FeedbackViewModel
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : BaseViewModelActivity<FeedbackViewModel>() {

    companion object {
        const val IDENTIFICATION_TAG = "IDENTIFICATION_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        setSupportActionBar(feedback_toolbar)
        initActivity()
    }

    private fun initActivity() {
        feedback_toolbar.setNavigationOnClickListener { finish() }
        val feedbackAdapter = FeedbackAdapter(viewModel.dataList,viewModel)
        feedback_recyclerView.adapter = feedbackAdapter

        viewModel.updateFeedBack.observe(this, Observer {
            tv_blank_prompt.visibility = if (viewModel.dataList.isEmpty()) View.VISIBLE else View.GONE
            feedback_swipe.isRefreshing = false
            feedbackAdapter.notifyDataSetChanged()
        })
        viewModel.getYourOwnFeedback(App.UUID)
        feedback_swipe.setOnRefreshListener {
            viewModel.getYourOwnFeedback(App.UUID)
        }
        fab.setOnClickListener {
            DialogHelper.addSuggestions(this, viewModel).show()
        }
    }

    override val viewModelClass = FeedbackViewModel::class.java
}