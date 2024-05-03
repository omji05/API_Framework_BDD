package base;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.PropertyManager;

import java.util.HashMap;
import java.util.Map;

public class RestAssuredBase {

    protected static Map<String,String> configurationPropertiesMap = new HashMap<>();

    protected static Logger logger = null;

    static {
        init();
    }

    private static synchronized void init(){
        if (logger == null){
            logger = LogManager.getLogger(RestAssuredBase.class);
        }
        if (configurationPropertiesMap.isEmpty()){
            readEnvironmentConfigurationFile();
        }

    }

    public static synchronized void readEnvironmentConfigurationFile(){
        logger.info("Reading Environment configuration file");
        PropertyManager.readKeyValueFromTargetFolder("configuration.properties");
        logger.info(configurationPropertiesMap.toString());
    }
}
