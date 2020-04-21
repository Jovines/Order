package com.jovines.order.viewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jovines.order.App
import com.jovines.order.bean.ItemDataBean
import com.jovines.order.event.Update
import com.jovines.order.order.Item
import com.jovines.order.order.Order
import com.jovines.order.order.OrderScene
import com.jovines.order.order.Turn
import com.jovines.order.util.defaultSharedPreferences
import com.jovines.order.util.editor
import org.greenrobot.eventbus.EventBus

/**
 * @author Jovines
 * @create 2020-04-14 2:26 PM
 *
 * 描述:
 *
 */
class OrderViewModel : ViewModel() {


    companion object {
        const val orderSceneTag = "orderSceneTag"
        const val maxRowTag = "maxRowTag"
        const val maxColumnTag = "maxColumnTag"
        const val turnCanFixedTag = "turnCanFixedTag"
        const val itemShouldBeFixedTag = "itemShouldBeFixedTag"
    }

    var orderScene: OrderScene = OrderScene(
        "默认",
        maxColumn = App.context.defaultSharedPreferences.getInt(maxColumnTag, Order.maxColumn),
        maxRow = App.context.defaultSharedPreferences.getInt(maxRowTag, Order.maxRow),
        itemShouldBeFixed = App.context.defaultSharedPreferences.getInt(
            itemShouldBeFixedTag,
            Order.itemShouldBeFixed
        ),
        turnCanFixed = App.context.defaultSharedPreferences.getInt(
            turnCanFixedTag,
            Order.turnCanFixed
        )
    )
    var itemShouldBeFixed: Int = orderScene.itemShouldBeFixed
        set(value) {
            field = value
            orderScene.itemShouldBeFixed = value
            App.context.defaultSharedPreferences.editor {
                putInt(itemShouldBeFixedTag, value)
            }
        }

    var turnCanFixed = orderScene.turnCanFixed
        set(value) {
            field = value
            orderScene.turnCanFixed = value
            App.context.defaultSharedPreferences.editor {
                putInt(turnCanFixedTag, value)
            }
        }

    private val gson = Gson()

    //用于做缓存
    var itemList: ArrayList<ItemDataBean> = gson.fromJson<ArrayList<ItemDataBean>>(
        App.context.defaultSharedPreferences.getString(
            orderSceneTag,
            ""
        ), object : TypeToken<ArrayList<ItemDataBean>>() {}.type
    ) ?: arrayListOf()

    /**
     * 最大行数
     */
    var maxRow = orderScene.maxRow
        set(value) {
            field = value
            orderScene.maxRow = value
            App.context.defaultSharedPreferences.editor {
                putInt(maxRowTag, value)
            }
        }

    /**
     * 最大列数
     */
    var maxColumn = orderScene.maxColumn
        set(value) {
            field = value
            orderScene.maxColumn = value
            App.context.defaultSharedPreferences.editor {
                putInt(maxColumnTag, value)
            }
        }

    lateinit var turnList: Array<Turn?>

    init {
        restoreCache()
    }

    /**
     * 添加item的
     *
     * @param item 需要添加的Item
     * @param isRefresh 添加之后是否需要刷新视图
     */
    fun addItem(item: Item, isRefresh: Boolean = true) {
        orderScene.addItem(item)
        itemList.add(ItemDataBean(item))
        buildTurnList()
        saveItemData()
        if (isRefresh)
            EventBus.getDefault().post(Update())
    }


    /**
     * 删除item的函数
     * @param item 需要删除的Item
     * @param isRefresh 删除之后是否需要刷新视图
     */
    fun removeItem(item: Item, isRefresh: Boolean = true) {
        orderScene.removeItem(item)
        itemList.remove(item.tag)
        buildTurnList()
        saveItemData()
        if (isRefresh)
            EventBus.getDefault().post(Update())
    }


    /**
     * 保存缓存数据
     */
    fun saveItemData() {
        App.context.defaultSharedPreferences.editor {
            putString(orderSceneTag, gson.toJson(itemList))
        }
    }

    /**
     * 重新建造[turnList]
     */
    private fun buildTurnList() {
        for (i in turnList.indices) {
            val row = i / orderScene.maxColumn + 1
            val column = i % orderScene.maxColumn + 1
            val filter = orderScene.fixedTurns.filter { it.row == row && it.column == column }
            turnList[i] = if (filter.isNotEmpty()) filter[0] else null
        }
    }

    /**
     * 清空所有缓存，并发送更新信号
     */
    fun clearAll() {
        itemList.clear()
        orderScene.removeAll()
        saveItemData()
        restoreCache()
        EventBus.getDefault().post(Update())
    }

    /**
     * 从缓存重建用于构造显示主要排序的控件数据[turnList]
     */
    private fun restoreCache() {
        //恢复缓存
        orderScene.removeAll()
        turnList = arrayOfNulls(maxColumn * maxRow)
        if (itemList.isNotEmpty()) {
            itemList.forEach {
                orderScene.addItem(it.toItem(orderScene))
            }
        }
    }

    /**
     * 行列规格没有更改的话，直接调用这个方法就可以更新所有视图
     */
    fun order() {
        orderScene.order()
        buildTurnList()
        EventBus.getDefault().post(Update())
    }

}