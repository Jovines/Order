package com.jovines.order.util

import android.Manifest
import android.app.Activity
import android.os.Environment
import com.jovines.order.App
import com.tbruyelle.rxpermissions2.RxPermissions
import java.io.File

/**
 * @author Jovines
 * @create 2020-04-21 4:55 PM
 *
 * 描述:
 *
 */

fun Activity.rxPermission(
    permission: String,
    successfulCallback: () -> Unit,
    failedCallback: () -> Unit
) {
    val rxPermission = RxPermissions(this)
    if (rxPermission.isGranted(permission)) {
        successfulCallback()
    } else {
        rxPermission.request(permission)
            .subscribe {
                if (it) {
//                    saveUUID(permission)
                    successfulCallback()
                } else {
                    failedCallback()
                }
            }.isDisposed
    }
}

private fun saveUUID(permission: String) {
    Thread {
        if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            val path = Environment.getExternalStorageDirectory().absolutePath
            val file = File("$path/order/UUID.txt")
            if (file.exists()) {
                val UUID = file.readText()
                if (UUID.isNotEmpty()) {
                    App.UUID = UUID
                    App.context.defaultSharedPreferences.edit().putString("UUID", App.UUID).apply()
                } else {
                    file.createNewFile()
                    file.writeText(App.UUID)
                }
            } else {
                file.createNewFile()
                file.writeText(App.UUID)
            }
        }
    }.start()
}