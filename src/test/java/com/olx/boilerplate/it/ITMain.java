package com.olx.boilerplate.it;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features_and_scenarios"},
        glue = {"com.olx.boilerplate.it.config", "com.olx.boilerplate.it.stepdefinition"})
public class ITMain {
}
