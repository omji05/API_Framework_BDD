package utility;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.Assert;
import steps.APICommonSteps;

public class ResponseUtils extends APICommonSteps {

    public static String getStringValueFromResponseUsingJsonPath(String jsonPath){
        return response.then().extract().jsonPath().getString(jsonPath);
    }

    //get integer value from the response using json path
    public static int getIntValueFromResponseUsingJsonPath(String jsonPath){
        return response.then().extract().jsonPath().getInt(jsonPath);
    }

    //to verify status code
    public static void assertResponseStatus(int expectedStatusCode){
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode,expectedStatusCode,
                "Expected status was: "+expectedStatusCode+" but actual status received in response is :"+actualStatusCode);
        logger.info("Response status code is "+actualStatusCode);
    }

    //to validate schema
    public static void validateJsonSchema(String schema){
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(Utility.readResponseJsonSchema(schema)));
    }

}
