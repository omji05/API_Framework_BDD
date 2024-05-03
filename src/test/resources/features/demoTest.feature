@demoTestFeature
Feature: Demo test of API

  @create
  Scenario: Creating the booking
    Given user send the "post" request to "createBooking" using request body as "CreateBooking"
      | description | key         | value         |
      | update      | firstname   | John          |
      | update      | lastname    | Rammy         |
      | update      | totalprice  | Integer-34    |
      | update      | depositpaid | Boolean-false |
    And validate the status code as "200"
    And user should fetch and save following values
      | JsonPath  | Key       |
      | bookingid | BOOKING_ID |
    Then validate below response data
      | description | key                | value |
      | firstName   | booking.firstname  | John  |
      | amount      | booking.totalprice | 34    |

  @get
  Scenario: Get the booking details
    Given user send the get request to "getBooking" with below parameters
      | description | key | value           |
      | pathParam   | id  | PROP_BOOKING_ID |
    And validate the status code as "200"

  @update
  Scenario: Update the booking
    Given user set the authorization token
    And user send the "put" request to "updateBooking" using request body as "CreateBooking"
      | description | key         | value           |
      | pathParam   | id          | PROP_BOOKING_ID |
      | header      | Cookie      | token=AUTH      |
      | update      | firstname   | Billy           |
      | update      | lastname    | Carter          |
      | update      | totalprice  | Integer-90      |
      | update      | depositpaid | Boolean-true    |
    And validate the status code as "200"
    Then validate below response data
      | description | key        | value |
      | firstName   | firstname  | Billy |
      | amount      | totalprice | 90    |