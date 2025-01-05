package org.example;

import base.CommonAPI;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CryptoQuoteFetcher extends CommonAPI {

    WebDriver driver;

    // Constructor to initialize the WebDriver
    public CryptoQuoteFetcher(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Fetch the current price of a cryptocurrency by its ticker.
     * @param ticker The ticker symbol of the cryptocurrency (e.g., BTC, ETH).
     * @return The price of the cryptocurrency as a String.
     * @throws Exception if the price element is not found.
     */
    public String fetchCryptoPrice(String ticker) throws Exception {
        // Construct the URL for the cryptocurrency
        //String url = "https://www.google.com/finance/quote/" + ticker + "-USD";
        String url = "https://finance.yahoo.com/quote/"+ticker+"-USD/";
        driver.get(url);

        // Locate the price element
        //WebElement priceElement = driver.findElement(By.cssSelector(".YMlKec.fxKbKc"));
        WebElement priceElement = driver.findElement(By.cssSelector("#nimbus-app > section > section > section > article > section.container.yf-k4z9w > div.bottom.yf-k4z9w > div.price.yf-k4z9w > section > div > section > div.container.yf-1tejb6 > fin-streamer.livePrice.yf-1tejb6 > span"));
        return priceElement.getText();
    }

    /**
     * Creates and writes cryptocurrency data to an Excel file.
     * @param tickers List of cryptocurrency tickers.
     * @param prices List of cryptocurrency prices.
     * @throws IOException if an error occurs while writing to the file.
     */
    public void writeToExcel(List<String> tickers, List<String> prices) throws IOException {
        // Date format to add to the row
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());

        // Check if the file exists, if not, create a new one
        FileInputStream fileIn;
        XSSFWorkbook workbook;
        Sheet sheet;
        int rowNum;

        try {
            fileIn = new FileInputStream("cryptocurrency_prices.xlsx");
            workbook = new XSSFWorkbook(fileIn);
            sheet = workbook.getSheetAt(0);
            rowNum = sheet.getPhysicalNumberOfRows();
        } catch (IOException e) {
            // If the file does not exist, create a new one
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Crypto Prices");
            rowNum = 0;

            // Create the header row with Date and Tickers as columns
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Date");
            for (int i = 0; i < tickers.size(); i++) {
                headerRow.createCell(i + 1).setCellValue(tickers.get(i));
            }
        }

        // Create a new row for the data
        Row dataRow = sheet.createRow(rowNum);

        // Set the date in the first column
        dataRow.createCell(0).setCellValue(currentDate);

        // Write the prices in subsequent columns
        for (int i = 0; i < prices.size(); i++) {
            dataRow.createCell(i + 1).setCellValue(prices.get(i));
        }

        // Write the data to an Excel file
        try (FileOutputStream fileOut = new FileOutputStream("cryptocurrency_prices.xlsx")) {
            workbook.write(fileOut);
        }

        // Close the workbook
        workbook.close();
    }

    public static void main(String[] args) {

        setUpConfig();
        // Configure ChromeDriver path
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver"); // Replace with your ChromeDriver path

        // Set up ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        // Instantiate CryptoQuoteFetcher
        CryptoQuoteFetcher cryptoFetcher = new CryptoQuoteFetcher(driver);

        // Create a dynamic list of cryptocurrency tickers
        List<String> cryptoTickers = new ArrayList<>();
        cryptoTickers.add(getCryptoName("crypto1"));

        //cryptoTickers.add(getCryptoName("crypto2"));

        cryptoTickers.add(getCryptoName("crypto3"));
        /*
        cryptoTickers.add(getCryptoName("crypto4"));


         */

        // Add more cryptocurrencies dynamically if needed
        // cryptoTickers.add("LTC");
        // cryptoTickers.add("XRP");

        List<String> cryptoPrices = new ArrayList<>(cryptoTickers.size());

        try {
            // Fetch prices for each cryptocurrency
            for (String ticker : cryptoTickers) {
                String price = cryptoFetcher.fetchCryptoPrice(ticker);
                cryptoPrices.add(price);
            }

            // Print cryptocurrency prices
            System.out.println("Cryptocurrency Prices:");
            for (int i = 0; i < cryptoTickers.size(); i++) {
                System.out.println(cryptoTickers.get(i) + ": " + cryptoPrices.get(i));
            }

            // Write cryptocurrency data to Excel
            cryptoFetcher.writeToExcel(cryptoTickers, cryptoPrices);
            System.out.println("Data saved to cryptocurrency_prices.xlsx");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
