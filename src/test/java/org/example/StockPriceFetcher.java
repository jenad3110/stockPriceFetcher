package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockPriceFetcher {

    public static void fetchStockPrice(String tickerSymbol) {
        // Set up Chrome options for headless mode (without opening the browser window)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Optional: Run headless mode (no browser UI)

        WebDriver driver = new ChromeDriver(options);

        try {
            // URL to get stock information from Google Finance
            String url = "https://www.google.com/finance/quote/" + tickerSymbol + ":NASDAQ";
            driver.get(url);

            // Extract stock price from the page using CSS selector (you might need to adjust the selector)
            WebElement stockPriceElement = driver.findElement(By.cssSelector(".YMlKec.fxKbKc"));
            String stockPrice = stockPriceElement.getText();
            System.out.println("Stock price of " + tickerSymbol + ": " + stockPrice);

            // Get today's date in a readable format (e.g., "2025-01-02")
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            System.out.println("Today's date: " + currentDate);

            // Now we will write this stock price and date to an Excel file
            updateExcelFile(tickerSymbol, stockPrice, currentDate);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // Close the browser after the task is done
        }
    }

    // Function to write the stock price and date into an Excel file
    public static void updateExcelFile(String tickerSymbol, String stockPrice, String currentDate) {
        try {
            File file = new File("StockPrices.xlsx");
            Workbook workbook;
            Sheet sheet;

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);  // Open existing workbook
                fis.close();
            } else {
                workbook = new XSSFWorkbook();  // Create new workbook if doesn't exist
            }

            // Create a sheet named "Stock Prices" if it doesn't already exist
            sheet = workbook.getSheet("Stock Prices");
            if (sheet == null) {
                sheet = workbook.createSheet("Stock Prices");
                // Add headers to the first row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Date");
                String[] tickers = {"GOOG", "AAPL", "AMZN", "MSFT", "TSLA"};
                int colIndex = 1;
                for (String ticker : tickers) {
                    headerRow.createCell(colIndex).setCellValue(ticker);
                    colIndex++;
                }
            }

            // Check if today's date already exists in the sheet, if yes, do not add a new row
            boolean dateExists = false;
            for (Row row : sheet) {
                Cell dateCell = row.getCell(0);
                if (dateCell != null && dateCell.getStringCellValue().equals(currentDate)) {
                    dateExists = true;
                    break;
                }
            }

            if (!dateExists) {
                // Clear the second row (if it exists) to update it with today's data
                Row row = sheet.getRow(1);
                if (row == null) {
                    row = sheet.createRow(1);
                }

                // Add date for the new row (first column)
                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(currentDate);

                // Add stock prices for each ticker symbol in the corresponding column
                String[] tickers = {"GOOG", "AAPL", "AMZN", "MSFT", "TSLA"};
                int colIndex = 1;  // Start from column 1 for tickers

                for (String ticker : tickers) {
                    if (ticker.equals(tickerSymbol)) {
                        // Add the stock price of the current ticker to the correct column
                        Cell priceCell = row.createCell(colIndex);
                        priceCell.setCellValue(stockPrice);
                    }
                    colIndex++;
                }

                // Write the updated data to the Excel file
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();

                System.out.println("Stock price and date written to Excel file.");
            } else {
                System.out.println("Today's data is already in the sheet.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // List of tickers you want to track
        String[] tickers = {"GOOG", "AAPL", "AMZN", "MSFT", "TSLA"};

        // Fetch stock prices for each ticker
        for (String ticker : tickers) {
            fetchStockPrice(ticker);
        }
    }
}
