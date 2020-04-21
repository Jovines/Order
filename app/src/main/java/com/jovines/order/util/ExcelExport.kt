package com.jovines.order.util

import com.jovines.order.order.Turn
import com.jovines.order.util.rxjava.setSchedulers
import io.reactivex.Observable
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook

/**
 * @author jon
 * @create 2020-04-21 11:39 AM
 *
 * 描述:
 *   Excel导出工具类
 */
object ExcelExport {

    fun asynDocumentExport(
        turnArray: Array<Turn?>,
        maxRow: Int,
        maxColumn: Int,
        oneTurnCanBeFix: Int,
        successfulCallback: (XSSFWorkbook) -> Unit
    ) {
        Observable.create<XSSFWorkbook> {
            it.onNext(documentExport(turnArray, maxRow, maxColumn, oneTurnCanBeFix))
        }.setSchedulers()
            .subscribe {
                successfulCallback(it)
            }.isDisposed
    }

    fun documentExport(
        turnArray: Array<Turn?>,
        maxRow: Int,
        maxColumn: Int,
        oneTurnCanBeFix: Int
    ): XSSFWorkbook {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("排班数据")
        val style = workbook.createCellStyle().centerCell()
        val borderMedium = CellStyle.BORDER_THIN
        style.borderTop = borderMedium//上边框
        style.borderBottom = borderMedium//下边框
        style.borderLeft = borderMedium//左边框
        style.borderRight = borderMedium//右边框
        var indexRow = 0
        Row@ while (indexRow <= maxRow * oneTurnCanBeFix) {
            val excelRow = sheet.getRow(indexRow) ?: sheet.createRow(indexRow)
            //遍历一行
            for (indexColumn in 0..maxColumn) {
                if (indexColumn == 0 && indexRow == 0) continue
                if (indexRow == 0) {
                    val excelCell = excelRow.createCell(indexColumn)
                    excelCell.setCellValue("列${indexColumn}")
                    style.borderTop = CellStyle.BORDER_THIN
                    style.borderBottom = CellStyle.BORDER_THIN
                    excelCell.cellStyle = style
                    if (indexColumn == maxColumn) {
                        indexRow++
                        continue@Row
                    }
                } else if (indexColumn == 0) {
                    for (i in 0 until oneTurnCanBeFix) {
                        val excelCell = (sheet.getRow(indexRow + i)
                            ?: sheet.createRow(indexRow + i)).createCell(indexColumn)
                        excelCell.setCellValue("行${((indexRow - 1) / oneTurnCanBeFix) + 1}")
                        val region =
                            CellRangeAddress(
                                indexRow,
                                indexRow + oneTurnCanBeFix - 1,
                                indexColumn,
                                indexColumn
                            )
                        sheet.addMergedRegion(region)
                        val mStyle: XSSFCellStyle = cellStyle(i, workbook, oneTurnCanBeFix)
                        excelCell.cellStyle = mStyle
                    }
                } else {
                    val index = ((indexRow - 1) / oneTurnCanBeFix) * (maxColumn) + (indexColumn - 1)
                    val turn = turnArray[index]
                    if (turn != null) {
                        for ((i, item) in turn.fixedPeople.withIndex()) {
                            val excelCell = (sheet.getRow(indexRow + i)
                                ?: sheet.createRow(indexRow + i)).createCell(indexColumn)
                            excelCell.setCellValue(item.name)
                            val mStyle: XSSFCellStyle = cellStyle(i, workbook, oneTurnCanBeFix)
                            excelCell.cellStyle = mStyle
                        }
                        if (turn.fixedPeople.size < oneTurnCanBeFix) {
                            for (i in (turn.fixedPeople.size) until oneTurnCanBeFix) {
                                val excelCell = (sheet.getRow(indexRow + i)
                                    ?: sheet.createRow(indexRow + i)).createCell(indexColumn)
                                val mStyle: XSSFCellStyle = cellStyle(i, workbook, oneTurnCanBeFix)
                                excelCell.cellStyle = mStyle
                            }
                        }
                    } else {
                        for (i in 0 until oneTurnCanBeFix) {
                            val excelCell = (sheet.getRow(indexRow + i)
                                ?: sheet.createRow(indexRow + i)).createCell(indexColumn)
                            val mStyle: XSSFCellStyle = cellStyle(i, workbook, oneTurnCanBeFix)
                            excelCell.cellStyle = mStyle
                        }
                    }

                }
            }
            indexRow += oneTurnCanBeFix
        }
        return workbook
    }

    private fun cellStyle(
        i: Int,
        workbook: XSSFWorkbook,
        oneTurnCanBeFix: Int
    ): XSSFCellStyle {
        return when (i) {
            0 -> {
                if (oneTurnCanBeFix != 1)
                    workbook.createCellStyle().centerCell().apply {
                        borderTop = CellStyle.BORDER_THIN
                        borderLeft = CellStyle.BORDER_THIN
                        borderRight = CellStyle.BORDER_THIN
                        borderBottom = CellStyle.BORDER_NONE
                    }
                else
                    workbook.createCellStyle().centerCell().apply {
                        borderTop = CellStyle.BORDER_THIN
                        borderLeft = CellStyle.BORDER_THIN
                        borderRight = CellStyle.BORDER_THIN
                        borderBottom = CellStyle.BORDER_THIN
                    }
            }
            oneTurnCanBeFix - 1 -> {
                workbook.createCellStyle().centerCell().apply {
                    borderTop = CellStyle.BORDER_NONE
                    borderBottom = CellStyle.BORDER_THIN
                    borderLeft = CellStyle.BORDER_THIN
                    borderRight = CellStyle.BORDER_THIN
                }
            }
            else -> {
                workbook.createCellStyle().centerCell().apply {
                    borderBottom = CellStyle.BORDER_NONE
                    borderLeft = CellStyle.BORDER_THIN
                    borderRight = CellStyle.BORDER_THIN
                    borderTop = CellStyle.BORDER_NONE
                }
            }
        }
    }

    private fun XSSFCellStyle.centerCell(): XSSFCellStyle {
        alignment = CellStyle.ALIGN_CENTER //水平居中
        verticalAlignment = CellStyle.ALIGN_CENTER //垂直居中
        return this
    }
}