package skyglass.composer.chat.chatroom.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import skyglass.composer.chat.chatroom.domain.ChatRoom;
import skyglass.composer.chat.chatroom.domain.ChatRoomUser;
import skyglass.composer.chat.chatroom.domain.InstantMessage;
import skyglass.composer.chat.chatroom.domain.InstantMessageBuilder;
import skyglass.composer.chat.chatroom.repository.ChatRoomRepository;
import skyglass.composer.chat.chatroom.service.ChatRoomService;
import skyglass.composer.chat.chatroom.service.InstantMessageService;
import skyglass.composer.chat.chatroom.service.RedisChatRoomService;
import skyglass.composer.chat.utils.Destinations;
import skyglass.composer.chat.utils.SystemMessages;

@RunWith(MockitoJUnitRunner.class)
public class RedisChatRoomServiceUnitTest {

	@InjectMocks
	private ChatRoomService chatRoomService = new RedisChatRoomService();

	@Mock
	private SimpMessagingTemplate webSocketMessagingTemplate;

	@Mock
	private ChatRoomRepository chatRoomRepository;

	@Mock
	private InstantMessageService instantMessageService;

	@Captor
	private ArgumentCaptor<ChatRoom> chatRoomCaptor;

	@Captor
	private ArgumentCaptor<String> toUserCaptor;

	@Captor
	private ArgumentCaptor<String> destinationCaptor;

	@Captor
	private ArgumentCaptor<InstantMessage> instantMessageCaptor;

	@Captor
	private ArgumentCaptor<Object> messageObjectCaptor;

	@Test
	public void shouldJoinUserToChatRoom() {
		ChatRoom chatRoom = new ChatRoom("123", "Dream Theater", "Discuss about best band ever!");

		assertThat(chatRoom.getNumberOfConnectedUsers(), is(0));

		ChatRoomUser user = new ChatRoomUser("jorge_acetozi");
		chatRoomService.join(user, chatRoom);

		assertThat(chatRoom.getNumberOfConnectedUsers(), is(1));

		verify(chatRoomRepository, times(1)).save(chatRoom);
		verify(webSocketMessagingTemplate, times(2)).convertAndSend(destinationCaptor.capture(),
				messageObjectCaptor.capture());
		verify(instantMessageService, times(1)).appendInstantMessageToConversations(instantMessageCaptor.capture());

		List<String> destinations = destinationCaptor.getAllValues();
		List<Object> messageObjects = messageObjectCaptor.getAllValues();
		InstantMessage instantMessageToBeAppendedToConversations = instantMessageCaptor.getValue();

		String publicMessagesDestination = destinations.get(0);
		String connectedUsersDestination = destinations.get(1);
		InstantMessage welcomeMessage = (InstantMessage) messageObjects.get(0);
		List<ChatRoomUser> connectedUsers = (List<ChatRoomUser>) messageObjects.get(1);

		assertThat(publicMessagesDestination, is(Destinations.ChatRoom.publicMessages(chatRoom.getId())));
		assertThat(connectedUsersDestination, is(Destinations.ChatRoom.connectedUsers(chatRoom.getId())));
		assertEquals(SystemMessages.welcome(chatRoom.getId(), user.getUsername()), welcomeMessage);
		assertEquals(SystemMessages.welcome(chatRoom.getId(), user.getUsername()),
				instantMessageToBeAppendedToConversations);
		assertThat(connectedUsers.contains(user), is(true));
	}

	@Test
	public void shouldLeaveUserFromChatRoom() {
		ChatRoom chatRoom = new ChatRoom("123", "Dream Theater", "Discuss about best band ever!");
		ChatRoomUser user = new ChatRoomUser("jorge_acetozi");
		chatRoom.addUser(user);

		assertThat(chatRoom.getNumberOfConnectedUsers(), is(1));

		chatRoomService.leave(user, chatRoom);

		assertThat(chatRoom.getNumberOfConnectedUsers(), is(0));

		verify(chatRoomRepository, times(1)).save(chatRoom);
		verify(webSocketMessagingTemplate, times(2)).convertAndSend(destinationCaptor.capture(),
				messageObjectCaptor.capture());
		verify(instantMessageService, times(1)).appendInstantMessageToConversations(instantMessageCaptor.capture());

		List<String> destinations = destinationCaptor.getAllValues();
		List<Object> messageObjects = messageObjectCaptor.getAllValues();
		InstantMessage instantMessageToBeAppendedToConversations = instantMessageCaptor.getValue();

		String publicMessagesDestination = destinations.get(0);
		String connectedUsersDestination = destinations.get(1);
		InstantMessage goodbyeMessage = (InstantMessage) messageObjects.get(0);
		List<ChatRoomUser> connectedUsers = (List<ChatRoomUser>) messageObjects.get(1);

		assertThat(publicMessagesDestination, is(Destinations.ChatRoom.publicMessages(chatRoom.getId())));
		assertThat(connectedUsersDestination, is(Destinations.ChatRoom.connectedUsers(chatRoom.getId())));
		assertEquals(SystemMessages.goodbye(chatRoom.getId(), user.getUsername()), goodbyeMessage);
		assertEquals(SystemMessages.goodbye(chatRoom.getId(), user.getUsername()),
				instantMessageToBeAppendedToConversations);
		assertThat(connectedUsers.contains(user), is(false));
	}

