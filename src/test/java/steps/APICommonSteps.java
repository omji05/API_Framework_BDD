package steps;

import base.RestAssuredBase;
import io.cucumber.datatable.DataTable;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import utility.DataHolder;
import utility.RequestMethods;
import utility.ResponseUtils;
import utility.Utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APICommonSteps extends RestAssuredBase {

    public static String endpoint = null;
    public static String resourceUrl = null;
    protected static Response response = null;

    public static String payload = null;
    protected static HashMap<String, String> pathparam = new HashMap<String, String>();
    protected static HashMap<String, String> queryparam = new HashMap<String, String>();
    protected static HashMap<String, String> headers = new HashMap<String, String>();

    protected static String AuthorizationToken=null;

    public static int expectedStatusCode = 0;

    public static void setHeaders(DataTable table) {
        List<List<String>> List = table.asLists();
        for (int i = 1; i < List.size(); i++) {

            headers.put(List.get(i).get(0), List.get(i).get(1));
            logger.info(List.get(i).get(0) + " Header added successfully");
        }
    }

    public static void create_endpoint_with_methodtype_and_jsonbody(String methodType, String resourceEndpoint, String jsonFileName, DataTable dataTable) {
        resourceUrl = Utility.getProperty(resourceEndpoint);
        logger.info("Resource endpoint is:" + resourceUrl);

        endpoint = configurationPropertiesMap.get("DomainURL").concat(resourceUrl);
        logger.info("Resource endpoint with domain URL: " + endpoint);

        String requestBody = Utility.updateRequestBody(dataTable, Utility.readRequestJson(jsonFileName));

        response = Utility.buildRequest(dataTable, methodType, endpoint, requestBody);
    }


    public static void getStatusCode(String statusCode) {
        expectedStatusCode = Integer.parseInt(statusCode);
        ResponseUtils.assertResponseStatus(expectedStatusCode);

    }

    public static void responseDataValidator(DataTable dataTable) {
        List<List<String>> list = dataTable.asLists();
        for (int i = 1; i < list.size(); i++) {
            String jsonPath = list.get(i).get(1);
            String value = list.get(i).get(2);
            if (value.contains("Prop_")) {
                String actualResponse = ResponseUtils.getStringValueFromResponseUsingJsonPath(jsonPath);
                logger.info("Response data for: <" + jsonPath + "> is :" + actualResponse);
                Assert.assertEquals(actualResponse, DataHolder.getProperty(list.get(i).get(2).replace("Prop_", "")),
                        "Value does not match");
            } else {
                String actualResponse = ResponseUtils.getStringValueFromResponseUsingJsonPath(jsonPath);
                logger.info("Response data for: <" + jsonPath + "> is :" + actualResponse);
                Assert.assertEquals(actualResponse, value, "Value does not match");
            }
        }
    }

    public static void getCallWithPathParamAndQueryParamAndHeaders(String resourceEndpoint, DataTable dataTable) {
        resourceUrl = Utility.getProperty(resourceEndpoint);
        logger.info("Resource endpoint is:" + resourceUrl);

        endpoint = configurationPropertiesMap.get("DomainURL").concat(resourceUrl);
        logger.info("Resource endpoint with domain URL: " + endpoint);

        RequestSpecification reqSpec = getRequestSpec(dataTable);

        response = RequestMethods.getCallWithQueryParamPathParamAndHeaders(endpoint,reqSpec);
        logger.info("Get call with parameters executed");

    }

    public static RequestSpecification getRequestSpec(DataTable dataTable){
        Map<String,String> pathParam = Utility.getMapFromDataTableUsingKey(dataTable,"pathParam");
        Map<String,String> queryParam = Utility.getMapFromDataTableUsingKey(dataTable,"queryParam");
        Map<String,String> headers = Utility.getMapFromDataTableUsingKey(dataTable,"headers");
        String contentType = Utility.getContentTypeFromDatatable(dataTable)==null ? "application/json" : Utility.getContentTypeFromDatatable(dataTable);
        return new RequestSpecBuilder()
                .setContentType(contentType)
                .addHeaders(headers)
                .addPathParams(pathParam)
                .addQueryParams(queryParam)
                .build();
    }

    //can be overloaded if auth need using form params
    public static void readToken() {
        String tokenEndPoint = configurationPropertiesMap.get("TokenURL").concat(Utility.getProperty("AUTH_TOKEN_URL"));

        response = RequestMethods.postCallWithHeadersAndReqBody(tokenEndPoint,Utility.readRequestJson("Auth"));
        AuthorizationToken=ResponseUtils.getStringValueFromResponseUsingJsonPath("token");
        logger.info("Authorization token is  "+AuthorizationToken);
        DataHolder.setProperty("AUTH",AuthorizationToken);
    }

    public static void storeDataValues(DataTable dataTable) {
            for (int i = 1; i < dataTable.asLists().size(); i++) {
                if (ResponseUtils.getStringValueFromResponseUsingJsonPath(dataTable.asLists().get(i).get(0)) != null)
                {
                    DataHolder.setProperty(dataTable.asLists().get(i).get(1),
                            ResponseUtils.getStringValueFromResponseUsingJsonPath(dataTable.asLists().get(i).get(0)));
                    logger.info(dataTable.asLists().get(i).get(1)+" is stored in DataHolder with values as: "+DataHolder.getProperty(dataTable.asLists().get(i).get(1)));
                }


            }

    }
}
