@Test:User
Feature: User API Integration Tests

  Background:
    Given I have the API host

  Scenario: Create a new user successfully
    Given I have /api/v1/users API
    And I generate a CreateUserRequest
    And I have following headers
      | Content-Type |
      | application/json |
    And I have a request body in CreateUserRequest
    When Execute POST request using REST
    Then Validate status code is: 201
    And Validate user response

  Scenario: Fetch a user successfully
    Given I have /api/v1/users/998 API
    When Execute GET request using REST
    Then Validate status code is: 200
    And Validate user response with name Test User 1

  Scenario: Delete a user successfully
    Given I have /api/v1/users/998 API
    When Execute DELETE request using REST
    Then Validate status code is: 204
