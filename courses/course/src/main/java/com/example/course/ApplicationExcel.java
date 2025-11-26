package com.example.course;


import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;


public class ApplicationExcel {

    public static void main(String[] args) {
        String inputFile = "C:/Users/dell/Downloads/Pi Post requests.xlsx";
        String outputFile = "C:/Users/dell/Downloads/Event bridge-output.xlsx";

        try (
                FileInputStream fis = new FileInputStream(inputFile);
                Workbook inputWorkbook = new XSSFWorkbook(fis);
                Workbook outputWorkbook = new XSSFWorkbook()
        ) {
            Sheet inputSheet = inputWorkbook.getSheetAt(1);
            Sheet outputSheet = outputWorkbook.createSheet("Cleaned JSON");

            Iterator<Row> rowIterator = inputSheet.iterator();
            int outputRowNum = 0;

            // Get header
            Row headerRow = rowIterator.hasNext() ? rowIterator.next() : null;
            if (headerRow == null) {
                System.err.println("❌ No header row found in input Excel file.");
                return;
            }

            int logColIndex = -1;
            for (Cell cell : headerRow) {
                if ("@timestamp,@message".equalsIgnoreCase(cell.getStringCellValue())) {
                    logColIndex = cell.getColumnIndex();
                    break;
                }
            }

            if (logColIndex == -1) {
                System.err.println("❌ 'log' column not found in header.");
                return;
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell logCell = row.getCell(logColIndex);
                if (logCell == null || logCell.getCellType() != CellType.STRING) continue;

                String logLine = logCell.getStringCellValue();
                String formattedJson = extractAndFormatRequestBody(logLine);
                if (formattedJson != null) {
                    Row outputRow = outputSheet.createRow(outputRowNum++);
                    outputRow.createCell(0).setCellValue(formattedJson);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(new File(outputFile))) {
                outputWorkbook.write(fos);
            }

            System.out.println("✅ Completed. Cleaned JSON written to " + outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractAndFormatRequestBody(String logLine) {
        try {
            // Step 1: Normalize double quotes
            logLine = logLine.replaceAll("\"\"", "\"");



            // Step 2: Find the index of "requestBody":" and start after that
            String key = "\"requestBody\\\":\\\"";
            int startIndex = logLine.indexOf(key);
            if (startIndex == -1) return null;
            startIndex += key.length();

            // Step 3: Walk forward to find the closing quote, skipping escaped quotes
            StringBuilder sb = new StringBuilder();
            boolean escaped = false;

            for (int i = startIndex; i < logLine.length(); i++) {
                char c = logLine.charAt(i);

                if (c == '\\' && !escaped) {
                    escaped = true;
                    sb.append(c);
                } else if (c == '"' && !escaped) {
                    // End of string
                    break;
                } else {
                    sb.append(c);
                    escaped = false;
                }
            }

            String escapedJson = sb.toString();

            // Step 4: Unescape JSON string
            String cleaned = escapedJson
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

            // Step 5: Optional fix for malformed JSON (e.g. trailing commas or empty fields)
            cleaned = cleaned.replaceAll("\"[^\"]+\":\\s*,", ""); // remove empty key-values

            // Step 6: Convert to JSON
            String unescaped = StringEscapeUtils.unescapeJson(cleaned);
            JSONObject json = new JSONObject(unescaped);
            return json.toString(2); // pretty print

        } catch (Exception e) {
            System.err.println("⚠️ Failed to parse: " + e.getMessage());
            return null;
        }
    }
}
