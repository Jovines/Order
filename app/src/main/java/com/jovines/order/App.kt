package com.jovines.order

import android.app.Application
import android.content.Context
import com.jovines.order.util.defaultSharedPreferences
import com.jovines.order.util.editor
import com.jovines.order.util.rxPermission
import com.tbruyelle.rxpermissions2.RxPermissions

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
        lateinit var UUID: String
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base
        if (defaultSharedPreferences.getBoolean("isFirst", true)) {
            UUID = java.util.UUID.randomUUID().toString()
            defaultSharedPreferences.editor {
                putString("UUID", UUID)
                putBoolean("isFirst", false)
            }
        } else {
            UUID = defaultSharedPreferences.getString("UUID", "")!!
        }
    }
}