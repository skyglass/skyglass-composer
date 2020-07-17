package skyglass.saga.ordersandcustomers;

import org.junit.BeforeClass;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.palantir.docker.compose.DockerComposeExtension;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

public abstract class AbstractOrdersAndCustomersDockerComposeTest extends AbstractOrdersAndCustomersIntegrationTest {

	private static final int DATABASE_PORT = 8000;

	private static final String DYNAMODB = "dynamodb";

	@RegisterExtension
	public static DockerComposeExtension docker = DockerComposeExtension.builder()
			.file("src/test/resources/compose-test.yml")
			.projectName(ProjectName.random())
			.waitingForService("zookeeper", HealthChecks.toHaveAllPortsOpen())
			.waitingForService("dynamodb", HealthChecks.toHaveAllPortsOpen())
			.build();

	@BeforeClass
	public static void initialize() {
		//		DockerPort mysql = docker.containers()
		//				.container("mysql")
		//				.port(3306);
		//		String mysqlEndpoint = String.format("http://%s:%s", mysql.getIp(), mysql.getExternalPort());
		DockerPort dynamodb = docker.containers()
				.container("")
				.port(8000);
		String dynamodbEndpoint = String.format("http://%s:%s", dynamodb.getIp(), dynamodb.getExternalPort());
		dynamodbEndpoint = String.format("http://%s:%s", dynamodb.getIp(), dynamodb.getExternalPort());

		DockerPort zookeeper = docker.containers()
				.container("")
				.port(2181);
		String zookeeperEndpoint = String.format("http://%s:%s", zookeeper.getIp(), zookeeper.getExternalPort());
		zookeeperEndpoint = String.format("http://%s:%s", zookeeper.getIp(), zookeeper.getExternalPort());
	}

}
