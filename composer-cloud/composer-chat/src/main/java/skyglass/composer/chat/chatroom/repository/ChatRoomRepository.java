package skyglass.composer.chat.chatroom.repository;

import org.springframework.data.repository.CrudRepository;

import skyglass.composer.chat.chatroom.domain.ChatRoom;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, String> {

}
