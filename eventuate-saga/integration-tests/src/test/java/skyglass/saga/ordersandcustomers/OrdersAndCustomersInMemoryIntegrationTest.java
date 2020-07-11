package skyglass.saga.ordersandcustomers;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrdersAndCustomersInMemoryIntegrationTestConfiguration.class)
public class OrdersAndCustomersInMemoryIntegrationTest extends AbstractOrdersAndCustomersIntegrationTest {
}
