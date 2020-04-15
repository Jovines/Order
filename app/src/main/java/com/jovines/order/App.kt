package com.jovines.order

import android.app.Application
import android.content.Context

/**
 * @author Jovines
 * @create 2020-04-15 12:06 AM
 *
 * 描述:
 *
 */
class App : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base
    }
}