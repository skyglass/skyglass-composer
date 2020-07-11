package skyglass.composer.chat.chatroom.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.MapIdCassandraRepository;

import skyglass.composer.chat.chatroom.domain.InstantMessage;

public interface InstantMessageRepository extends MapIdCassandraRepository<InstantMessage> {

	List<InstantMessage> findInstantMessagesByUsernameAndChatRoomId(String username, String chatRoomId);
}
