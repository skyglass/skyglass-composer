package skyglass.composer.chat.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import skyglass.composer.chat.chatroom.domain.InstantMessageBuilderTest;
import skyglass.composer.chat.chatroom.service.RedisChatRoomServiceUnitTest;
import skyglass.composer.chat.test.utils.DestinationsTest;
import skyglass.composer.chat.test.utils.SystemMessagesTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		InstantMessageBuilderTest.class,
		DestinationsTest.class,
		SystemMessagesTest.class,
		RedisChatRoomServiceUnitTest.class
})
public class UnitTestsSuite {

}
