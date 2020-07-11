package skyglass.composer.local.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import skyglass.composer.dto.i18n.TranslationRegistry;
import skyglass.composer.exceptions.ClientException;
import skyglass.composer.local.bean.LocalDatabaseResetBean;
import skyglass.composer.local.bean.MockHelper;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.dto.SecurityCacheRegistry;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AbstractBaseTest.PROFILE_H2 })
public abstract class AbstractSuperBaseTest {
	private static final Logger log = LoggerFactory.getLogger(AbstractSuperBaseTest.class);

	public static final String PROFILE_H2 = "h2-liquibase";

	public static final String PROFILE_PSQL = "psql-liquibase";

	@Autowired
	private LocalDatabaseResetBean localDatabaseResetBean;

	@Autowired
	private MockHelper mockHelper;

	@Before
	public void setupDefault() {
		clearRegistries();

		if (resetDatabase()) {
			log.info("Database reset");
		}
		mockHelper.setupDefault();
	}

	protected boolean resetDatabase() {
		return localDatabaseResetBean.resetDatabase();
	}

	@Rule
	public TestWatcher watchman = new TestWatcher() {
		@Override
		protected void failed(Throwable e, Description description) {
			if (e instanceof ClientException) {
				ClientException ex = (ClientException) e;
				String username = "N/A";
				User user = MockHelper.getCurrentUser();
				if (user != null) {
					username = user.getName();
				}

				log.error("Test failed (Current user: " + username + "): HTTP " + ex.getRawStatusCode() + " Response: " + ex.getResponseBody(), ex);
			} else {
				log.error("Test failed: " + description.toString(), e);
			}
		}

		@Override
		protected void succeeded(Description description) {
			log.debug("Test succeeded: " + description.toString());
		}
	};

	private void clearRegistries() {
		SecurityCacheRegistry.close();
		try {
			TranslationRegistry.get().close();
		} catch (Exception e) {

		}
	}
}
