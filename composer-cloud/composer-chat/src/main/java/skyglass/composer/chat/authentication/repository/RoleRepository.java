package skyglass.composer.chat.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import skyglass.composer.chat.authentication.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	Role findByName(String name);
}
