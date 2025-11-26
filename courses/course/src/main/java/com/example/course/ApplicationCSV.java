package com.example.course;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVWriter;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@SpringBootApplication
public class ApplicationCSV {

    public static void nnn(String[] args) {
        //SpringApplication.run(CourseApplication.class, args);
        String inputFile = "C:/Users/dell/Downloads/Replay.csv";
        String outputFile = "C:/Users/dell/Downloads/Replay-output.csv";

        try (
                CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new FileReader(inputFile));
                CSVWriter writer = new CSVWriter(new FileWriter(outputFile))
        ) {
            List<String[]> outputRows = new ArrayList<>();

            Map<String, String> row;
            while ((row = reader.readMap()) != null) {
                String logLine = row.get("log");
                if (logLine == null || logLine.isEmpty()) continue;

                String formattedJson = extractAndFormatRequestBody(logLine);

                if (formattedJson != null) {
                    outputRows.add(new String[]{formattedJson});
                }
            }

            writer.writeAll(outputRows);
            System.out.println("✅ Completed. Cleaned JSON written to " + outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractAndFormatRequestBody(String logLine) {
        try {
            int startIndex = logLine.indexOf("\"requestBody\":\"") + 15;
            int endIndex = logLine.indexOf("\",\"functionName\"");
            if (startIndex < 15 || endIndex < 0) return null;

            String escaped = logLine.substring(startIndex, endIndex);

            String cleaned = escaped
                    .replace("\\\"", "\"")     // Handles backslash-escaped quotes
                    .replace("\\\\", "\\")     // Handles backslash escaping
                    .replace("\"\"", "\"");

            JSONObject json = new JSONObject(cleaned);
            return json.toString(); // Use json.toString(2) for pretty-print
        } catch (Exception e) {
            System.err.println("⚠️ Failed to parse: " + e.getMessage());
            return null;
        }
    }
}
