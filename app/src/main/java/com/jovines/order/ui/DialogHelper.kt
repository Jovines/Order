@file:Suppress("DEPRECATION")

package com.jovines.order.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovines.order.App
import com.jovines.order.R
import com.jovines.order.adapter.DialogAdapter
import com.jovines.order.bean.FeedbackBean
import com.jovines.order.event.PageTurningEvent
import com.jovines.order.event.Update
import com.jovines.order.order.Item
import com.jovines.order.util.ExcelExport.asynDocumentExport
import com.jovines.order.util.ShareFile
import com.jovines.order.util.rxPermission
import com.jovines.order.viewmodel.FeedbackViewModel
import com.jovines.order.viewmodel.OrderViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.dialog_content.view.*
import kotlinx.android.synthetic.main.dialog_import_item_name.*
import kotlinx.android.synthetic.main.diaolg_add_feed_back.*
import kotlinx.android.synthetic.main.number_of_people.view.*
import kotlinx.android.synthetic.main.number_of_people.view.confirm_button
import kotlinx.android.synthetic.main.specification_setting.view.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern


/**
 * @author Jovines
 * @create 2020-04-15 2:32 PM
 *
 * 描述:
 *
 */
object DialogHelper {


    fun specificationSettingDialog(
        context: Context,
        orderViewModel: OrderViewModel
    ): AppCompatDialog {
        val dialog = AppCompatDialog(context)
        val inflate = LayoutInflater.from(context).inflate(
            R.layout.specification_setting,
            dialog.window?.decorView as ViewGroup,
            false
        )
        inflate.row_number_picker.maxValue = 12
        inflate.column_number_picker.maxValue = 12
        inflate.row_number_picker.minValue = 1
        inflate.column_number_picker.minValue = 1
        inflate.row_number_picker.value = orderViewModel.maxRow
        inflate.column_number_picker.value = orderViewModel.maxColumn

        inflate.confirm_button.setOnClickListener {
            val row = inflate.row_number_picker.value
            val column = inflate.column_number_picker.value
            orderViewModel.maxColumn = column
            orderViewModel.maxRow = row
            orderViewModel.clearAll()
            dialog.dismiss()
        }
        dialog.setContentView(inflate)
        return dialog
    }


    fun numberOfPeopleDialog(
        context: Context,
        orderViewModel: OrderViewModel
    ): AppCompatDialog {
        val dialog = AppCompatDialog(context)
        val inflate = LayoutInflater.from(context).inflate(
            R.layout.number_of_people,
            dialog.window?.decorView as ViewGroup,
            false
        )
        inflate.number_selection.maxValue = 50
        inflate.number_selection.minValue = 1
        inflate.number_selection.value =
            orderViewModel.orderScene.turnCanFixed
        inflate.confirm_button.setOnClickListener {
            orderViewModel.turnCanFixed = inflate.number_selection.value
            orderViewModel.order()
            dialog.dismiss()
        }
        dialog.setContentView(inflate)
        return dialog
    }

    fun timeSettingDialog(
        context: Context,
        orderViewModel: OrderViewModel
    ): AppCompatDialog {
        val dialog = AppCompatDialog(context)
        val inflate = LayoutInflater.from(context).inflate(
            R.layout.number_of_people,
            dialog.window?.decorView as ViewGroup,
            false
        )
        inflate.number_selection.maxValue = 50
        inflate.number_selection.minValue = 1
        inflate.number_selection.value =
            orderViewModel.orderScene.itemShouldBeFixed
        inflate.textView3.text = context.getString(R.string.an_item_can_be_sorted)
        inflate.textView11.text = "次"
        inflate.confirm_button.setOnClickListener {
            orderViewModel.itemShouldBeFixed = inflate.number_selection.value
            orderViewModel.order()
            dialog.dismiss()
        }
        dialog.setContentView(inflate)
        return dialog
    }

    fun clearDataDialog(
        context: Context,
        orderViewModel: OrderViewModel
    ): AlertDialog.Builder? {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("是否清空所有已经添加的数据？")
        dialog.setMessage("这个操作会将你添加的所有数据清空")
        dialog.setPositiveButton("是的") { _, _ ->
            orderViewModel.itemList.clear()
            orderViewModel.orderScene.removeAll()
            orderViewModel.saveItemData()
            orderViewModel.order()
        }
        return dialog
    }

