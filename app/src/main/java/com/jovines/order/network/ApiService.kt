package com.jovines.order.network

import com.jovines.order.bean.FeedbackBean
import com.jovines.order.bean.WarpMessageBean
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author jon
 * @create 2020-04-23 3:15 PM
 *
 * 描述:
 *
 */
interface ApiService {

    @POST(ADD_ONE_FEED)
    fun addFeed(@Body feedbackBean: FeedbackBean): Observable<WarpMessageBean<List<FeedbackBean>>>

    @GET(QUERY_YOUR_OWN_FEEDBACK)
    fun queryYourOwnFeedback(@Query("identificationCode") identification: String): Observable<WarpMessageBean<List<FeedbackBean>>>

    @GET(DELETE_FEEDBACK)
    fun deleteFeedback(@Query("id") id: Long): Observable<WarpMessageBean<List<FeedbackBean>>>

}