package microservices.book;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import microservices.book.testutils.http.ServerEnvironment;

public class AbstractCucumberTest {

	@BeforeClass
	public static void setup() {
		ServerEnvironment.start();
	}

	@AfterClass
	public static void tearDown() {
		ServerEnvironment.shutdown();
	}

}
