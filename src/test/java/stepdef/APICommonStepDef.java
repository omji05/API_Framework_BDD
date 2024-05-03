package stepdef;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import steps.APICommonSteps;

public class APICommonStepDef {


    @Given("^I set the below fields in request Header$")
    public void I_set_the_below_fields_in_request_Header(DataTable table) {
        APICommonSteps.setHeaders(table);
    }

    @Given("user send the {string} request to {string} using request body as {string}")
    public void user_send_the_request_to_using_request_body_as(String methodType, String resourceEndpoint, String jsonBody, DataTable dataTable) {
        APICommonSteps.create_endpoint_with_methodtype_and_jsonbody(methodType,resourceEndpoint,jsonBody,dataTable);
    }

    @Given("user send the get request to {string} with below parameters")
    public void userSendTheGetRequestToWithBelowParameters(String resourceEndpoint, DataTable dataTable) {
        APICommonSteps.getCallWithPathParamAndQueryParamAndHeaders(resourceEndpoint,dataTable);
    }

    @Given("validate the status code as {string}")
    public void validate_the_status_code_as(String status) {
        APICommonSteps.getStatusCode(status);
    }

    @Then("validate below response data")
    public void validate_below_response_data(DataTable dataTable) {
        APICommonSteps.responseDataValidator(dataTable);

    }

    @Given("user set the authorization token")
    public void userSetTheAuthorizationToken() {
        APICommonSteps.readToken();
    }

    @And("user should fetch and save following values")
    public void userShouldFetchAndSaveFollowingValues(DataTable dataTable) {
        APICommonSteps.storeDataValues(dataTable);
    }
}
