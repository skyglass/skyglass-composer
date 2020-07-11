package skyglass.composer.local.test;

import org.springframework.test.context.ContextConfiguration;

import skyglass.composer.local.config.CommonJpaConfig;
import skyglass.composer.local.config.H2JpaConfig;
import skyglass.composer.local.config.MockBeanConfig;
import skyglass.composer.local.config.PsqlJpaConfig;

@ContextConfiguration(classes = { CommonJpaConfig.class, H2JpaConfig.class, PsqlJpaConfig.class, MockBeanConfig.class })
public abstract class AbstractBaseTest extends AbstractSuperBaseTest {

}
