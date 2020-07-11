package skyglass.composer.test;

import org.springframework.test.context.ContextConfiguration;

import skyglass.composer.config.CommonDomainConfig;
import skyglass.composer.local.config.H2JpaConfig;
import skyglass.composer.local.config.MockBeanConfig;
import skyglass.composer.local.config.PsqlJpaConfig;
import skyglass.composer.local.test.AbstractSuperBaseTest;

@ContextConfiguration(classes = { CommonDomainConfig.class, H2JpaConfig.class, PsqlJpaConfig.class, MockBeanConfig.class })
public abstract class AbstractDomainTest extends AbstractSuperBaseTest {

}
