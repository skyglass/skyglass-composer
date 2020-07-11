package skyglass.composer.chat.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import skyglass.composer.chat.authentication.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

}
