package skyglass.composer.chat.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import skyglass.composer.chat.authentication.api.AuthenticationControllerTest;
import skyglass.composer.chat.authentication.service.DefaultUserServiceTest;
import skyglass.composer.chat.chatroom.api.ChatRoomControllerTest;
import skyglass.composer.chat.chatroom.service.CassandraInstantMessageServiceTest;
import skyglass.composer.chat.chatroom.service.RedisChatRoomServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		CassandraInstantMessageServiceTest.class,
		RedisChatRoomServiceTest.class,
		DefaultUserServiceTest.class,
		AuthenticationControllerTest.class,
		ChatRoomControllerTest.class
})
public class IntegrationTestsSuite extends AbstractChatTest {

}
