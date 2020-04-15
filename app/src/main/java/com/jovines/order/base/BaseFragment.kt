package com.jovines.order.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jovines.order.event.Update
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author Jovines
 * @create 2020-04-15 4:32 PM
 *
 * 描述:
 *
 */
open class BaseFragment: Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun update(update: Update) {

    }
}