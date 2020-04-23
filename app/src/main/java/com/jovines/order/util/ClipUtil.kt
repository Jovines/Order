package com.jovines.order.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import com.jovines.order.App


/**
 * @author Jovines
 * @create 2020-04-21 11:15 PM
 *
 * 描述:
 *
 */
object ClipUtil {

    fun copy(content: String) {
        if (!TextUtils.isEmpty(content)) { // 得到剪贴板管理器
            val cmb: ClipboardManager =
                App.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cmb.setPrimaryClip(ClipData.newPlainText(null, content.trim { it <= ' ' }))
            // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
            val clipData = ClipData.newPlainText(null, content)
            // 把数据集设置（复制）到剪贴板
            cmb.setPrimaryClip(clipData)
        }
    }


    fun getClipContent(): String {
        val manager =
            App.context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        if (manager != null) {
            if (manager.hasPrimaryClip() && manager.primaryClip!!.itemCount > 0) {
                val addedText = manager.primaryClip!!.getItemAt(0).text
                val addedTextString = addedText.toString()
                if (!TextUtils.isEmpty(addedTextString)) {
                    return addedTextString
                }
            }
        }
        return ""
    }

    fun clearClipboard() {
        val manager =
            App.context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        manager?.setPrimaryClip(ClipData.newPlainText(null, ""))
    }
}