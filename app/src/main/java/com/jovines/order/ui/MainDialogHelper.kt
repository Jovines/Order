package com.jovines.order.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovines.order.R
import com.jovines.order.adapter.DialogAdapter
import com.jovines.order.order.Order
import com.jovines.order.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main_content.*
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
class MainDialogHelper(var mainActivity: MainActivity?,var mainViewModel: MainViewModel?) : LifecycleObserver {


    fun specificationSettingDialog(): AppCompatDialog {
        val dialog = AppCompatDialog(mainActivity)
        val inflate = LayoutInflater.from(mainActivity).inflate(
            R.layout.specification_setting,
            dialog.window?.decorView as ViewGroup,
            false
        )
        inflate.row_number_picker.maxValue = 12
        inflate.column_number_picker.maxValue = 12
        inflate.row_number_picker.minValue = 1
        inflate.column_number_picker.minValue = 1
        inflate.row_number_picker.value = mainViewModel?.maxRow?:Order.maxRow
        inflate.column_number_picker.value = mainViewModel?.maxColumn?:Order.maxColumn

        inflate.confirm_button.setOnClickListener {
            val row = inflate.row_number_picker.value
            val column = inflate.column_number_picker.value
            mainViewModel?.maxColumn = column
            mainViewModel?.maxRow = row
            mainViewModel?.itemList?.clear()
            mainViewModel?.orderScene?.removeAll()
            mainViewModel?.saveItemData()
            mainActivity?.initData()
            dialog.dismiss()
        }
        dialog.setContentView(inflate)
        return dialog
    }


    fun numberOfPeopleDialog(): AppCompatDialog {
        val dialog = AppCompatDialog(mainActivity)
        val inflate = LayoutInflater.from(mainActivity).inflate(
            R.layout.number_of_people,
            dialog.window?.decorView as ViewGroup,
            false
        )
        inflate.number_selection.maxValue = 8
        inflate.number_selection.minValue = 1
        inflate.number_selection.value = mainViewModel?.orderScene?.turnCanFixed?:Order.turnCanFixed
        inflate.confirm_button.setOnClickListener {
            mainViewModel?.orderScene?.turnCanFixed = inflate.number_selection.value
            mainActivity?.initData()
            dialog.dismiss()
        }
        dialog.setContentView(inflate)
        return dialog
    }

    fun timeSettingDialog(): AppCompatDialog {
        val dialog = AppCompatDialog(mainActivity)
        val inflate = LayoutInflater.from(mainActivity).inflate(
            R.layout.number_of_people,
            dialog.window?.decorView as ViewGroup,
            false
        )
        inflate.number_selection.maxValue = 8
        inflate.number_selection.minValue = 1
        inflate.number_selection.value = mainViewModel?.orderScene?.itemShouldBeFixed?:Order.itemShouldBeFixed
        inflate.textView3.text = "一个Item能被排"
        inflate.textView11.text = "次"
        inflate.confirm_button.setOnClickListener {
            mainViewModel?.orderScene?.itemShouldBeFixed = inflate.number_selection.value
            mainActivity?.initData()
            dialog.dismiss()
        }
        dialog.setContentView(inflate)
        return dialog
    }

    fun clearDataDialog(): AlertDialog.Builder? {
        mainActivity?:return null
        val dialog = AlertDialog.Builder(mainActivity!!)
        dialog.setTitle("是否清空所有已经添加的数据？")
        dialog.setMessage("这个操作会将你添加的所有数据清空")
        dialog.setPositiveButton("是的") { dialogInterface, i: Int ->
            mainViewModel?.itemList?.clear()
            mainViewModel?.orderScene?.removeAll()
            mainViewModel?.saveItemData()
            mainViewModel?.order {
                (mainActivity?.order_content?.adapter as? RecyclerView.Adapter)?.notifyDataSetChanged()
            }
        }
        return dialog
    }

    fun mainBottomSheetDialog(): BottomSheetDialog? {
        mainActivity?:return null
        val dialog = BottomSheetDialog(mainActivity!!, R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(mainActivity)
            .inflate(R.layout.dialog_content, dialog.window?.decorView as ViewGroup, false)
        dialog.setContentView(view)
        val itemBuild = mainViewModel?.orderScene?.ItemBuild()
        itemBuild?:return null
        view.dialog_grid.adapter =
            DialogAdapter(itemBuild, mainViewModel?.maxColumn?:Order.maxColumn, mainViewModel?.maxRow?:Order.maxRow)
        view.dialog_grid.numColumns = mainViewModel?.maxColumn?:Order.maxColumn
        view.materialButton.setOnClickListener {
            itemBuild.name = view.editText.editableText.toString()
            mainViewModel?.addItem(itemBuild.build()) {
                (mainActivity?.order_content?.adapter as RecyclerView.Adapter).notifyDataSetChanged()
            }
            dialog.dismiss()
        }
        return dialog
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        mainActivity = null
        mainViewModel= null
    }

}