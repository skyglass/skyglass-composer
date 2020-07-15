package com.allaroundjava.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class ServerEnvironment {
	private static List<SpringApplicationWrapper> instances = new ArrayList<>();

	static {
		List<String> defaultArgs = Lists
				.newArrayList(
						"--management.endpoint.shutdown.sensitive=false",
						"--management.endpoint.shutdown.enabled=true",
						"--management.context-path=/actuator",
						"--management.endpoint.metrics.enabled=true",
						"--management.endpoints.web.exposure.include=shutdown,mappings",
						"--management.endpoint.health.show-details=always",
						"--management.endpoint.beans.enabled=true",
						"--logging.level.org.springframework=WARN");
		instances.add(createDoctorBookingApplicationInstance(Lists.newArrayList(defaultArgs)));
		instances.add(createDoctorApplicationInstance(Lists.newArrayList(defaultArgs)));

	}

	public static void start() {
		instances.forEach(instance -> instance.start());
	}

	public static void shutdown() {
		instances.forEach(instance -> instance.shutdown());
	}

	private static SpringApplicationWrapper createDoctorBookingApplicationInstance(List<String> defaultArgs) {
		String jarFilePath = PathUtil
				.getJarfile(PathUtil.getProjectRoot() + "/doctor-booking/doctor-booking-application/target");
		defaultArgs.add("--server.port=8090");
		return new SpringApplicationWrapper("http://localhost:8090/actuator", jarFilePath, defaultArgs);
	}

	private static SpringApplicationWrapper createDoctorApplicationInstance(List<String> defaultArgs) {
		String jarFilePath = PathUtil
				.getJarfile(PathUtil.getProjectRoot() + "/doctor-booking/doctor-application/target");
		defaultArgs.add("--server.port=8091");
		return new SpringApplicationWrapper("http://localhost:8091/actuator", jarFilePath, defaultArgs);
	}

}
