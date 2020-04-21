package com.jovines.order.util

import android.Manifest
import android.app.Activity
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * @author jon
 * @create 2020-04-21 4:55 PM
 *
 * 描述:
 *
 */

fun Activity.rxPermission(failureMessage:String,successfulCallback:()->Unit){
    val rxPermission = RxPermissions(this)
    if (rxPermission.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        successfulCallback()
    } else {
        rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe {
                if (it) {
                    successfulCallback()
                } else {
                    Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
                }
            }.isDisposed
    }
}