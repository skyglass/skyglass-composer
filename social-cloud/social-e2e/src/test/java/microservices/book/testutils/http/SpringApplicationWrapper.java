package microservices.book.testutils.http;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.assertj.core.util.Lists;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SpringApplicationWrapper {

	private Logger log = LoggerFactory.getLogger(SpringApplicationWrapper.class);

	private RestTemplate template = new RestTemplate();

	enum Status {
		IDLE, STARTING, STARTED, STOPPING, STOPPED;
	}

	public SpringApplicationWrapper(String managementUrl, String executable, List<String> arguments) {
		this.managementUrl = managementUrl;
		this.executable = executable;
		this.arguments = arguments;
	}

	private String managementUrl;

	private String executable;

	private List<String> arguments;

	private Status status = Status.IDLE;

	private long waitForStartup = 15L;

	private long waitForShutdown = 5L;

	private StartUpMonitor startUpMonitor = new StartUpMonitor();

	private ShutdownMonitor shutdownMonitor = new ShutdownMonitor();

	public Status getStatus() {
		return status;
	}

	public void start() {
		if (status != Status.IDLE) {
			log.info("Cannot start service. Status is {}", status.name());
			return;
		}
		status = Status.STARTING;
		startServer();
		try {
			waitUntil(waitForStartup, startUpMonitor);
			if (status == Status.STARTING) {
				throw new IllegalStateException("Could not start service within reasonable time");
			}
		} catch (Exception e) {
			throw new IllegalStateException("Failed to start server");
		}
		log.info("Server is up");
	}

	public void shutdown() {
		if (status != Status.STARTED) {
			log.error("Can only shut down running server, but status is {}", status);
		}
		status = Status.STOPPING;
		try {
			waitUntil(waitForShutdown, shutdownMonitor);
			if (status == Status.STOPPING) {
				throw new IllegalStateException("Could not stop service within reasonable time");
			}
		} catch (Exception e) {
			throw new IllegalStateException("Failed to stop server");
		}
		log.info("Server is down");
	}

	private void waitUntil(long atMost, Callable<Boolean> condition) throws Exception {
		Thread.sleep(3000);
		int counter = 0;
		while (counter < atMost) {
			if (condition.call()) {
				break;
			}
			Thread.sleep(1000);
			counter++;
		}
	}

	private void startServer() {
		List<String> arguments = Lists.newArrayList("java", "-jar", executable);
		arguments.addAll(this.arguments);
		ProcessBuilder processBuilder = new ProcessBuilder(arguments);
		Process process = null;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			throw new IllegalStateException("Could not start process");
		}
		StreamMonitor.monitorStream(process, process.getInputStream(), false);
		StreamMonitor.monitorStream(process, process.getErrorStream(), true);
		if (!process.isAlive()) {
			throw new IllegalStateException("Not started");
		}
	}

	class StartUpMonitor implements Callable<Boolean> {
		@Override
		public Boolean call() throws Exception {
			try {
				ResponseEntity<JSONObject> response = template
						.getForEntity(managementUrl + "/mappings", JSONObject.class);
				boolean isStarted = response != null && response.getStatusCode().is2xxSuccessful();
				if (isStarted) {
					status = Status.STARTED;
				}
				return isStarted;
			} catch (Exception e) {
				return false;
			}
		}
	}

	class ShutdownMonitor implements Callable<Boolean> {
		@Override
		public Boolean call() throws Exception {
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> entity = new HttpEntity<String>(null, headers);
				ResponseEntity<String> response = template.postForEntity(managementUrl + "/shutdown", entity, String.class);
				boolean isStopped = response != null && response.getStatusCode().is2xxSuccessful();
				if (isStopped) {
					status = Status.STOPPED;
				}
				return isStopped;
			} catch (Exception e) {
				return false;
			}
		}
	}

}
