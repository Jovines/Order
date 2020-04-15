package com.jovines.order.order

import java.io.Serializable
import java.util.*


/**
 * @author Jovines
 * @create 2020-04-13 3:23 下午
 * 描述:
 *      排序方法主要方法类
 *      主要运用的是贪心算法的思想
 */
object Order : Serializable {

    const val turnCanFixed = 2 //一个班次能被最多能被安排人数
    const val itemShouldBeFixed = 2 //Item应该被安排次数
    var maxColumn = 5
    var maxRow = 4

    /**
     * 主要函数，进行排班
     */
    fun order(
        items: List<Item>,
        turns: MutableList<Turn>,
        fixedTurns: MutableList<Turn>,
        turnCanFixed: Int,
        itemShouldBeFixed: Int,
        maxColumn: Int = Order.maxColumn,
        maxRow: Int = Order.maxRow
    ) {
        reset(fixedTurns, turns, items) //进行所有数据的复位
        initTurn(items, turns, maxRow, maxColumn) /*将Item分配到各自的位置*/
        /*将只有n或者n以下的班次位置上的Item进行固定(或者已经被安排n个Item的班次),并它取出*/
        pickOutShouldFixed(turns, fixedTurns, turnCanFixed, itemShouldBeFixed)
        var filterTurns: List<Turn>? = null //挑选出n+1个人班次，也就是现在turns里面还剩余的最少人数的班次
        while (true) {
            for (j in turnCanFixed + 1..Turn.maxItemCount) {
                filterTurns = filterTurns(turns, j) //重新寻找挑选出未被确定n+1人班次
                if (filterTurns.isNotEmpty()) break
            }
            if (turns.isEmpty()) break
            filterTurns?.let {
                val maxFixedTurn = selectMaxFixedCountTurn(it) //找到固定Item最多的那个班次
                val middleItems = searchNotFixedItem(maxFixedTurn) //找出未固定的Item
                maxFixedTurn.addFixedStudent(middleItems[0]) //绑定空闲班次个数最小的那个Item
                pickOutShouldFixed(turns, fixedTurns, turnCanFixed, itemShouldBeFixed) //更新
            }
        }
    }


//    /**
//     * 从班次的列表里提取出同学列表
//     *
//     * @param turns    所需要提取的轮次列表
//     * @param isRepeat 同学列表里面的元素是否可以重复
//     * @return 返回一个Item列表（并按照从Item的空闲课次的多少从高到低排列）
//     */
//    private fun extractItems(turns: List<Turn>, isRepeat: Boolean): List<Item> {
//        val people: MutableList<Item> = ArrayList()
//        var isExists = false
//        for (t in turns) {
//            for (turnItem in t.people) {
//                if (isRepeat) {
//                    people.add(turnItem)
//                } else {
//                    for (s in people) {
//                        if (s === turnItem) {
//                            isExists = true
//                        }
//                    }
//                    if (people.size == 0 || !isExists) {
//                        people.add(turnItem)
//                    }
//                    isExists = false
//                }
//            }
//        }
//        //排序
//        return people.sortedBy { it.freeTimeCount }
//    }

    /**
     * 找出班次列表中被固定的Item最多的那个班次
     *
     * @param turns 所需要寻找的班次
     * @return 被固定Item最多的那个班次
     */
    private fun selectMaxFixedCountTurn(turns: List<Turn>): Turn {
        var turn = turns[0]
        for (t in turns) {
            if (t.fixedItemCount > turn.fixedItemCount) {
                turn = t
            }
        }
        return turn
    }

    /**
     * 将Item分配到各自的位置
     */
    private fun initTurn(items: List<Item>, turns: MutableList<Turn>, maxRow: Int, maxColumn: Int) {
        for (i in 0 until maxColumn) {
            for (j in 0 until maxRow) {
                val turn = Turn(i + 1, j + 1)
                for (s in items) {
                    if (s.check(i + 1, j + 1)) {
                        turn.addStudent(s)
                    }
                }
                if (turn.itemNumber > Turn.maxItemCount) {
                    Turn.maxItemCount = turn.itemNumber
                }
                if (turn.itemNumber != 0) {
                    turns.add(turn)
                }
            }
        }
        turns.sortBy { it.allItemCanCount }
    }

    /**
     * 复位，将其中的数据清空
     *
     * @param fixedTurns 需要清空的班次列表
     * @param turns      需要清空的班次列表
     * @param items   需要重置的Item列表（这里不会清空这个列表只是会重置其中的Item对象的数据）
     */
    private fun reset(
        fixedTurns: MutableList<Turn>,
        turns: MutableList<Turn>,
        items: List<Item>
    ) {
        run {
            var i = 0
            val j = fixedTurns.size
            while (i < j) {
                fixedTurns[0].reset()
                fixedTurns.removeAt(0)
                i++
            }
        }
        var i = 0
        val j = turns.size
        while (i < j) {
            turns[0].reset()
            turns.removeAt(0)
            i++
        }
        for (item in items) {
            item.reset()
        }
    }

