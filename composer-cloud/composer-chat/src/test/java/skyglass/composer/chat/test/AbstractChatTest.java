package skyglass.composer.chat.test;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

// @ContextConfiguration(classes = { CommonChatConfig.class, H2JpaConfig.class, PsqlJpaConfig.class, MockBeanConfig.class })
public abstract class AbstractChatTest {

	//	@ClassRule
	//	public static DockerComposeContainer compose = new DockerComposeContainer(
	//			new File("src/test/resources/compose-test.yml"))
	//					.withExposedService("cassandra_1", 9042);

	@ClassRule
	public static DockerComposeContainer cassandra = new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))

			.withExposedService("cassandra_1", 9042)
			.waitingFor("cassandra_1", Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
			.withLocalCompose(true)

			.withExposedService("mysql_1", 3306)
			.withEnv("MYSQL_DATABASE", "ebook_chat")
			.withEnv("MYSQL_ROOT_PASSWORD", "root")
			.waitingFor("mysql_1", Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
			.withLocalCompose(true)

			.withExposedService("redis_1", 6379)
			.waitingFor("redis_1", Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
			.withLocalCompose(true)

			.withExposedService("rabbitmq-stomp_1", 61613)
			.waitingFor("rabbitmq-stomp_1", Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
			.withLocalCompose(true);
	//
	//	@ClassRule
	//	public static final GenericContainer redis = new FixedHostPortGenericContainer("redis:3.0.6")
	//			.withFixedExposedPort(6379, 6379);

	//@ClassRule
	//public static final GenericContainer cassandra = new FixedHostPortGenericContainer("cassandra:3.0")
	//.withFixedExposedPort(9042, 9042);

	//@ClassRule
	//public static DockerComposeContainer mysql = new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
	//.withExposedService("mysql_1", 3306, Wait.forListeningPort())
	//.withEnv("MYSQL_DATABASE", "ebook_chat")
	//.withEnv("MYSQL_ROOT_PASSWORD", "root")
	//.waitingFor("db_1", Wait.forLogMessage("started", 1))
	//.withLocalCompose(true);

	//@ClassRule
	//public static final GenericContainer mysql = new FixedHostPortGenericContainer("mysql:5.7")
	//.withFixedExposedPort(3306, 3306)
	//.withEnv("MYSQL_DATABASE", "ebook_chat")
	//.withEnv("MYSQL_ROOT_PASSWORD", "root");

	//@ClassRule
	//public static DockerComposeContainer redis = new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
	//.withExposedService("redis_1", 6379, Wait.forListeningPort())
	//.waitingFor("db_1", Wait.forLogMessage("started", 1))
	//.withLocalCompose(true);

	//@ClassRule
	//public static final GenericContainer redis = new FixedHostPortGenericContainer("redis:3.0.6")
	//.withFixedExposedPort(6379, 6379);

	//@ClassRule
	//public static DockerComposeContainer rabbitmq = new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
	//.withExposedService("rabbitmq_1", 61613, Wait.forListeningPort())
	//.waitingFor("db_1", Wait.forLogMessage("started", 1))
	//.withLocalCompose(true);

	//@ClassRule
	//public static final GenericContainer rabbitmq = new FixedHostPortGenericContainer("jorgeacetozi/rabbitmq-stomp:3.6")
	//.withFixedExposedPort(61613, 61613)
	//.withExposedPorts(61613);

	@BeforeClass
	public static void waitForMysqlContainerStartup() throws InterruptedException, TimeoutException {
		//WaitingConsumer consumer = new WaitingConsumer();
		//cassandra.followOutput(consumer);
		//consumer.waitUntil(frame -> frame.getUtf8String().contains("mysqld: ready for connections."), 90, TimeUnit.SECONDS);
	}

}
