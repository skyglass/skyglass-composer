package skyglass.composer.chat.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;

@Profile({ "dev", "test" })
@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {

	@Override
	protected String getKeyspaceName() {
		return "ebook_chat";
	}

	@Bean
	@Override
	public CqlSessionFactoryBean cassandraSession() {
		CqlSessionFactoryBean cluster = new CqlSessionFactoryBean();
		//cluster.setJmxReportingEnabled(false);
		cluster.setContactPoints("localhost");
		cluster.setPort(9042);
		cluster.setKeyspaceCreations(
				Arrays.asList(
						CreateKeyspaceSpecification.createKeyspace("ebook_chat")
								.ifNotExists()
								.withSimpleReplication(1)));
		cluster.setStartupScripts(Arrays.asList(
				"USE ebook_chat",
				"CREATE TABLE IF NOT EXISTS messages (" +
						"username text," +
						"chatRoomId text," +
						"date timestamp," +
						"fromUser text," +
						"toUser text," +
						"text text," +
						"PRIMARY KEY ((username, chatRoomId), date)" +
						") WITH CLUSTERING ORDER BY (date ASC)"));
		return cluster;
	}

	@Bean
	@Override
	public CassandraMappingContext cassandraMapping()
			throws ClassNotFoundException {
		return new  CassandraMappingContext();
	}

	//@Override
	protected boolean getMetricsEnabled() {
		return false;
	}
}
