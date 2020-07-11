package skyglass.composer.local.config;

import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory;

import skyglass.composer.security.repository.PermissionBean;
import skyglass.composer.security.repository.UserRepository;

public class AbstractMockBeanConfig {

	@Bean
	@Primary
	public PermissionBean permissionBeanSpy(PermissionBean permissionBean) {
		return Mockito.spy(PermissionBean.class);
	}

	@Bean
	@Primary
	@DependsOn({ "permissionBean" })
	public UserRepository userRepositorySpy(UserRepository userRepository) {
		return Mockito.spy(UserRepository.class);
	}

	@Bean
	@Primary
	public ConnectionFactory connectionFactory() {
		return new CachingConnectionFactory(MockConnectionFactoryFactory.build());
	}
}
