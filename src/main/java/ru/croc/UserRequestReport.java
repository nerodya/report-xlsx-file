package ru.croc;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserRequestReport {

    private final String FILE_PATH_NAME = "src/requests/"
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-MM")) + " Requests.xlsx";

    public static void main(String[] args) {

        var userRequestReport = new UserRequestReport();
        var data = userRequestReport.readData();
        userRequestReport.createReport(data);
    }

    private List<String[]> readData () {

        List<String[]> data = new ArrayList<>();
        try (BufferedReader inputStream = new BufferedReader(new FileReader("src/main/resources/TestData.csv"))) {
            String line;
            while ((line = inputStream.readLine()) != null){
                data.add((line.split(";", -RequestColumn.values().length)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private void createReport(List<String[]> data) {

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(FILE_PATH_NAME)) {

            var sheet = workbook.createSheet("Отчет");
            var style = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            sheet.setDefaultColumnWidth(20);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setWrapText(true);
            style.setFont(font);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ЗАГОЛОВОК ТАБЛИЦЫ
            var row = 0;
            var rowHeader = sheet.createRow(row);
            rowHeader.setHeightInPoints(60);
            for (int i = 0; i < RequestColumn.values().length; i++) {
                rowHeader.createCell(i);
            }
            rowHeader.getCell(0).setCellValue("Перечень обращений за период");
            rowHeader.getCell(0).setCellStyle(style);
            sheet.addMergedRegion(new CellRangeAddress(
                    row,0,0, RequestColumn.values().length - 1));
            row++;

            // ЗАГОЛОВКИ СТОЛБЦОВ
            var rowWithRequestColumn = sheet.createRow(row);
            rowWithRequestColumn.setHeightInPoints(40);
            row++;
            for (var header : RequestColumn.values()) {
                var cell = rowWithRequestColumn.createCell(header.getNumberColumn());
                cell.setCellValue(header.getName());
                cell.setCellStyle(style);
            }

            var styleCell = workbook.createCellStyle();
            styleCell.setBorderTop(BorderStyle.THIN);
            styleCell.setBorderBottom(BorderStyle.THIN);
            styleCell.setBorderLeft(BorderStyle.THIN);
            styleCell.setBorderRight(BorderStyle.THIN);
            // ФОРМИРОВАНИЕ СТРОК ДАННЫХ
            var id = 1;
            for (var line : data) {
                var rowWithData = sheet.createRow(row++);
                rowWithData.createCell(RequestColumn.NUMBER.getNumberColumn()).setCellValue(id++);
                Arrays.stream(RequestColumn.values()).filter(x -> x.getNumberColumn() != 0).forEach(x -> {
                    var cell = rowWithData.createCell(x.getNumberColumn());
                    cell.setCellValue(line[x.getNumberColumn()]);
                    cell.setCellStyle(styleCell);
                });
            }

            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}