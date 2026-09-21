@Client
Feature: Client API Tests

  Background:
    Given I have the API host

  Scenario: Store and retrieve a value from Redis
    Given I have /test/redis API
    And I have following query parameters
      | key   | testKey   |
      | value | testValue |
    When Execute GET request using REST
    Then Validate status code is: 200

