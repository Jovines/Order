package com.jovines.order.util

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.umeng.commonsdk.statistics.common.DeviceConfig.getPackageName
import java.io.File


/**
 * @author jon
 * @create 2020-04-21 4:26 PM
 *
 * 描述:
 *
 */
object ShareFile {

    // 調用系統方法分享文件
    fun shareFile(context: Context, file: File?) {
        if (null != file && file.exists()) {
            val share = Intent(Intent.ACTION_SEND)
            share.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    context,
                    getPackageName(context) + ".fileprovider",
                    file
                )
            )
            share.type = getMimeType(file.absolutePath) //此处可发送多种文件
            share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(share, "分享文件"))
        } else {
            Toast.makeText(context, "分享文件不存在", Toast.LENGTH_SHORT).show()
        }
    }


    fun openFile(context: Context, file: File) {
        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val uri: Uri = FileProvider.getUriForFile(
            context,
            getPackageName(context) + ".fileprovider",
            file
        )
        intent.setDataAndType(uri, "application/vnd.ms-excel")
        context.startActivity(intent)
    }


    // 根据文件后缀名获得对应的MIME类型。
    private fun getMimeType(filePath: String?): String? {
        val mmr = MediaMetadataRetriever()
        var mime = "*/*"
        if (filePath != null) {
            mime = try {
                mmr.setDataSource(filePath)
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            } catch (e: IllegalStateException) {
                return mime
            } catch (e: IllegalArgumentException) {
                return mime
            } catch (e: RuntimeException) {
                return mime
            }
        }
        return mime
    }
}