package com.jovines.order.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Jovines
 * @create 2020-04-23 3:08 PM
 *
 * 描述:
 *
 */

object ApiGenerator {

    private var defaultRetrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 设置网络请求的公共Url地址
            .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava平台
            .build()

    fun <T> getApiService(clazz: Class<T>) = defaultRetrofit.create(clazz)

    fun <T> getApiService(retrofit: Retrofit, clazz: Class<T>) = retrofit.create(clazz)

}