package utility;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

public class RequestMethods {

    //GET method with Path param and Query Param
    public static Response getCallWithQueryParamPathParamAndHeaders(String endpoint, RequestSpecification requestSpec){
        return given().log().all().config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .spec(requestSpec).relaxedHTTPSValidation().when().get(endpoint).then().log().all().extract().response();

    }

    //post method with headers and request body
    public static Response postCallWithHeadersAndReqBody(String tokenUrl,String jsonBody) {
        return given().log().all().config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .contentType(ContentType.JSON).body(jsonBody).when().post(tokenUrl).then().log().all().extract().response();

    }
}
