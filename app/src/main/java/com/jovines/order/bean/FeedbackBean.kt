package com.jovines.order.bean

import java.io.Serializable

/**
 * (Feedback)实体类
 *
 * @author Jovines
 * @since 2020-04-23 13:07:23
 */
data class FeedbackBean(
        var id: Long? = null,
        var identificationcode: String? = null,
        var title: String? = null,
        var content: String? = null,
        //状态为1表示开发者已读，2表示未读
        var status: Int? = null) : Serializable