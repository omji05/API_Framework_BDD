package utility;

import base.RestAssuredBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyManager extends RestAssuredBase {

    public static void readKeyValueFromTargetFolder(String filepath){
        PropertyManager propManager = new PropertyManager();
        Properties prop = propManager.loadPropertiesFile(filepath);
        prop.forEach((k,v)->configurationPropertiesMap.put(k.toString(),v.toString()));
    }

    public Properties loadPropertiesFile(String filepath){
        Properties prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + File.separator +filepath);
            if (fis !=null){
                prop.load(fis);
                fis.close();
            }
            else{
                logger.info("Error while reading the config file");
            }
        } catch (FileNotFoundException e) {
            logger.info("File not found at given path.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop;
    }
}
