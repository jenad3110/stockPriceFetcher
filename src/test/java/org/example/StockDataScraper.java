package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockDataScraper {
    public static void main(String[] args) {
        // Configure ChromeDriver path
        //System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver"); // Replace with the path to your ChromeDriver

        // Set up ChromeDriver with headless mode (optional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        // Define stock tickers
        String[] tickers = {"GOOG", "AAPL", "AMZN", "MSFT", "TSLA"};
        String[] stockPrices = new String[tickers.length];

        try {
            // Fetch stock prices for each ticker
            for (int i = 0; i < tickers.length; i++) {
                String ticker = tickers[i];
                String url = "https://www.google.com/finance/quote/" + ticker + ":NASDAQ";
                driver.get(url);

                // Locate stock price element
                WebElement priceElement = driver.findElement(By.cssSelector(".YMlKec.fxKbKc"));
                stockPrices[i] = priceElement.getText(); // Extract the stock price text
            }

            // Get today's date
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Write stock data to a CSV file
            try (FileWriter csvWriter = new FileWriter("StockPrices.csv", true)) {
                // Write header row if the file is empty
                csvWriter.append("Date,GOOG,AAPL,AMZN,MSFT,TSLA\n");

                // Write the stock data
                csvWriter.append(currentDate).append(",");
                for (String price : stockPrices) {
                    csvWriter.append(price).append(",");
                }
                csvWriter.append("\n");
            }

            System.out.println("Stock data saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the WebDriver
            driver.quit();
        }
    }
}
