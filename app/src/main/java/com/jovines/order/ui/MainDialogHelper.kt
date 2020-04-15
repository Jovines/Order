package com.jovines.order.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovines.order.R
import com.jovines.order.adapter.DialogAdapter
import com.jovines.order.order.Item
import com.jovines.order.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.dialog_content.view.*
import kotlinx.android.synthetic.main.number_of_people.view.*
import kotlinx.android.synthetic.main.specification_setting.view.*
import kotlinx.android.synthetic.main.specification_setting.view.confirm_button

/**
 * @author Jovines
 * @create 2020-04-15 2:32 PM
 *
 * 描述:
 *
 */
object MainDialogHelper {


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
        inflate.number_selection.maxValue = 8
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
        inflate.number_selection.maxValue = 8
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
        dialog.setPositiveButton("是的") { dialogInterface, i: Int ->
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
        view.dialog_grid.adapter =
            DialogAdapter(
                itemBuild,
                orderViewModel.maxColumn,
                orderViewModel.maxRow
            )
        view.dialog_grid.numColumns = orderViewModel.maxColumn
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
        view.dialog_grid.adapter =
            DialogAdapter(
                copyItem,
                orderViewModel.maxColumn,
                orderViewModel.maxRow
            )
        view.dialog_grid.numColumns = orderViewModel.maxColumn
        view.editText.setText(item.name, TextView.BufferType.EDITABLE)
        view.tv_bottom_dialog_title.text = context.getString(R.string.modify_item)
        view.materialButton.text = "确认修改"
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


}