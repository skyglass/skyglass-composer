package microservices.book.testutils.http;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class ServerEnvironment {
	private static List<SpringApplicationWrapper> instances = new ArrayList<>();

	public static final int MULTIPLICATION_PORT = 8080;

	public static final int GAMIFICATION_PORT = 8081;

	public static final int SERVICE_REGISTRY_PORT = 8761;

	public static final int GATEWAY_PORT = 8000;

	static {
		List<String> defaultArgs = Lists
				.newArrayList(
						"--management.endpoint.shutdown.sensitive=true",
						"--management.endpoint.shutdown.enabled=true",
						"--management.context-path=/actuator",
						"--management.endpoint.metrics.enabled=true",
						"--management.endpoints.web.exposure.include=shutdown,mappings",
						"--management.endpoint.health.show-details=always",
						"--management.endpoint.beans.enabled=true",
						"--logging.level.org.springframework=WARN");
		instances.add(createServiceRegistryInstance(Lists.newArrayList(defaultArgs), SERVICE_REGISTRY_PORT));
		instances.add(createGatewayInstance(Lists.newArrayList(defaultArgs), GATEWAY_PORT));
		instances.add(createSocialMultiplicationInstance(Lists.newArrayList(defaultArgs), MULTIPLICATION_PORT));
		instances.add(createSocialGamificationInstance(Lists.newArrayList(defaultArgs), GAMIFICATION_PORT));
	}

	public static void start() {
		instances.forEach(instance -> instance.start());
	}

	public static void shutdown() {
		instances.forEach(instance -> instance.shutdown());
	}

	private static SpringApplicationWrapper createServiceRegistryInstance(List<String> defaultArgs, int port) {
		String jarFilePath = PathUtil
				.getJarfile(PathUtil.getProjectRoot() + "/social-cloud/social-service-registry/target");
		String managementUrl = String.format("http://localhost:%d/actuator", port);
		defaultArgs.add("--server.port=" + port);
		return new SpringApplicationWrapper(managementUrl, jarFilePath, defaultArgs);
	}

	private static SpringApplicationWrapper createGatewayInstance(List<String> defaultArgs, int port) {
		String jarFilePath = PathUtil
				.getJarfile(PathUtil.getProjectRoot() + "/social-cloud/social-gateway/target");
		String managementUrl = String.format("http://localhost:%d/actuator", port);
		defaultArgs.add("--server.port=" + port);
		return new SpringApplicationWrapper(managementUrl, jarFilePath, defaultArgs);
	}

	private static SpringApplicationWrapper createSocialGamificationInstance(List<String> defaultArgs, int port) {
		String jarFilePath = PathUtil
				.getJarfile(PathUtil.getProjectRoot() + "/social-cloud/social-gamification/target");
		String managementUrl = String.format("http://localhost:%d/actuator", port);
		defaultArgs.add("--server.port=" + port);
		defaultArgs.add("--run.profiles=test");
		return new SpringApplicationWrapper(managementUrl, jarFilePath, defaultArgs);
	}

	private static SpringApplicationWrapper createSocialMultiplicationInstance(List<String> defaultArgs, int port) {
		String jarFilePath = PathUtil
				.getJarfile(PathUtil.getProjectRoot() + "/social-cloud/social-multiplication/target");
		String managementUrl = String.format("http://localhost:%d/actuator", port);
		defaultArgs.add("--server.port=" + port);
		defaultArgs.add("--run.profiles=test");
		return new SpringApplicationWrapper(managementUrl, jarFilePath, defaultArgs);
	}

}