	@Test
	public void shouldSendPublicMessage() {
		ChatRoom chatRoom = new ChatRoom("123", "Dream Theater", "Discuss about best band ever!");
		ChatRoomUser user = new ChatRoomUser("jorge_acetozi");
		chatRoom.addUser(user);

		assertThat(chatRoom.getNumberOfConnectedUsers(), is(1));

		InstantMessage publicMessage = new InstantMessageBuilder().newMessage().withChatRoomId(chatRoom.getId())
				.publicMessage().fromUser(user.getUsername()).withText("This is a public message!");

		chatRoomService.sendPublicMessage(publicMessage);

		verify(webSocketMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(),
				messageObjectCaptor.capture());
		verify(instantMessageService, times(1)).appendInstantMessageToConversations(instantMessageCaptor.capture());

		String sentDestination = destinationCaptor.getValue();
		InstantMessage sentMessage = (InstantMessage) messageObjectCaptor.getValue();
		InstantMessage instantMessageToBeAppendedToConversations = instantMessageCaptor.getValue();

		assertThat(sentDestination, is(Destinations.ChatRoom.publicMessages(chatRoom.getId())));
		assertEquals(publicMessage, sentMessage);
		assertEquals(publicMessage, instantMessageToBeAppendedToConversations);
	}

	@Test
	public void shouldSendPrivateMessage() {
		ChatRoom chatRoom = new ChatRoom("123", "Dream Theater", "Discuss about best band ever!");
		ChatRoomUser jorgeAcetozi = new ChatRoomUser("jorge_acetozi");
		ChatRoomUser johnPetrucci = new ChatRoomUser("john_petrucci");
		chatRoom.addUser(jorgeAcetozi);
		chatRoom.addUser(johnPetrucci);

		assertThat(chatRoom.getNumberOfConnectedUsers(), is(2));

		InstantMessage privateMessage = new InstantMessageBuilder().newMessage().withChatRoomId(chatRoom.getId())
				.privateMessage().toUser(johnPetrucci.getUsername()).fromUser(jorgeAcetozi.getUsername())
				.withText("Hello John, can you teach me how to play guitar?");

		chatRoomService.sendPrivateMessage(privateMessage);

		verify(webSocketMessagingTemplate, times(2)).convertAndSendToUser(toUserCaptor.capture(),
				destinationCaptor.capture(), messageObjectCaptor.capture());
		verify(instantMessageService, times(1)).appendInstantMessageToConversations(instantMessageCaptor.capture());

		List<String> toUsers = toUserCaptor.getAllValues();
		List<String> destinations = destinationCaptor.getAllValues();
		List<Object> messageObjects = messageObjectCaptor.getAllValues();
		InstantMessage instantMessageToBeAppendedToConversations = instantMessageCaptor.getValue();

		String messageOneSentToUser = toUsers.get(0);
		String messageTwoSentToUser = toUsers.get(1);

		String messageOneDestination = destinations.get(0);
		String messageTwoDestination = destinations.get(1);

		InstantMessage messageOne = (InstantMessage) messageObjects.get(0);
		InstantMessage messageTwo = (InstantMessage) messageObjects.get(1);

		assertThat(messageOneDestination, is(Destinations.ChatRoom.privateMessages(chatRoom.getId())));
		assertEquals(privateMessage, messageOne);
		assertThat(messageOneSentToUser, is(johnPetrucci.getUsername()));

		assertThat(messageTwoDestination, is(Destinations.ChatRoom.privateMessages(chatRoom.getId())));
		assertEquals(privateMessage, messageTwo);
		assertThat(messageTwoSentToUser, is(jorgeAcetozi.getUsername()));

		assertEquals(privateMessage, instantMessageToBeAppendedToConversations);
	}

	@Test
	public void shouldSaveNewChatRoom() {
		ChatRoom chatRoom = new ChatRoom("123", "Dream Theater", "Discuss about best band ever!");
		chatRoomService.save(chatRoom);
		verify(chatRoomRepository, times(1)).save(chatRoom);
	}

	@Test
	public void shouldFindChatRoomById() {
		chatRoomService.findById("123");
		verify(chatRoomRepository, times(1)).findById("123");
	}

	@Test
	public void shouldFindAllChatRooms() {
		chatRoomService.findAll();
		verify(chatRoomRepository, times(1)).findAll();
	}
}
