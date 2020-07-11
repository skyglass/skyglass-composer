package skyglass.composer.config;

import java.sql.SQLException;

import javax.persistence.SharedCacheMode;

import org.eclipse.persistence.jpa.PersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import skyglass.composer.bean.ComposerBeanConfiguration;
import skyglass.composer.customer.CustomerConfiguration;
import skyglass.composer.local.bean.ComposerLocalBeanConfiguration;
import skyglass.composer.local.config.CommonJpaConfig;
import skyglass.composer.order.OrderConfiguration;
import skyglass.composer.security.SecurityConfiguration;

@Configuration
@EnableTransactionManagement
//@ComponentScan(basePackages = { "skyglass.composer.local.repository", "skyglass.composer.local.bean", "skyglass.composer.bean",
//		"skyglass.composer.security.service", "skyglass.composer.security.repository", "skyglass.composer.component" })
//@EnableJpaRepositories(basePackages = { "skyglass.composer.local.repository", "skyglass.composer.repository.jpa" })
@Import({ ComposerBeanConfiguration.class,
		ComposerLocalBeanConfiguration.class,
		SecurityConfiguration.class,
		CustomerConfiguration.class,
		OrderConfiguration.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CommonDomainConfig extends CommonJpaConfig {

	@Bean
	@Override
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws SQLException {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] {
				"skyglass.composer.local.domain",
				"skyglass.composer.security.domain",
				"skyglass.composer.customer",
				"skyglass.composer.order" });
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

}
