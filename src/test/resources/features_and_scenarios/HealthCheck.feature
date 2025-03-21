@Test:Health
Feature: Check health of application
  Background:
    Given I have http://0.0.0.0:8081/ host

  Scenario: Validate Health API
    Given I have /health API
    When Execute GET request using REST
    Then Validate status code is: 200
