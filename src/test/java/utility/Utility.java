package utility;

import base.RestAssuredBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.io.*;
import java.net.URL;
import java.util.*;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

public class Utility extends RestAssuredBase {

    public static String getProperty(String propertyName) {
        Properties prop = new Properties();
        FileInputStream fis=null;
        String value=null;
        String fpath = System.getProperty("user.dir")+File.separator+"src/test/resources/configFiles/propertiesFile/static.properties";
        try {
            fis = new FileInputStream(fpath);
            prop.load(fis);
            value = prop.getProperty(propertyName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public static String updateRequestBody(DataTable dataTable,String jsonBody) {
        List<List<String>> tableLists = dataTable.asLists();
        for(int i=1;i<tableLists.size();i++){
            List<String> row = tableLists.get(i);
            switch(row.get(0)){
                case "update":
                    jsonBody = Utility.updateKeyValueInJson(jsonBody,row.get(1),row.get(2));
                    break;
                case "add":
                    jsonBody = Utility.addKeyInJson(jsonBody,row.get(1),row.get(2));
                    break;
                case "remove":
                    jsonBody = Utility.removeKeyFromJson(jsonBody,row.get(1));
                    break;
                default:
                    logger.info(row.get(0)+"action is not defined. Value not updated for "+jsonBody,row.get(1));
            }
        }
        return jsonBody;
    }

    public static String readRequestJson(String jsonFileName) {
        String filepath = System.getProperty("user.dir") + "/src/test/resources/configFiles/jsonFiles/requestJson/" + jsonFileName + ".json";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(objectMapper.readTree(new File(filepath)));
            return jsonRequest;
        } catch (JsonProcessingException e) {
            logger.info("Error in Processing json file " + jsonFileName);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String updateKeyValueInJson(String json,String jsonPath,String valueToBeUpdated){
        String updatedJson = null;
        try{

            Object oldValue = JsonPath.parse(json).read(jsonPath);
            Object newValue = checkIfGivenStringIsSomeKeyword(valueToBeUpdated);
            Object addObject = Configuration.defaultConfiguration().jsonProvider().parse(newValue.toString());
            if (oldValue.toString() != null && newValue.getClass() == oldValue.getClass()) {
                updatedJson = JsonPath.parse(json).set(jsonPath,addObject).jsonString();
            }else{
                Assert.fail("DataType of valueToBeUpdated is not as expected\n"+
                                "Failed at: <"+Thread.currentThread().getStackTrace()[1].getMethodName()+"> for json path: <"+jsonPath+">");
            }
        }catch(PathNotFoundException e){
            Assert.fail("Json path does not exists\n"+
                    "Failed at: <"+Thread.currentThread().getStackTrace()[1].getMethodName()+"> for json path: <"+jsonPath+">");
        }
        return updatedJson;
    }

    public static String removeKeyFromJson(String json,String jsonPath){
        String updatedJson = null;
        try {
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
            updatedJson = JsonPath.parse(json).delete(jsonPath).jsonString();
            //skipped getProperty(jsonPath) to get json path defined in static.properties in above method
        }catch (Exception e){
            Assert.assertTrue(false,e.getMessage());
        }
        return updatedJson;
    }

    public static String addKeyInJson(String json,String jsonPath,String keyValue){
        String updatedJson = null;
        try {
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
            Object addObject = Configuration.defaultConfiguration().jsonProvider().parse(keyValue);

            updatedJson = JsonPath.parse(json).put("$",jsonPath,addObject).jsonString();
            //skipped getProperty(jsonPath) to get json path defined in static.properties in above method
        }catch (Exception e){
            logger.info("Exception details:"+e.getMessage());
        }
        return updatedJson;
    }

    public static Object checkIfGivenStringIsSomeKeyword(String keyword){
        //we can also create a method calls getKeywordValue eg ValidId ,InvalidId so as to save for future reference

        if (keyword.contains("RandomInt")){
            int length = Integer.parseInt(keyword.split("-")[1]);
            return getRandomIntegerOfLength(length);
        }
        else if (keyword.contains("Integer")){
            int intValue = Integer.parseInt(keyword.split("-")[1]);
            return intValue;
        }
        else if (keyword.contains("RandomString")){
            int length = Integer.parseInt(keyword.split("-")[1]);
            return getRandomStringOfLength(length);
        }
        else if (keyword.contains("Boolean")){
            Boolean BoolValue = Boolean.valueOf(keyword.split("-")[1]);
            return BoolValue;
        }
        else{
            return keyword;
        }
    }

    public static int getRandomIntegerOfLength(int length) {
        if (length<=0){
            logger.error("Length of String needs to be greater than 0. Please pass value in format as RandomInt-4");
            return 0;
        }
        else{
            Random random = new Random();
            int min = (int) Math.pow(10,length-1);
            int max = (int) Math.pow(10,length)-1;
            return random.nextInt(max-min+1)+min;
        }
    }

    public static String getRandomStringOfLength(int length) {
        String value="";
        if (length<=0){
            logger.error("Length of String needs to be greater than 0. Please pass value in format as RandomInt-4");
        }
        else{
            String alpha = "abcdefghijklmnopqrstuvwxyz";
            StringBuilder sb = new StringBuilder();
            Random rand = new Random();
            for (int i=0;i<length;i++){
                int randomIndex = rand.nextInt(alpha.length());
                char randomChar = alpha.charAt(randomIndex);
                sb.append(randomChar);
            }
            value= sb.toString();
        }
        return value;
    }

    public static Map<String,String> getHeaderMapFromDataTable(DataTable dataTable){
        Map<String,String> mapOfKey = new HashMap<>();
        for (int i=1;i<dataTable.asLists().size();i++){
            if (dataTable.asLists().get(i).get(0).equalsIgnoreCase("header")){
                //here we can also pass static value as key value in datatable
                //and use PropertyHolder.getPropertymethod to get actual values od static constant
                //currently setting as it is values from featture file
                if(dataTable.asLists().get(i).get(2).contains("AUTH")){
                    mapOfKey.put(dataTable.asLists().get(i).get(1),dataTable.asLists().get(i).get(2).replace("AUTH",DataHolder.getProperty("AUTH")));
                }
                else {
                    mapOfKey.put(dataTable.asLists().get(i).get(1), dataTable.asLists().get(i).get(2));
                }
            }
        }
        return mapOfKey;
    }

    public static Map<String,String> getMapFromDataTableUsingKey(DataTable dataTable,String key){
        Map<String,String> mapOfKey = new HashMap<>();
        for (int i=0;i<dataTable.asLists().size();i++){
            if (dataTable.asLists().get(i).get(0).equalsIgnoreCase(key)){
                //here we can also pass static value as key value in datatable
                //and use PropertyHolder.getPropertymethod to get actual values od static constant
                //currently setting as it is values from featture file
                if(dataTable.asLists().get(i).get(2).contains("PROP_")){
                    String propValue = DataHolder.getProperty(dataTable.asLists().get(i).get(2).replace("PROP_",""));
                    mapOfKey.put(dataTable.asLists().get(i).get(1),propValue);
                }
                else {
                    mapOfKey.put(dataTable.asLists().get(i).get(1),dataTable.asLists().get(i).get(2));
                }
            }
        }
        return mapOfKey;
    }

    public static Response buildRequest(DataTable dataTable, String methodType, String endpoint, String requestBody) {
        String url = endpoint;
        logger.info("Path parameters are: "+getMapFromDataTableUsingKey(dataTable,"pathParam"));
        Map<String, String> headerMap = getHeaderMapFromDataTable(dataTable);
        logger.info(headerMap);
        logger.debug("UpdatedURL is :"+url);
        String contentType = getContentTypeFromDatatable(dataTable) == null ? "application/json" :getContentTypeFromDatatable(dataTable);
            RequestSpecification req = new RequestSpecBuilder()
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                    .setContentType(contentType)
                    .addPathParams(getMapFromDataTableUsingKey(dataTable,"pathParam"))
                    .addHeaders(headerMap)
                    .addQueryParams(getMapFromDataTableUsingKey(dataTable,"queryParam"))
                    .build();

            return doRequest(req,methodType,url,requestBody);
    }

    public static Response doRequest(RequestSpecification requestSpec, String methodType, String url, String requestBody){
        switch(methodType.toLowerCase()){
            case "get":
                return given().log().all().config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                        .spec(requestSpec).relaxedHTTPSValidation().get(url);
            case "post":
                return given()
                        .config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("json", ContentType.JSON)))
                        .spec(requestSpec).log().ifValidationFails().relaxedHTTPSValidation().body(requestBody).post(url);
            case "put":
                return given().spec(requestSpec).log().ifValidationFails().relaxedHTTPSValidation().body(requestBody).put(url);
            default:
                Assert.assertTrue(false,"Invalid method type passed: "+methodType);
                return null;
        }
    }

    public static String getContentTypeFromDatatable(DataTable dataTable){
        String contentType = getMapFromDataTableUsingKey(dataTable,"contentType").get("contentType");
        logger.info("Contentype is "+contentType);
        return contentType;
    }

    public static String readResponseJsonSchema(String schema){
        String jsonString = null;
        try{
            URL file = Resources.getResource("./configFiles/pathOfResponseJsonSchema");
            jsonString =Resources.toString(file, Charsets.UTF_8);
        } catch (Exception e) {
            Assert.assertTrue(false,e.getMessage());
        }
        return jsonString;
    }


}
