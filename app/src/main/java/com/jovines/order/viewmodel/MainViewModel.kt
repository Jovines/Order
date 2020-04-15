package com.jovines.order.viewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jovines.order.App
import com.jovines.order.bean.ItemDataBean
import com.jovines.order.order.Item
import com.jovines.order.order.Order
import com.jovines.order.order.OrderScene
import com.jovines.order.order.Turn
import com.jovines.order.util.rxjava.ExecuteOnceObserver
import com.jovines.order.util.rxjava.setSchedulers
import com.jovines.order.util.defaultSharedPreferences
import com.jovines.order.util.editor
import io.reactivex.Observable

/**
 * @author Jovines
 * @create 2020-04-14 2:26 PM
 *
 * 描述:
 *
 */
class MainViewModel : ViewModel() {


    companion object {
        const val orderSceneTag = "orderSceneTag"
        const val maxRowTag = "maxRowTag"
        const val maxColumnTag = "maxColumnTag"
    }

    var orderScene: OrderScene = OrderScene(
        "默认",
        maxColumn = App.context.defaultSharedPreferences.getInt(maxColumnTag, Order.maxColumn),
        maxRow = App.context.defaultSharedPreferences.getInt(maxRowTag, Order.maxRow)
    )

    private val gson = Gson()

    //用于做缓存
    var itemList: ArrayList<ItemDataBean> = gson.fromJson<ArrayList<ItemDataBean>>(
        App.context.defaultSharedPreferences.getString(
            orderSceneTag,
            ""
        ), object : TypeToken<ArrayList<ItemDataBean>>() {}.type
    ) ?: arrayListOf()

    var maxRow = orderScene.maxRow
        set(value) {
            field = value
            orderScene.maxRow = value
            App.context.defaultSharedPreferences.editor {
                putInt(maxRowTag, value)
            }
        }

    var maxColumn = orderScene.maxColumn
        set(value) {
            field = value
            orderScene.maxColumn = value
            App.context.defaultSharedPreferences.editor {
                putInt(maxColumnTag, value)
            }
        }

    lateinit var turnList: Array<Turn?>

    fun addItem(item: Item, successCallBack: () -> Unit) {
        Observable.create<Any> {
            orderScene.addItem(item)
            itemList.add(ItemDataBean(item))
            buildTurnList()
            saveItemData()
            it.onNext(Any())
        }
            .setSchedulers()
            .subscribe(
                ExecuteOnceObserver(
                    onExecuteOnceNext = {
                        successCallBack()
                    }
                )
            )
    }

    fun saveItemData() {
        App.context.defaultSharedPreferences.editor {
            putString(orderSceneTag, gson.toJson(itemList))
        }
    }

    private fun buildTurnList() {
        for (i in turnList.indices) {
            val row = i / orderScene.maxColumn + 1
            val column = i % orderScene.maxColumn + 1
            val filter = orderScene.fixedTurns.filter { it.row == row && it.column == column }
            turnList[i] = if (filter.isNotEmpty()) filter[0] else null
        }
    }


    fun order(successCallBack: () -> Unit) {
        Observable.create<Any> {
            orderScene.order()
            buildTurnList()
            it.onNext(Any())
        }
            .setSchedulers()
            .subscribe(
                ExecuteOnceObserver(
                    onExecuteOnceNext = {
                        successCallBack()
                    }
                )
            )
    }

}