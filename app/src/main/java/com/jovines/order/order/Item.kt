package com.jovines.order.order

import java.io.Serializable
import java.util.*

/**
 * @author Jovines
 * @create 2020-04-13 3:23 下午
 * 描述: 排序单体
 *
 */
class Item(
    var name: String = "",
    private val maxColumn: Int = Order.maxColumn,
    private val maxRow: Int = Order.maxRow
) : Serializable {

    //还剩时间表
    private val suitableSchedule = Array(maxColumn) { BooleanArray(maxRow) { false } }

    //时间表
    val totalSchedule = Array(maxColumn) { BooleanArray(maxRow) { false } }

    //已经安排的轮次
    val fixedTurns: MutableList<Turn> = ArrayList()

    //可以被排到那些轮次
    private val suitableTurns = ArrayList<Turn>()

    //被安排的场景
    val orderSceneList: MutableList<OrderScene> = ArrayList()

    //时间表上的空闲时间数量
    var totalTimeCount = 0

    //还剩的空闲时间
    var freeTimeCount = 0
        private set

    //已经被安排了多少次
    var fixedCount = 0
        private set

    var tag: Any? = null

    fun addSuitableTurn(turn: Turn) {
        suitableTurns.add(turn)
    }

    fun addOrderScene(orderScene: OrderScene) {
        if (!orderSceneList.contains(orderScene)) {
            orderSceneList.add(orderScene)
        }
        if (!orderScene.items.contains(this)) {
            orderScene.addItem(this)
        }
    }

    fun removeOrderScene(orderScene: OrderScene) {
        orderSceneList.remove(orderScene)
        reset()
    }


    fun reset() {
        freeTimeCount = totalTimeCount
        fixedCount = 0
        fixedTurns.clear()
        suitableTurns.clear()
        for (i in 0 until maxColumn) {
            for (j in 0 until maxRow) {
                suitableSchedule[i][j] = totalSchedule[i][j]
            }
        }
    }

    fun copyObject(item: Item) {
        name = item.name
        for (i in 0 until maxColumn) {
            for (j in 0 until maxRow) {
                if (item.totalSchedule[i][j]) {
                    add(i + 1, j + 1)
                }
            }
        }
    }

    fun fixed(turn: Turn) {
        suitableSchedule[turn.column - 1][turn.row - 1] = false
        fixedTurns.add(turn)
        suitableTurns.remove(turn)
        fixedCount++
        freeTimeCount--
    }

    /**
     * @param column 代表星期几，从1开始
     * @param row 代表一天中的第几节课，从1开始
     */
    fun add(column: Int, row: Int): Item {
        suitableSchedule[column - 1][row - 1] = true
        totalSchedule[column - 1][row - 1] = true
        totalTimeCount++
        freeTimeCount++
        return this
    }

    /**
     * @param column 代表星期几，从1开始
     * @param row 代表一天中的第几节课，从1开始
     */
    fun remove(column: Int, row: Int): Item {
        suitableSchedule[column - 1][row - 1] = false
        totalSchedule[column - 1][row - 1] = false
        totalTimeCount--
        freeTimeCount--
        return this
    }

    fun check(column: Int, row: Int): Boolean {
        return suitableSchedule[column - 1][row - 1]
    }

    override fun toString(): String {
        return name
    }
}