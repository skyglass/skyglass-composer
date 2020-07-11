package skyglass.composer.local.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.test.context.ContextConfiguration;

import skyglass.composer.local.config.AsyncMockBeanConfig;
import skyglass.composer.local.config.CommonJpaConfig;
import skyglass.composer.local.config.H2JpaConfig;
import skyglass.composer.local.config.PsqlJpaConfig;

@ContextConfiguration(classes = { CommonJpaConfig.class, H2JpaConfig.class, PsqlJpaConfig.class, AsyncMockBeanConfig.class })
public abstract class AbstractAsyncBaseTest extends AbstractSuperBaseTest {

	protected ExecutorService EXEC = Executors.newFixedThreadPool(10);

	protected List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();

	protected void invokeAll() throws InterruptedException, ExecutionException {
		List<Future<Void>> futures = EXEC.invokeAll(tasks);
		for (Future<Void> future : futures) {
			future.get();
		}
		awaitTerminationAfterShutdown(EXEC);
	}

	private void awaitTerminationAfterShutdown(ExecutorService threadPool) {
		threadPool.shutdown();
		try {
			if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
			}
		} catch (InterruptedException ex) {
			threadPool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
