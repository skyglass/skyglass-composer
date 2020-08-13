package microservices.book;

import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.palantir.docker.compose.DockerComposeExtension;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import microservices.book.testutils.http.ServerEnvironment;

/**
 * @author moises.macero
 */
@Category(Cucumber.class)
@CucumberOptions(plugin = { "pretty" }, features = "src/test/resources/multiplication.feature")
public class MultiplicationFeatureTest extends AbstractCucumberTest {

	private static final String RABBITMQ = "rabbitmq";

	@RegisterExtension
	public static DockerComposeExtension docker = DockerComposeExtension.builder()
			.file("src/test/resources/docker-compose-rabbitmq.yml")
			.projectName(ProjectName.random())
			.waitingForService(RABBITMQ, HealthChecks.toHaveAllPortsOpen())
			.build();

	@BeforeAll
	public static void setup() {
		ServerEnvironment.start();
	}

	@AfterAll
	public static void tearDown() {
		ServerEnvironment.shutdown();
	}

}
