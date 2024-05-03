package utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataHolder {

    public static final Map<String,String> dataMap = new HashMap<>();

    public static void setProperty(String attribute,String value){ dataMap.put(attribute, value);}

    public static void setProperties(Map<String,String> map){ dataMap.putAll(map);}

    public static String getProperty(String attribute){ return dataMap.get(attribute);}
    public static Set<Map.Entry<String,String>> getAllProperty(){return dataMap.entrySet();}


}
