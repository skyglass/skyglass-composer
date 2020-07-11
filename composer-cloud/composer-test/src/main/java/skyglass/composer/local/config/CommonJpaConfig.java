package skyglass.composer.local.config;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import skyglass.composer.bean.ComposerBeanConfiguration;
import skyglass.composer.business.BusinessConfiguration;
import skyglass.composer.local.bean.ComposerLocalBeanConfiguration;
import skyglass.composer.security.SecurityConfiguration;

@Configuration
@EnableTransactionManagement
//@ComponentScan(basePackages = { "skyglass.composer.local.repository", "skyglass.composer.local.bean", "skyglass.composer.bean",
//		"skyglass.composer.security.service", "skyglass.composer.security.repository", "skyglass.composer.component" })
//@EnableJpaRepositories(basePackages = { "skyglass.composer.local.repository", "skyglass.composer.repository.jpa" })
@Import({ ComposerBeanConfiguration.class,
		ComposerLocalBeanConfiguration.class,
		SecurityConfiguration.class,
		BusinessConfiguration.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@EnableScheduling
public class CommonJpaConfig {

	@Autowired
	private Environment env;

	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setThreadNamePrefix("SpringAsyncThread-");
		executor.initialize();

		return executor;
	}

	@Bean
	public DataSource dataSource() throws SQLException {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(env.getProperty("jdbc.url"));
		dataSource.setUsername(env.getProperty("jdbc.user"));
		dataSource.setPassword(env.getProperty("jdbc.pass"));

		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws SQLException {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] {
				"skyglass.composer.local.domain",
				"skyglass.composer.security.domain",
				"skyglass.composer.business.domain" });
		em.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());
		em.setJpaProperties(additionalProperties());

		em.setPersistenceUnitName("platform");
		em.setPersistenceProviderClass(PersistenceProvider.class);
		em.setSharedCacheMode(SharedCacheMode.NONE);
		em.setJtaDataSource(dataSource());
		em.afterPropertiesSet();
		em.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());

		return em;
	}

	@Bean
	public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

	protected Properties additionalProperties() {
		final Properties eclipseLinkProperties = new Properties();
		eclipseLinkProperties.put(PersistenceUnitProperties.PERSISTENCE_CONTEXT_FLUSH_MODE, "commit");
		eclipseLinkProperties.put(PersistenceUnitProperties.DDL_GENERATION, "none");
		eclipseLinkProperties.put(PersistenceUnitProperties.LOGGING_LEVEL, "SEVERE");
		eclipseLinkProperties.put(PersistenceUnitProperties.WEAVING, "static");
		eclipseLinkProperties.put(PersistenceUnitProperties.QUERY_CACHE, "false");

		return eclipseLinkProperties;
	}
}
