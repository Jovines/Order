package com.jovines.order.order

import java.util.*

/**
 * @author Jovines
 * 描述：对Order类进行封装的方便类
 * @param name 排序场景名
 * @param turnCanFixed 一个场景能被安排多少人
 * @param itemShouldBeFixed 一个人能被安排多少次
 * @param maxRow 有多少行
 * @param maxColumn 有多少列
 */

class OrderScene(
    private val name: String,
    var turnCanFixed: Int = Order.turnCanFixed,
    var itemShouldBeFixed: Int = Order.itemShouldBeFixed,
    var maxRow: Int = Order.maxRow,
    var maxColumn: Int = Order.maxColumn
) {


    //总的Item列表，如不是删除和添加Item或者改变次序不能轻易改变
    var items: MutableList<Item> = ArrayList()

    //还未排好的班次列表
    private var turns = ArrayList<Turn>()

    //已经排好的班次列表
    var fixedTurns = ArrayList<Turn>()


    fun removeItem(item: Item) =
        if (unBind(item)) {
            order()
            true
        } else false

    fun removeAll() {
        for (i in items.map { it }) {
            unBind(i)
        }
    }

    fun addItem(item: Item) =
        //互相绑定
        if (binding(item)) {
            order()
            true
        } else false


    /**
     * 如果要使用OrderScene那么最好使用这个来构建每一个item
     *
     * @param name item 的名字
     * @param time 所在行列，，第一个值是列，第二个值是行
     * @return 返回一个构建好的item
     */
    fun buildItem(name: String = "", vararg time: Pair<Int, Int>): Item {
        val item = Item(name, maxColumn, maxRow)
        time.forEach {
            item.add(it.second, it.first)
        }
        return item
    }

    inner class ItemBuild {
        var name: String = ""
            set(value) {
                field = value
                item.name = value
            }

        private val item = Item(maxColumn = maxColumn, maxRow = maxRow)


        fun add(column: Int, row: Int) {
            item.add(column, row)
        }

        fun remove(column: Int, row: Int) {
            item.remove(column, row)
        }


        fun build() = item
    }

    /**
     * @param item 需要绑定的item
     * @return 返回绑定结果
     */
    private fun binding(item: Item): Boolean {
        return if (!this.items.contains(item) && !item.orderSceneList.contains(this)) {
            this.items.add(item)
            item.addOrderScene(this)
            true
        } else false
    }

    /**
     * @param item 需要解绑的item
     * @return 返回解绑结果
     */
    private fun unBind(item: Item): Boolean {
        return if (this.items.contains(item) && item.orderSceneList.contains(this)) {
            this.items.remove(item)
            item.removeOrderScene(this)
            true
        } else false
    }

    /**
     * 调用Order的排序
     */
    fun order() {
        Order.order(items, turns, fixedTurns, turnCanFixed, itemShouldBeFixed, maxColumn, maxRow)
    }

    override fun toString(): String {
        return StringBuilder().apply {
            append("$name 场景：\n")
            append("====================\n")
            append("未排序：\n")
            if (turns.isNotEmpty()) {
                for (i in turns) {
                    append(i.toString() + "\n")
                }
            } else {
                append("所有人已经排序\n")
            }
            append("====================\n")
            append("已排序：\n")
            if (fixedTurns.isNotEmpty()) {
                for (i in fixedTurns) {
                    append(i.toString() + "\n")
                }
            } else {
                append("还未有排序成功的人")
            }
            append("====================\n")
        }.toString()
    }
}