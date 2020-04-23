package com.jovines.order.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jovines.order.base.viewmodel.BaseViewModel
import com.jovines.order.bean.FeedbackBean
import com.jovines.order.network.ApiGenerator
import com.jovines.order.network.ApiService
import com.jovines.order.util.rxjava.setSchedulers

/**
 * @author jon
 * @create 2020-04-23 3:27 PM
 *
 * 描述:
 *
 */
class FeedbackViewModel : BaseViewModel() {

    var dataList: MutableList<FeedbackBean> = mutableListOf()
    var updateFeedBack: MutableLiveData<Unit> = MutableLiveData(Unit)
    val apiService = ApiGenerator.getApiService(ApiService::class.java)


    fun getYourOwnFeedback(identification: String) {
        apiService
            .queryYourOwnFeedback(identification)
            .setSchedulers()
            .subscribe {
                dataList.clear()
                dataList.addAll(it.data.reversed())
                updateFeedBack.value = Unit
            }.isDisposed
    }

    fun addFeedBack(feedbackBean: FeedbackBean){
        apiService.addFeed(feedbackBean)
            .setSchedulers()
            .subscribe {
                dataList.clear()
                dataList.addAll(it.data.reversed())
                updateFeedBack.value = Unit
            }.isDisposed
    }

    fun deleteFeedback(id:Long){
        apiService.deleteFeedback(id)
            .setSchedulers()
            .subscribe {
                dataList.clear()
                dataList.addAll(it.data.reversed())
                updateFeedBack.value = Unit
            }.isDisposed
    }
}