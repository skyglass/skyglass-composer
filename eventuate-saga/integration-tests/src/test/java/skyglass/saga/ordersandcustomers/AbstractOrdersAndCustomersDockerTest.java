package skyglass.saga.ordersandcustomers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.LazyFuture;

public abstract class AbstractOrdersAndCustomersDockerTest extends AbstractOrdersAndCustomersIntegrationTest {

	//	@ClassRule
	//	public static final DockerComposeContainer container = new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
	//
	//			.withExposedService("zookeeper_1", 2181)
	//			.waitingFor("zookeeper_1", Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
	//
	//			.withExposedService("kafka_1", 9092)
	//			.withEnv("ADVERTISED_HOST_NAME", "192.168.0.59")
	//			.withEnv("KAFKA_HEAP_OPTS", "-Xmx320m -Xms320m")
	//			.withEnv("ZOOKEEPER_SERVERS", "zookeeper:2181")
	//			.waitingFor("kafka_1", Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
	//			.withExposedService("mysql_1", 3306)
	//			.withEnv("MYSQL_ROOT_PASSWORD", "rootpassword")
	//			.withEnv("MYSQL_USER", "mysqluser")
	//			.withEnv("MYSQL_PASSWORD", "mysqlpw")
	//			.waitingFor("mysql_1", Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
	//
	//			.withExposedService("cdcservice_1", 8080)
	//			.withEnv("SPRING_DATASOURCE_URL", "jdbc:mysql://localhost:3306/eventuate")
	//			.withEnv("SPRING_DATASOURCE_USERNAME", "mysqluser")
	//			.withEnv("SPRING_DATASOURCE_PASSWORD", "mysqlpw")
	//			.withEnv("SPRING_DATASOURCE_DRIVER_CLASS_NAME", "com.mysql.jdbc.Driver")
	//			.withEnv("EVENTUATELOCAL_KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
	//			.withEnv("EVENTUATELOCAL_ZOOKEEPER_CONNECTION_STRING", "localhost:2181")
	//			.withEnv("EVENTUATELOCAL_CDC_DB_USER_NAME", "root")
	//			.withEnv("EVENTUATELOCAL_CDC_DB_PASSWORD", "rootpassword")
	//			.withEnv("EVENTUATELOCAL_CDC_READER_NAME", "MySqlReader")
	//			.withEnv("EVENTUATELOCAL_CDC_OFFSET_STORE_KEY", "MySqlBinlog")
	//			.withEnv("EVENTUATELOCAL_CDC_MYSQL_BINLOG_CLIENT_UNIQUE_ID", "1234567890")
	//			.withEnv("EVENTUATELOCAL_CDC_READ_OLD_DEBEZIUM_DB_OFFSET_STORAGE_TOPIC", "false")
	//			.waitingFor("cdcservice_1", Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));

	//	@Container
	//	static final MySQLContainer<?> mysql = new MySQLContainer<>()
	//			.withNetwork(Network.SHARED)
	//			.withClasspathResourceMapping("./mysql", ".", BindMode.READ_ONLY)
	//			.withExposedPorts(3306)
	//			.withEnv("MYSQL_ROOT_PASSWORD", "rootpassword")
	//			.withEnv("MYSQL_USER", "mysqluser")
	//			.withEnv("MYSQL_PASSWORD", "mysqlpw")
	//			.withEnv("EVENTUATE_COMMON_VERSION", "0.9.0.RC4")
	//			.withNetworkAliases("mysql");

	@ClassRule
	public static GenericContainer<?> zookeeper = new GenericContainer<>("eventuateio/eventuate-zookeeper:0.9.0.RC4")
			.withExposedPorts(2181)
			.withNetwork(Network.SHARED)
			.withNetworkAliases("zookeeper")
			.withEnv("ZOOKEEPER_CLIENT_PORT", "2181");

	@ClassRule
	public static GenericContainer<?> kafka = new GenericContainer<>("eventuateio/eventuate-kafka:0.9.0.RC4")
			.withExposedPorts(9092)
			.withNetwork(Network.SHARED)
			.dependsOn(zookeeper)
			.withNetworkAliases("kafka")
			.withEnv("ADVERTISED_HOST_NAME", "192.168.0.59")
			.withEnv("KAFKA_HEAP_OPTS", "-Xmx320m -Xms320m")
			.withEnv("ZOOKEEPER_SERVERS", "zookeeper:2181");

	@ClassRule
	public static GenericContainer<?> mysql = new GenericContainer(
			new ImageFromDockerfile()
					.withDockerfile(Paths.get("src/test/resources/mysql/Dockerfile")))
							.withNetwork(Network.SHARED)
							.withExposedPorts(3306)
							.withEnv("MYSQL_ROOT_PASSWORD", "rootpassword")
							.withEnv("MYSQL_USER", "mysqluser")
							.withEnv("MYSQL_PASSWORD", "mysqlpw")
							.withEnv("EVENTUATE_COMMON_VERSION", "0.9.0.RC4")
							.withNetworkAliases("mysql");