    fun mainBottomSheetDialog(
        context: Context,
        orderViewModel: OrderViewModel
    ): BottomSheetDialog? {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_content, dialog.window?.decorView as ViewGroup, false)
        dialog.setContentView(view)
        val itemBuild = orderViewModel.orderScene.buildItem()
        val dialogAdapter = DialogAdapter(
            itemBuild,
            orderViewModel.maxColumn,
            orderViewModel.maxRow
        )
        view.dialog_grid.adapter =
            dialogAdapter
        view.dialog_grid.numColumns = orderViewModel.maxColumn
        var isSelectAll = false
        view.tv_select_all.setOnClickListener {
            for (column in itemBuild.totalSchedule.indices) {
                for (row in itemBuild.totalSchedule[0].indices) {
                    if (!itemBuild.totalSchedule[column][row] && !isSelectAll) {
                        itemBuild.add(column + 1, row + 1)
                    }
                    if (itemBuild.totalSchedule[column][row] && isSelectAll) {
                        itemBuild.remove(column + 1, row + 1)
                    }
                }
            }
            dialogAdapter.notifyDataSetChanged()
            isSelectAll = !isSelectAll
            view.tv_select_all.text = if (isSelectAll) "取消全选" else "全选"
        }
        view.materialButton.setOnClickListener {
            val editableText = view.editText.editableText
            if (editableText.isEmpty()) {
                Toast.makeText(context, "Item名不能为空", Toast.LENGTH_SHORT).show()
            } else {
                itemBuild.name = editableText.toString()
                orderViewModel.addItem(itemBuild)
                dialog.dismiss()
            }
        }
        return dialog
    }


    fun itemBottomSheetDialog(
        context: Context,
        orderViewModel: OrderViewModel, item: Item
    ): BottomSheetDialog? {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_content, dialog.window?.decorView as ViewGroup, false)
        dialog.setContentView(view)
        val copyItem = orderViewModel.orderScene.buildItem()
        copyItem.copyObject(item)
        val dialogAdapter = DialogAdapter(
            copyItem,
            orderViewModel.maxColumn,
            orderViewModel.maxRow
        )
        view.dialog_grid.adapter =
            dialogAdapter
        view.dialog_grid.numColumns = orderViewModel.maxColumn
        view.editText.setText(item.name, TextView.BufferType.EDITABLE)
        view.tv_bottom_dialog_title.text = context.getString(R.string.modify_item)
        view.materialButton.text = "确认修改"
        var isSelectAll = true
        view.tv_select_all.text = if (isSelectAll) "取消全选" else "全选"
        copyItem.totalSchedule.forEach { booleans ->
            booleans.forEach {
                if (!it) {
                    isSelectAll = false
                    view.tv_select_all.text = if (isSelectAll) "取消全选" else "全选"
                }
            }
        }
        view.tv_select_all.setOnClickListener {
            for (column in copyItem.totalSchedule.indices) {
                for (row in copyItem.totalSchedule[0].indices) {
                    if (!copyItem.totalSchedule[column][row] && !isSelectAll) {
                        copyItem.add(column + 1, row + 1)
                    }
                    if (copyItem.totalSchedule[column][row] && isSelectAll) {
                        copyItem.remove(column + 1, row + 1)
                    }
                }
            }
            dialogAdapter.notifyDataSetChanged()
            isSelectAll = !isSelectAll
            view.tv_select_all.text = if (isSelectAll) "取消全选" else "全选"
        }
        view.materialButton.setOnClickListener {
            val editableText = view.editText.editableText
            if (editableText.isEmpty()) {
                Toast.makeText(context, "Item名不能为空", Toast.LENGTH_SHORT).show()
            } else {
                copyItem.name = editableText.toString()
                orderViewModel.removeItem(item, false)
                orderViewModel.addItem(copyItem)
                dialog.dismiss()
            }
        }
        return dialog
    }


    fun fileExport(activity: Activity, viewModel: OrderViewModel) {
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("是否导出当前排班结果？")
        dialog.setMessage("需要文件读写权限，若未授权，接下来会请求授权")
        val process = MaterialDialog.Builder(activity)
            .progress(true, 100)
            .content("正在导出,请等待.....").build()
        dialog.setPositiveButton("保存本地") { _, _ ->
            activity.rxPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                successfulCallback = {
                    process.show()
                    asynDocumentExport(
                        viewModel.turnList,
                        viewModel.maxRow,
                        viewModel.maxColumn,
                        viewModel.turnCanFixed
                    ) {
                        process.dismiss()
                        val path = Environment.getExternalStorageDirectory().absolutePath
                        val fileOutputStream = FileOutputStream("${path}/排班数据.xlsx")
                        it.write(fileOutputStream)
                        fileOutputStream.close()
                        Toast.makeText(activity, "导出成功，文件在sd根目录", Toast.LENGTH_SHORT).show()
                        MaterialDialog.Builder(activity)
                            .title("是否直接打开文件")
                            .positiveText("是的")
                            .onPositive { _, _ ->
                                ShareFile.openFile(activity, File("${path}/排班数据.xlsx"))
                            }.show()
                    }
                },
                failedCallback = {
                    Toast.makeText(activity, "无法获取权限，导出失败", Toast.LENGTH_SHORT).show()
                })
        }
        dialog.setNegativeButton("直接分享") { _, _ ->
            activity.rxPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, successfulCallback = {
                process.show()
                asynDocumentExport(
                    viewModel.turnList,
                    viewModel.maxRow,
                    viewModel.maxColumn,
                    viewModel.turnCanFixed
                ) {
                    process.dismiss()
                    val path = Environment.getExternalStorageDirectory().absolutePath
                    val file = File("${path}/排班数据.xlsx")
                    val fileOutputStream = FileOutputStream(file)
                    it.write(fileOutputStream)
                    fileOutputStream.close()
                    ShareFile.shareFile(activity, file)
                }
            }) {
                Toast.makeText(activity, "无法获取权限，导出失败", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }


    fun importDialog(context: Context, orderViewModel: OrderViewModel) {
        val dialog = MaterialDialog.Builder(context)
            .customView(R.layout.dialog_import_item_name, true).build()
        dialog.apply {
            button_confirm_button.setOnClickListener {
                dialog.dismiss()
            }
            prompt_button.setOnClickListener {
                MaterialDialog
                    .Builder(context)
                    .content(R.string.import_tips)
                    .contentGravity(GravityEnum.CENTER)
                    .positiveText("知道了")
                    .onPositive { dialog, _ -> dialog.dismiss() }.show()
            }
            val s = View.OnClickListener {
                select_text_view_1.toggle()
                select_text_view_2.toggle()
                if (select_text_view_1.isChecked) {
                    select_text_view_1.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.design_default_color_background
                        )
                    )
                    select_text_view_2.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                } else {
                    select_text_view_2.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.design_default_color_background
                        )
                    )
                    select_text_view_1.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                }
            }
            select_text_view_1.setOnClickListener(s)
            select_text_view_2.setOnClickListener(s)
            val dataList = ArrayList<String>()
            et_input.addTextChangedListener {
                dataList.clear()
                val matcher = Pattern.compile("[\\u4e00-\\u9fa5]+").matcher(it.toString())
                val stringBuilder = StringBuilder()
                while (matcher.find()) {
                    stringBuilder.append(matcher.group())
                    dataList.add(matcher.group())
                    stringBuilder.append(" ")
                }
                tv_analytical_results.text = stringBuilder
                val peopleCount = "解析结果：${dataList.size}人"
                tv_population_statistics.text = peopleCount
            }
            button_confirm_button.setOnClickListener {
                dataList.forEach {
                    val item = orderViewModel.orderScene.buildItem(it)
                    if (select_text_view_2.isChecked) {
                        for (column in 1..orderViewModel.maxColumn) {
                            for (row in 1..orderViewModel.maxRow) {
                                item.add(column, row)
                            }
                        }
                    }
                    orderViewModel.addItem(item, false)
                }
                EventBus.getDefault().post(Update())
                EventBus.getDefault().post(PageTurningEvent(1))
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun addSuggestions(activity: Activity, feedbackViewModel: FeedbackViewModel): MaterialDialog {
        val dialog = MaterialDialog.Builder(activity)
            .customView(R.layout.diaolg_add_feed_back, false)
            .positiveText("提交")
            .autoDismiss(false)
            .cancelable(false)
            .negativeText("取消").build()
        dialog.apply {
            builder.onPositive { dialog, which ->
                val feedbackBean = FeedbackBean(
                    identificationcode = App.UUID,
                    content = et_feed_back_content_input.editableText.toString(),
                    title = if (radioGroup.checkedRadioButtonId == R.id.rb_function_suggestions) "功能建议" else "问题反馈",
                    status = 0
                )
                feedbackViewModel.addFeedBack(feedbackBean)
                dialog.dismiss()
            }
            builder.onNegative { dialog, which ->
                dialog.dismiss()
            }
            et_feed_back_content_input.doAfterTextChanged {
                if (it?.length ?: 0 > 999) {
                    Toast.makeText(activity, "字数超过上限不可再输入", Toast.LENGTH_SHORT).show()
                }
            }
            fun MaterialDialog.Builder.permissionCheck(): MaterialDialog.Builder {
                val rxPermission = RxPermissions(activity)
                if (!rxPermission.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    negativeText("现在授予权限")
                    onNegative { dialog, which ->
                        activity.rxPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, {}, {})
                    }
                }
                return this
            }
            tv_feed_back_prompt_button.setOnClickListener {
                MaterialDialog.Builder(activity)
                    .title("反馈须知")
                    .content(
                        "由于暂时没有账号机制，所以首次进入app生成的唯一编码则为你的唯一身份识别信息," +
                                "一旦您卸载app或者清空app数据，再次进入app时，则会重新分配唯一编码，那么您之前提交的反馈将不可见。"
                    )
                    .positiveText("我知道了")
//                    .permissionCheck()
                    .show()
            }
        }
        return dialog
    }

}