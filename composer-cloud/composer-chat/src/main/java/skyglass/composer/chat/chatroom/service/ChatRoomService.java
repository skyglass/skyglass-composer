package skyglass.composer.chat.chatroom.service;

import java.util.List;

import skyglass.composer.chat.chatroom.domain.ChatRoom;
import skyglass.composer.chat.chatroom.domain.ChatRoomUser;
import skyglass.composer.chat.chatroom.domain.InstantMessage;

public interface ChatRoomService {
	
	ChatRoom save(ChatRoom chatRoom);
	ChatRoom findById(String chatRoomId);
	ChatRoom join(ChatRoomUser joiningUser, ChatRoom chatRoom);
	ChatRoom leave(ChatRoomUser leavingUser, ChatRoom chatRoom);
	void sendPublicMessage(InstantMessage instantMessage);
	void sendPrivateMessage(InstantMessage instantMessage);
	List<ChatRoom> findAll();
}