	//	@ClassRule
	//	public static GenericContainer<?> cdcservice = new GenericContainer<>("eventuateio/eventuate-cdc-service:0.6.0.RC4")
	//			.withExposedPorts(8080)
	//			.withNetwork(Network.SHARED)
	//			.withNetworkAliases("cdcservice")
	//			.dependsOn(mysql)
	//			.dependsOn(kafka)
	//			.dependsOn(zookeeper)
	//			.withEnv("SPRING_DATASOURCE_URL", "jdbc:mysql://mysql/eventuate")
	//			.withEnv("SPRING_DATASOURCE_USERNAME", "mysqluser")
	//			.withEnv("SPRING_DATASOURCE_PASSWORD", "mysqlpw")
	//			.withEnv("SPRING_DATASOURCE_DRIVER_CLASS_NAME", "com.mysql.jdbc.Driver")
	//			.withEnv("EVENTUATELOCAL_KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
	//			.withEnv("EVENTUATELOCAL_ZOOKEEPER_CONNECTION_STRING", "zookeeper:2181")
	//			.withEnv("EVENTUATELOCAL_CDC_DB_USER_NAME", "root")
	//			.withEnv("EVENTUATELOCAL_CDC_DB_PASSWORD", "rootpassword")
	//			.withEnv("EVENTUATELOCAL_CDC_READER_NAME", "MySqlReader")
	//			.withEnv("EVENTUATELOCAL_CDC_OFFSET_STORE_KEY", "MySqlBinlog")
	//			.withEnv("EVENTUATELOCAL_CDC_MYSQL_BINLOG_CLIENT_UNIQUE_ID", "1234567890")
	//			.withEnv("EVENTUATELOCAL_CDC_READ_OLD_DEBEZIUM_DB_OFFSET_STORAGE_TOPIC", "false");

	//@ClassRule
	public static final GenericContainer<?> customerservice = new GenericContainer<>(getFuture("customer-service")).withNetwork(Network.SHARED).dependsOn(mysql).dependsOn(kafka).dependsOn(zookeeper)
			//.dependsOn(cdcservice)
			//.withExposedPorts(8082)
			//.withNetworkAliases("localhost")
			.withEnv("SPRING_DATASOURCE_URL", "jdbc:mysql://mysql/eventuate").withEnv("SPRING_DATASOURCE_USERNAME", "mysqluser").withEnv("SPRING_DATASOURCE_PASSWORD", "mysqlpw")
			.withEnv("SPRING_DATASOURCE_DRIVER_CLASS_NAME", "com.mysql.jdbc.Driver").withEnv("EVENTUATELOCAL_KAFKA_BOOTSTRAP_SERVERS", "192.168.0.59:9092")
			.withEnv("EVENTUATELOCAL_ZOOKEEPER_CONNECTION_STRING", "192.168.0.59:2181")
			// Forward logs
			.withLogConsumer(new ToStringConsumer() {
				@Override
				public void accept(OutputFrame outputFrame) {
					if (outputFrame.getBytes() != null) {
						try {
							System.out.write(outputFrame.getBytes());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
			});

	//@ClassRule
	public static final GenericContainer<?> orderservice = new GenericContainer<>(getFuture("order-service")).withNetwork(Network.SHARED).dependsOn(mysql).dependsOn(kafka).dependsOn(zookeeper)
			//.dependsOn(cdcservice)
			//.withExposedPorts(8081)
			//.withNetworkAliases("localhost")
			.withEnv("SPRING_DATASOURCE_URL", "jdbc:mysql://mysql/eventuate").withEnv("SPRING_DATASOURCE_USERNAME", "mysqluser").withEnv("SPRING_DATASOURCE_PASSWORD", "mysqlpw")
			.withEnv("SPRING_DATASOURCE_DRIVER_CLASS_NAME", "com.mysql.jdbc.Driver").withEnv("EVENTUATELOCAL_KAFKA_BOOTSTRAP_SERVERS", "192.168.0.59:9092")
			.withEnv("EVENTUATELOCAL_ZOOKEEPER_CONNECTION_STRING", "192.168.0.59:2181")
			// Forward logs
			.withLogConsumer(new ToStringConsumer() {
				@Override
				public void accept(OutputFrame outputFrame) {
					if (outputFrame.getBytes() != null) {
						try {
							System.out.write(outputFrame.getBytes());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
			});

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			startContainers();
			String test = mysql.getContainerIpAddress();

			TestPropertyValues.of(
					"spring.jpa.generate-ddl=false",
					"spring.datasource.url=jdbc:mysql://mysql/eventuate",
					"spring.datasource.username=mysqluser",
					"spring.datasource.password=mysqlpw",
					"spring.datasource.driver-class-name=com.mysql.jdbc.Driver",
					"spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect",
					"eventuatelocal.kafka.bootstrap.servers=kafka",
					"eventuatelocal.zookeeper.connection.string=kafka").applyTo(configurableApplicationContext.getEnvironment());

		}
	}

	private static void startContainers() {
		Startables.deepStart(Stream.of(zookeeper, kafka, mysql)).join();
	}

	@BeforeClass
	public static void waitForMysqlContainerStartup() throws InterruptedException, TimeoutException {
		//container.start();
		//zookeeper.start();
		//kafka.start();
		//mysql.start();
		//cdcservice.start();
		//customerservice.start();
		//orderservice.start();
	}

	private static Future<String> getFuture(String serviceName) {
		Future<String> result = new LazyFuture<String>() {

			@Override
			protected String resolve() {
				// Find project's root dir
				File cwd;
				for (cwd = new File(String.format("../%s", serviceName)); !new File(cwd, "pom.xml").isFile(); cwd = cwd.getParentFile()) {

				}

				Properties properties = new Properties();
				// Avoid recursion :D
				properties.put("skipTests", "true");

				InvocationRequest request = new DefaultInvocationRequest().setPomFile(new File(cwd, "pom.xml")).setGoals(Arrays.asList("spring-boot:build-image")).setProperties(properties);

				try {
					InvocationResult invocationResult = new DefaultInvoker().execute(request);
					if (invocationResult.getExitCode() != 0) {
						throw new RuntimeException(invocationResult.getExecutionException());
					}
				} catch (MavenInvocationException e) {
					throw new RuntimeException(e);
				}

				return String.format("docker.io/library/%s:0.0.1-SNAPSHOT", serviceName);
			}
		};

		return result;
	}

}
