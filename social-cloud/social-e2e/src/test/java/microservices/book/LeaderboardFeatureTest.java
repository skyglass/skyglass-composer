package microservices.book;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

/**
 * @author moises.macero
 */
@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber", "junit:target/junit-report.xml" }, features = "src/test/resources/leaderboard.feature")
public class LeaderboardFeatureTest extends AbstractCucumberTest {

}
