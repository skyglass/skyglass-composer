package skyglass.composer.local.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@Profile("psql-liquibase")
@PropertySource("classpath:persistence-psql.properties")
public class PsqlJpaConfig {
	@Bean
	public SpringLiquibase liquibase(DataSource dataSource) throws SQLException {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setChangeLog("classpath:/liquibase/skyglass.changelog-test.xml");
		liquibase.setDataSource(dataSource);

		return liquibase;
	}
}
