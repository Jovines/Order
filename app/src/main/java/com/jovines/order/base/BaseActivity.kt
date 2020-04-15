package com.jovines.order.base

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.jovines.order.event.Update
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author Jovines
 * @create 2020-04-15 4:30 PM
 *
 * 描述:
 *
 */
@SuppressLint("Registered")
open class BaseActivity: AppCompatActivity() {


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun update(update: Update) {

    }
}