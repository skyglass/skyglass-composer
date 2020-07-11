package skyglass.composer.chat.chatroom.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import skyglass.composer.chat.chatroom.domain.ChatRoom;
import skyglass.composer.chat.chatroom.domain.ChatRoomUser;
import skyglass.composer.chat.chatroom.repository.ChatRoomRepository;
import skyglass.composer.chat.chatroom.repository.InstantMessageRepository;
import skyglass.composer.chat.chatroom.service.ChatRoomService;
import skyglass.composer.chat.test.EbookChatTest;

@RunWith(SpringRunner.class)
@EbookChatTest
public class RedisChatRoomServiceTest {
	
	@Autowired
	private ChatRoomService chatRoomService;
	
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	
	@Autowired
	private InstantMessageRepository instantMessageRepository;
	
	private ChatRoom chatRoom;
	
	@Before
	public void setup() {
		chatRoom = new ChatRoom("123", "Dream Theater", "Discuss about best band ever!");
		chatRoomService.save(chatRoom);
	}
	
	@After
	public void destroy() {
		chatRoomRepository.delete(chatRoom);
		instantMessageRepository.deleteAll();
	}
	
	@Test
	public void shouldJoinUsersToChatRoom() {
		assertThat(chatRoom.getNumberOfConnectedUsers(), is(0));
		
		ChatRoomUser jorgeAcetozi = new ChatRoomUser("jorge_acetozi");
		ChatRoomUser johnPetrucci = new ChatRoomUser("john_petrucci");
		
		chatRoomService.join(jorgeAcetozi, chatRoom);
		assertThat(chatRoom.getNumberOfConnectedUsers(), is(1));
		
		chatRoomService.join(johnPetrucci, chatRoom);
		assertThat(chatRoom.getNumberOfConnectedUsers(), is(2));
		
		ChatRoom dreamTheaterChatRoom = chatRoomService.findById(chatRoom.getId());
		List<ChatRoomUser> connectedUsers = dreamTheaterChatRoom.getConnectedUsers();
		
		assertThat(connectedUsers.contains(jorgeAcetozi), is(true));
		assertThat(connectedUsers.contains(johnPetrucci), is(true));
	}
	
	@Test
	public void shouldLeaveUsersFromChatRoom() {
		ChatRoomUser jorgeAcetozi = new ChatRoomUser("jorge_acetozi");
		ChatRoomUser johnPetrucci = new ChatRoomUser("john_petrucci");
		
		chatRoomService.join(jorgeAcetozi, chatRoom);
		chatRoomService.join(johnPetrucci, chatRoom);
		assertThat(chatRoom.getNumberOfConnectedUsers(), is(2));
		
		chatRoomService.leave(jorgeAcetozi, chatRoom);
		chatRoomService.leave(johnPetrucci, chatRoom);
		assertThat(chatRoom.getNumberOfConnectedUsers(), is(0));
	}
}