    /**
     * 从目标的Item列表中删除某个Item（且如果列表中有多个相同Item对象，一次执行只删除一个）
     *
     * @param goalList    目标列表
     * @param goalItem 目标Item
     */
    private fun formTurnListDeleteItem(
        goalList: MutableList<Item>,
        goalItem: Item
    ) {
        for (i in goalList.indices) {
            val s = goalList[i]
            if (goalItem === s) {
                goalList.remove(s)
                break
            }
        }
    }

    /**
     * 在未安排成功的班次列表中剔除已经确定安排了Item需要被次的同学
     * 或者删除已经安排了两个人的班次的其他无关Item
     *
     * @param turns 所需要剔除对的班次列表
     */
    private fun removeFixed(
        turns: List<Turn>,
        turnCanFixed: Int,
        itemShouldBeFixed: Int
    ) {
        for (t1 in turns) {
            //对已经固定了班次所需个人的班次删除多余那些人
            if (t1.fixedItemCount >= turnCanFixed) { //判断当前班次是否已经已经被安排了所需次
                var i = 0
                while (i < t1.people.size) {
                    //遍历当前班次的Item
                    val s1 = t1.people[i] //假如这个Items1是张三
                    var n = false
                    for (s2 in t1.fixedPeople) { //遍历当前班次所固定的Item
                        if (s1 === s2) { //张三(s1)是否是被固定在当前班次的人
                            n = true
                        }
                    }
                    if (!n) { //如果张三（s1）不是，则从当前班次的Item中删除张三
                        formTurnListDeleteItem(t1.people, s1)
                        i--
                    }
                    i++
                }
            }
            //对安排了（单个Item所要安排次数）的Item在其他班次进行剔除
            var i = 0
            while (i < t1.people.size) {
                //遍历当前班次的未固定的Item列表
                val s1 = t1.people[i] //s1为具体未固定Item
                if (s1.fixedCount >= itemShouldBeFixed) { //找出那个已经被安排了（单个Item所要安排次数）及以上的Item
                    var isExist = false
                    for (t2 in s1.fixedTurns) { //查看这个被安排了两次及以上的Item，是否是在当前班次
                        if (t1 === t2) {
                            isExist = true
                        }
                    }
                    if (!isExist) { //若这个被安排了两次的Item没有在当前班次，则在当前班次删除该Item
                        t1.people.remove(s1)
                        i--
                    }
                }
                i++
            }
        }
    }

    /**
     * 取出和固定人数为班次所需人数及以下的班次
     * 或者取出已经固定人数为班次所需人数的班次
     *
     * @param turns      待取班次列表
     * @param fixedTurns 固定班次（目标班次列表）
     * @param turnCanFixed 一个班次能被排多少人
     * @param itemShouldBeFixed 一个人能被安排多少次
     */
    private fun pickOutShouldFixed(
        turns: MutableList<Turn>,
        fixedTurns: MutableList<Turn>,
        turnCanFixed: Int,
        itemShouldBeFixed: Int
    ) {
        //保证剔除的准确性
        /*在其他班次删除那些已经安排了n次的人*/
        removeFixed(turns, turnCanFixed, itemShouldBeFixed)
        var i = 0
        while (i < turns.size) {
            //遍历整个班次列表
            val turn = turns[i]
            when {
                turn.itemNumber in 1..turnCanFixed -> { //如果当前班次的Item是0到要求人数之间，则对当前班次的Item固定
                    for (s in turn.people) {
                        turn.addFixedStudent(s)
                        removeFixed(turns, turnCanFixed, itemShouldBeFixed)
                    }
                    fixedTurns.add(turn) //将当前班次添加到固定班次列表
                    turns.remove(turn) //与此同时从未完成班次列表里删除
                    //递归，更新turns和fixedTurns
                    pickOutShouldFixed(turns, fixedTurns, turnCanFixed, itemShouldBeFixed)
                    i = 0
                }
                turn.fixedItemCount == turnCanFixed -> { //取出已经固定人数为班次最大人数的班次
                    fixedTurns.add(turn)
                    turns.remove(turn) //进行清除
                    i--
                }
                turn.itemNumber == 0 -> { //清除为空的轮次
                    turns.remove(turn) //进行清除
                    i--
                }
            }
            i++
        }
    }

    /**
     * 找到指定的班次中未被固定的Item
     *
     * @param turn 所要搜索的班次
     * @return 返回Item列表
     */
    private fun searchNotFixedItem(turn: Turn): List<Item> {
        val middleItems: MutableList<Item> = ArrayList()
        for (item in turn.people) { //遍历班次内的Item
            var m = false
            for (sF in turn.fixedPeople) { //遍历班次内已经被固定的Item
                if (sF === item) {
                    m = true
                }
            }
            if (!m) { //如果这个Item没有在班次内被确定
                middleItems.add(item)
            }
        }
        //排序，空闲班次，从小到大
        return middleItems.sortedBy { it.freeTimeCount }
    }

    /**
     * 从班次列表取出对应人数的班次
     *
     * @param items 所需要取出的班次里面存在的人数
     */
    private fun filterTurns(turns: List<Turn>, items: Int): List<Turn> {
        val filterTurns: MutableList<Turn> = ArrayList()
        for (t in turns) {
            if (t.itemNumber == items) {
                filterTurns.add(t)
            }
        }
        return filterTurns.sortedBy { it.allItemCanCount }
    }
}