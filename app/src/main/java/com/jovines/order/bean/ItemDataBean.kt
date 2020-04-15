package com.jovines.order.bean

import com.jovines.order.order.Item
import com.jovines.order.order.OrderScene

/**
 * @author Jovines
 * @create 2020-04-15 10:40 AM
 *
 * 描述:
 *
 */

class ItemDataBean(item: Item? = null) {
    private val timeList: ArrayList<Int> = arrayListOf()
    var name: String = ""

    init {
        item?.let { build(item) }
    }


    /**
     * 通过Item来构建储存数据对象
     */
    fun buildDataBean(item: Item): ItemDataBean {
        build(item)
        return this
    }

    private fun build(item: Item) {
        name = item.name
        for ((i, valueColumn) in item.totalSchedule.withIndex()) {
            for ((j, valueRow) in valueColumn.withIndex()) {
                if (valueRow) {
                    timeList.add(i + 1)//列
                    timeList.add(j + 1)//行
                }
            }
        }
    }

    /**
     * 构建的依附于场景的item
     */
    fun toItem(orderScene: OrderScene): Item {
        val item = Item(name,maxColumn = orderScene.maxColumn,maxRow = orderScene.maxRow)
        for (i in timeList.indices) {
            if (i % 2 == 1) {
                item.add(timeList[i - 1], timeList[i])
            }
        }
        return item
    }
}