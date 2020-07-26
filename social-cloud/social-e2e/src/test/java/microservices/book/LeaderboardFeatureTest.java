package microservices.book;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * @author moises.macero
 */
@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber", "junit:target/junit-report.xml" }, features = "src/test/resources/leaderboard.feature")
public class LeaderboardFeatureTest extends AbstractCucumberTest {

}
