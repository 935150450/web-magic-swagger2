package com.webmagic.utils;

import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class HutoolExcelUtil {

    /**
     * 设置单元格样式
     * @param workbook
     * @return
     */
    public static CellStyle buildCellStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        // 字体样式
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
//        font.setFontName("Courier New");
        style.setFont(font);
        // 设置边框
        style.setBorderBottom(BorderStyle.THIN); // 下边框
        style.setBorderRight(BorderStyle.THIN);// 右边框
        style.setBorderLeft(BorderStyle.THIN);// 左边框
        style.setBorderTop(BorderStyle.THIN);// 上边框

        // 设置背景色
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());// 设置背景色

        // 设置自动换行;
        style.setWrapText(false);
        // 设置水平对齐的样式为 居中对齐;
        style.setAlignment(HorizontalAlignment.LEFT);
        // 设置垂直对齐的样式为居中对齐;
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setcolor
        return style;
    }



    /**
     * 设置单元格样式  - 天蓝色
     * @param workbook
     * @return
     */
    public static CellStyle buildInterfaceInfoCellStyle(Workbook workbook,short colour) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();

        // 设置背景色
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(colour);// 设置背景色
        style.setBorderBottom(BorderStyle.THIN); //下边框
        style.setBorderRight(BorderStyle.THIN); //右边框
        style.setBorderLeft(BorderStyle.THIN); //左边框
        style.setBorderTop(BorderStyle.THIN); //上边框
        // 设置垂直对齐的样式为居中对齐;
        return style;
    }


    /**
     * 设置单元格样式  - 天蓝色
     * @param workbook
     * @return
     */
    public static CellStyle buildFrameCellStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();

        // 设置背景色
        style.setBorderBottom(BorderStyle.THIN); //下边框
        style.setBorderRight(BorderStyle.THIN); //右边框
        style.setBorderLeft(BorderStyle.THIN); //左边框
        style.setBorderTop(BorderStyle.THIN); //上边框
        // 设置垂直对齐的样式为居中对齐;
        return style;
    }


    /**
     * 设置宽度
     * @param workbook
     * @return
     */
    public static CellStyle buildDescribeCellStyle(Workbook workbook) {
            CellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        //高度
        return style;
    }

    /**
     * 字体加粗
     * @param workbook
     * @return
     */
    public static CellStyle createTitleFont(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = createFont(workbook, true, false, null);
        style.setFont(font);
        //高度
        return style;
    }

    /**
     * 方法描述: 设置基础字体样式字体 这里保留最基础的样式使用
     *
     * @param bold     是否粗体
     * @param fontName 字体名称
     * @return org.apache.poi.ss.usermodel.Font
     * @author wqf
     * @date 2021/5/19 15:58
     */
    public static Font createFont(Workbook workbook, boolean bold, boolean italic, String fontName) {
        Font font = workbook.createFont();
        //设置字体名称 宋体 / 微软雅黑 /等
        font.setFontName(fontName);
        //设置是否斜体
        font.setItalic(italic);
        //设置是否加粗
        font.setBold(bold);
        return font;
    }




    /**
     * 设置单元格样式
     * @param workbook
     * @return
     */
    public static CellStyle buildRowStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 字体样式
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
//        font.setFontName("Courier New");
        style.setFont(font);
        //边框
        border(style);
       /* // 设置底边颜色
        style.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        // 设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        // 设置右边框颜色;
        style.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        // 设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());*/
        // 设置自动换行;
        style.setWrapText(false);
        // 设置水平对齐的样式为 居中对齐;
        style.setAlignment(HorizontalAlignment.LEFT);
        // 设置垂直对齐的样式为居中对齐;
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置背景色
        style.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
        return style;
    }

    private static void border(CellStyle style){
        // 设置底边框
        style.setBorderBottom(BorderStyle.THIN);
        // 设置左边框
        style.setBorderLeft(BorderStyle.THIN);
        // 设置右边框;
        style.setBorderRight(BorderStyle.THIN);
        // 设置顶边框;
        style.setBorderTop(BorderStyle.THIN);
    }

    public static StyleSet GlobalStyleSet(ExcelWriter writer, Workbook workbook) {
        //全局样式设置
        StyleSet styleSet = writer.getStyleSet();
        CellStyle cellStyle = styleSet.getCellStyle();
        //设置全局文本居中
        styleSet.setAlign(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        //设置全局字体样式
//        styleSet.setFont(font);
        //设置背景颜色 第二个参数表示是否将样式应用到头部
        styleSet.setBackgroundColor(IndexedColors.WHITE, true);
        //设置自动换行 当文本长于单元格宽度是否换行
//        styleSet.setWrapText();
        // 设置全局边框样式
        styleSet.setBorder(BorderStyle.THIN, IndexedColors.BLACK);
        return styleSet;
    }

    public static CellStyle getCellStyle(ExcelWriter writer){
        Workbook workbook = writer.getWorkbook();
        //创建样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle .setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle .setAlignment(HorizontalAlignment.LEFT);
        cellStyle .setFont(setBaseFont(workbook, true, false, "宋体", 12));
        cellStyle .setBorderBottom(BorderStyle.THIN);
        cellStyle .setBorderLeft(BorderStyle.THIN);
        cellStyle .setBorderRight(BorderStyle.THIN);
        cellStyle .setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    /**
     * 方法描述: 设置基础字体样式字体 这里保留最基础的样式使用
     *
     * @param workbook 工作簿
     * @param bold     是否粗体
     * @param fontName 字体名称
     * @param fontSize 字体大小
     * @return org.apache.poi.ss.usermodel.Font
     * @author wqf
     * @date 2021/5/19 15:58
     */
    public static Font setBaseFont(Workbook workbook, boolean bold, boolean italic, String fontName, int fontSize) {
        Font font = workbook.createFont();
        //设置字体名称 宋体 / 微软雅黑 /等
        font.setFontName(fontName);
        //设置是否斜体
        font.setItalic(italic);
        //设置字体高度
        //font.setFontHeight((short) fontHeight);
        //设置字体大小 以磅为单位
        font.setFontHeightInPoints((short) fontSize);
        //设置是否加粗
        font.setBold(bold);
        //默认字体颜色
        // font.setColor(Font.COLOR_NORMAL);
        //红色
        //font.setColor(Font.COLOR_RED);
        //设置下划线样式
        //font.setUnderline(Font.ANSI_CHARSET);
        //设定文字删除线
        //font.setStrikeout(true);
        return font;
    }

    /**
     * 方法描述: 设置基础字体样式字体 这里保留最基础的样式使用
     *
     * @param bold     是否粗体
     * @param fontName 字体名称
     * @param fontSize 字体大小
     * @return org.apache.poi.ss.usermodel.Font
     * @author wqf
     * @date 2021/5/19 15:58
     */
    public static Font createFont(ExcelWriter writer, boolean bold, boolean italic, String fontName, int fontSize) {
        Font font = writer.getWorkbook().createFont();
        //设置字体名称 宋体 / 微软雅黑 /等
        font.setFontName(fontName);
        //设置是否斜体
        font.setItalic(italic);
        //设置字体大小 以磅为单位
        font.setFontHeightInPoints((short) fontSize);
        //设置是否加粗
        font.setBold(bold);
        return font;
    }

    /**
     * 方法描述: 全局基础样式设置
     * 默认 全局水平居中+垂直居中
     * 默认 自动换行
     * 默认单元格边框颜色为黑色，细线条
     * 默认背景颜色为白色
     *
     * @param writer writer
     * @param font   字体样式
     * @return cn.hutool.poi.excel.StyleSet
     * @author wqf
     * @date 2021/5/28 10:43
     */
    public static StyleSet setBaseGlobalStyle(ExcelWriter writer, Font font) {
        //全局样式设置
        StyleSet styleSet = writer.getStyleSet();
        //设置全局文本居中
        styleSet.setAlign(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        //设置全局字体样式
        styleSet.setFont(font, true);
        //设置背景颜色 第二个参数表示是否将样式应用到头部
        styleSet.setBackgroundColor(IndexedColors.WHITE, true);
        //设置自动换行 当文本长于单元格宽度是否换行
        //styleSet.setWrapText();
        // 设置全局边框样式
        styleSet.setBorder(BorderStyle.THIN, IndexedColors.BLACK);
        return styleSet;
    }


    /**
     * 方法描述: 自适应宽度(中文支持)
     *
     * @param sheet 页
     * @param size  因为for循环从0开始，size值为 列数-1
     * @return void
     * @author wqf
     * @date 2021/5/28 14:06
     */
    public static void setSizeColumn(Sheet sheet, int size) {
        for (int columnNum = 0; columnNum <= size; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                    Cell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == CellType.STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 200);
        }
    }
}
