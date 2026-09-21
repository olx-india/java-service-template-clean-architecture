@Order
Feature: Order API Integration Tests

  Background:
    Given I have the API host

  Scenario: Create a new order successfully
    Given I have /api/v1/orders API
    And I generate a CreateOrderRequest
    And I have following headers
      | Content-Type |
      | application/json |
    And I have a request body in CreateOrderRequest
    When Execute POST request using REST
    Then Validate status code is: 201

  Scenario: Retrieve an existing order
    Given I have /api/v1/orders/998 API
    When Execute GET request using REST
    Then Validate status code is: 200
    And Validate order response
    And Validate order response with product Test Order 1

  Scenario: Delete an order successfully
    Given I have /api/v1/orders/999 API
    When Execute DELETE request using REST
    Then Validate status code is: 204

