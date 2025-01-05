package base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class CommonAPI {


    static Properties properties;


    public static void setUpConfig(){


        properties = new Properties();

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("config.properties");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static String getStockName(String stockName){

        return properties.getProperty(stockName);
    }

    public static String getCryptoName(String cryptoName){

        return properties.getProperty(cryptoName);
    }
}
