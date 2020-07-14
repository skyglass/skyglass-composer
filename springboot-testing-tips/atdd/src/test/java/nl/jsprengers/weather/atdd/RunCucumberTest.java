package nl.jsprengers.weather.atdd;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;

@RunWith(Cucumber.class)
@CucumberContextConfiguration
@CucumberOptions(features = "classpath:features", plugin = { "pretty", "html:target/cucumber-html-report.html", "junit:target/junit-report.xml" })
public class RunCucumberTest {
	@BeforeClass
	public static void setup() {
		ServerEnvironment.start();
	}

	@AfterClass
	public static void tearDown() {
		ServerEnvironment.shutdown();
	}
}
