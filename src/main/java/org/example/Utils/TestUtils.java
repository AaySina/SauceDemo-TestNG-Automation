package org.example.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestUtils {

    // 1. Inisialisasi Logger
    private static final Logger log = LogManager.getLogger(TestUtils.class);

    /**
     * Membaca data dari Excel untuk TestNG DataProvider (Indikator Data Driven)
     @param filePath Path ke file Excel
     @param sheetName Nama sheet
     @return Object[][] untuk DataProvider
     */
    public static Object[][] getTestData(String filePath, String sheetName) {
        Object[][] data = null;
        File excelFile = new File(filePath);

        // Cek keberadaan file di awal
        if (!excelFile.exists()) {
            log.error("File Excel tidak ditemukan di: {}", filePath);
            throw new RuntimeException("File Excel tidak ditemukan: " + filePath);
        }

        log.info("Attempting to load test data from: {} (Sheet: {})", filePath, sheetName);

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                log.error("Sheet '{}' tidak ditemukan di file: {}", sheetName, filePath);
                throw new RuntimeException("Sheet '" + sheetName + "' tidak ditemukan di file: " + filePath);
            }

            // Hitung jumlah baris data (skip header di row 0)
            int rowCount = sheet.getLastRowNum();
            if (rowCount == 0) {
                log.warn("Sheet '{}' kosong. Mengembalikan array kosong.", sheetName);
                return new Object[0][0];
            }

            // Dapatkan jumlah kolom dari baris header (row 0)
            int colCount = sheet.getRow(0).getLastCellNum();

            // Inisialisasi array
            data = new Object[rowCount][colCount];

            // Baca data mulai dari row 1 (skip header di row 0)
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < colCount; j++) {
                        Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        data[i - 1][j] = getCellValue(cell);
                    }
                }
            }

            log.info("Successfully loaded {} test data rows from sheet: {}", rowCount, sheetName);

        } catch (IOException e) {
            log.error("Error reading Excel file: {}", filePath, e);
            throw new RuntimeException("Failed to read Excel file: " + e.getMessage());
        }

        return data;
    }

    /**
     * Helper method untuk mendapatkan nilai cell
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                // Mengembalikan nilai numerik sebagai String
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}