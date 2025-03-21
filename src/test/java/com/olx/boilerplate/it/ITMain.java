package com.olx.boilerplate.it;

import com.olx.assertx.ITRunner;
import io.cucumber.junit.CucumberOptions;

@CucumberOptions(
    features = {"src/test/resources/features_and_scenarios"},
    glue = {"com.olx.boilerplate.it.stepdefinition"})
public class ITMain extends ITRunner {
}

