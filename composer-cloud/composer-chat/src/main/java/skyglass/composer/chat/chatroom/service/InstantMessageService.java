package skyglass.composer.chat.chatroom.service;

import java.util.List;

import skyglass.composer.chat.chatroom.domain.InstantMessage;

public interface InstantMessageService {
	
	void appendInstantMessageToConversations(InstantMessage instantMessage);
	List<InstantMessage> findAllInstantMessagesFor(String username, String chatRoomId);
}
