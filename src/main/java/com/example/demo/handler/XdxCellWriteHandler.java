package com.example.demo.handler;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;
import java.util.Map;

public class XdxCellWriteHandler implements CellWriteHandler {

    /**
     * 超过 LIMIT 的大小就使用 sheet关联下拉，否则直接设置下拉
     */
    private static final Integer LIMIT = 100;

    private final Map<String, List<String>> dropDownData;

    public XdxCellWriteHandler(Map<String, List<String>> dropDownData) {
        this.dropDownData = dropDownData;
    }

    /**
     * 设置下拉框数据
     * @param key 当前列名
     * @param rowIndex 行号
     * @param columnIndex 列号
     */
    private void setSelectDataList(WriteSheetHolder writeSheetHolder, String key, int rowIndex, int columnIndex) {
        if (dropDownData.get(key) == null) {
            return;
        }

        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();

        // 设置下拉列表的行： 首行，末行，首列，末列
        CellRangeAddressList rangeList = new CellRangeAddressList(rowIndex, 50000, columnIndex, columnIndex);
        // 设置下拉列表的值
        DataValidationConstraint constraint;
        if (dropDownData.get(key).size() < LIMIT) {
            // 直接设置下拉选
            constraint = helper.createExplicitListConstraint(dropDownData.get(key).toArray(new String[0]));

        } else {
            // 联动到另外一个 sheet
            constraint = helper.createFormulaListConstraint(key+ "!$A$1:$A$" + dropDownData.get(key).size());
        }

        // 设置约束
        DataValidation validation = helper.createValidation(constraint, rangeList);
        // 阻止输入非下拉选项的值
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        validation.createErrorBox("提示", "请输入下拉选项中的内容");
        sheet.addValidationData(validation);
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                 List<WriteCellData<?>> cellDataList, Cell cell, Head head,
                                 Integer relativeRowIndex, Boolean isHead) {

        // 设置隐藏 sheet
        WriteSheet writeSheet = writeSheetHolder.getWriteSheet();
        if (writeSheet.getSheetNo() > 0) {
            Workbook workbook = writeSheetHolder.getParentWriteWorkbookHolder().getWorkbook();
            workbook.setSheetHidden(writeSheet.getSheetNo(), true);
            return;
        }

        if (!isHead) {
            //设置value下拉框
            setSelectDataList(writeSheetHolder, head.getFieldName(), cell.getRowIndex(), cell.getColumnIndex());
        }
    }
}