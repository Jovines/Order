package com.jovines.order.order

import java.io.Serializable

/**
 * @author Jovines
 * @create 2020-04-13 3:23 下午
 * 描述:
 * @param column 该轮次在多少列，从1开始
 * @param row 该轮次在多少行，从1开始
 */
class Turn(val column: Int, val row: Int) : Serializable {

    val people = ArrayList<Item>()
    val fixedPeople = ArrayList<Item>()

    /**
     * 获取当前班次所有Item的空闲班次的总和
     *
     * @return Item们所剩空闲时间总和
     */
    val allItemCanCount: Int
        get() {
            var n = 0
            for (s in people) {
                n += s.freeTimeCount
            }
            return n
        }

    fun addStudent(item: Item) {
        people.add(item)
        item.addSuitableTurn(this)
    }


    fun reset() {
        maxItemCount = 0
        people.clear()
        fixedPeople.clear()
    }

    /**
     * 把班次和Item进行互相绑定
     *
     * @param item 需要绑定的Item
     */
    fun addFixedStudent(item: Item) {
        if (!isFixedExists(item)) {
            item.fixed(this)
            fixedPeople.add(item)
        }
        if (item.fixedCount > 2) {
            println()
        }
    }

    private fun isFixedExists(item: Item): Boolean {
        var n = false
        for (s in fixedPeople) {
            if (item == s) {
                n = true
            }
        }
        return n
    }

    val itemNumber: Int
        get() = people.size


    val fixedItemCount: Int
        get() = fixedPeople.size


    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("${row}行${column}列：")
        for (s in fixedPeople) {
            stringBuilder.append(s.name + " ")
        }
        return stringBuilder.toString()
    }

    companion object {
        var maxItemCount = 0
    }
}