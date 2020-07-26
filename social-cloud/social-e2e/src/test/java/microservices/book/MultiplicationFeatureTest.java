package microservices.book;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;

/**
 * @author moises.macero
 */
@RunWith(Cucumber.class)
@CucumberContextConfiguration
@CucumberOptions(plugin = { "pretty", "html:target/cucumber", "junit:target/junit-report.xml" }, features = "src/test/resources/multiplication.feature")
public class MultiplicationFeatureTest extends AbstractCucumberTest {

}
