package skyglass.composer.security.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;

import skyglass.composer.dto.AEntityDTOFactory;
import skyglass.composer.security.domain.ResourceOwnerView;
import skyglass.composer.security.domain.User;

public class UserDTOFactory extends AEntityDTOFactory {
	public static UserDTO createUserDTO(User user) {
		UserDTO dto = new UserDTO();
		dto.setUuid(user.getUuid());
		dto.setUsername(user.getName());
		return dto;
	}

	public static List<UserDTO> createUserDTOs(Collection<User> users) {
		List<UserDTO> userDTOs = new ArrayList<>();
		if (users != null) {
			for (User user : users) {
				userDTOs.add(createUserDTO(user));
			}
		}
		return userDTOs;
	}

	@NotNull
	public static List<UserDTO> createUserDTOs(List<ExtUserDTO> extUsers, List<User> dbUsers) {
		if (extUsers == null || extUsers.isEmpty() || dbUsers == null || dbUsers.isEmpty()) {
			return Collections.emptyList();
		}

		List<UserDTO> dtos = new ArrayList<>();
		for (ExtUserDTO extUser : extUsers) {
			for (User user : dbUsers) {
				if (user != null && user.getName() != null) {
					if (user.getName().equals(extUser.getId())) {
						dtos.add(createUserDTO(user, extUser));
						break;
					}
				}
			}
		}
		return dtos;
	}

	@NotNull
	public static List<UserDTO> createUserDTOs(List<User> dbUsers, Function<User, ExtUserDTO> extUserDTOProvider) {
		if (extUserDTOProvider == null || dbUsers == null || dbUsers.isEmpty()) {
			return Collections.emptyList();
		}

		List<UserDTO> dtos = new ArrayList<>();
		for (User user : dbUsers) {
			if (user != null) {
				ExtUserDTO extUserDto = extUserDTOProvider.apply(user);
				if (extUserDto != null) {
					dtos.add(createUserDTO(user, extUserDto));
				}
			}
		}

		return dtos;
	}

	public static UserDTO createUserDTO(User user, ExtUserDTO extUserDTO) {
		if (user == null || extUserDTO == null) {
			return null;
		}
		UserDTO dto = new UserDTO();
		dto.setUuid(user.getUuid());
		dto.setUsername(user.getName());
		if (user.getGlobalRoles() != null && !user.getGlobalRoles().isEmpty()) {
			dto.setUserRoles(user.getGlobalRoles());
		}

		if (extUserDTO.getEmails() != null && !extUserDTO.getEmails().isEmpty()) {
			if (extUserDTO.getEmails().get(0) != null) {
				dto.setEmail(extUserDTO.getEmails().get(0).getValue());
			}
		}

		if (!CollectionUtils.isEmpty(extUserDTO.getPhoneNumbers())) {
			if (extUserDTO.getPhoneNumbers().get(0) != null) {
				dto.setPhoneNumber(extUserDTO.getPhoneNumbers().get(0).getValue());
			}
		}

		dto.setActive(extUserDTO.isActive() == null || extUserDTO.isActive());
		if (extUserDTO.getName() != null) {
			dto.setFirstName(extUserDTO.getName().getGivenName());
			dto.setLastName(extUserDTO.getName().getFamilyName());
		}
		if (extUserDTO.getGroups() != null) {
			for (ExtGroupForUserDTO group : extUserDTO.getGroups()) {
				dto.addGroups(group.getValue());
			}
		}

		dto.setOwnerUuid(user.getOwner().getUuid());
		dto.setOwnerName(user.getOwner().getName());
		return dto;
	}

	public static ExtUserDTO createUserDTO(UserDTO userDto) {
		if (userDto == null) {
			return null;
		}
		ExtUserDTO dto = new ExtUserDTO();
		dto.setId(userDto.getUsername());
		dto.setName(UserDTOFactory.createExtUserNameDTO(userDto));
		dto.setEmails(UserDTOFactory.createExtEmailDTO(userDto.getEmail()));
		dto.setActive(userDto.isActive());
		dto.setGroups(createExtGroupDTO(userDto.getGroups()));
		dto.setPhoneNumbers(createExtPhoneNumberDTOs(userDto.getPhoneNumber()));
		return dto;
	}

	public static List<ExtGroupForUserDTO> createExtGroupDTO(List<String> userGroups) {
		List<ExtGroupForUserDTO> groupDtos = new ArrayList<>();
		if (userGroups == null) {
			return groupDtos;
		}
		for (String group : userGroups) {
			ExtGroupForUserDTO groupDto = new ExtGroupForUserDTO();
			groupDto.setValue(group);
			groupDtos.add(groupDto);
		}
		return groupDtos;
	}

	public static List<ExtEmailDTO> createExtEmailDTO(String userEmail) {
		List<ExtEmailDTO> emails = new ArrayList<>();
		if (userEmail == null) {
			return emails;
		}
		ExtEmailDTO email = new ExtEmailDTO();
		email.setValue(userEmail);
		emails.add(email);
		return emails;
	}

	public static List<ExtPhoneNumberDTO> createExtPhoneNumberDTOs(String phoneNumber) {
		List<ExtPhoneNumberDTO> numbers = new ArrayList<>();
		if (phoneNumber == null) {
			return numbers;
		}
		ExtPhoneNumberDTO number = new ExtPhoneNumberDTO(phoneNumber);
		numbers.add(number);
		return numbers;
	}

	public static ExtUserNameDTO createExtUserNameDTO(UserDTO userDto) {
		ExtUserNameDTO name = new ExtUserNameDTO();
		if (userDto == null) {
			return name;
		}
		name.setGivenName(userDto.getFirstName());
		name.setFamilyName(userDto.getLastName());
		return name;
	}

	public static User createUser(UserDTO dto, ResourceOwnerView owner) {
		User user = new User();

		user.setName(dto.getUsername());

		if (dto.getUserRoles() != null && !dto.getUserRoles().isEmpty()) {
			user.setGlobalRoles(dto.getUserRoles());
		}

		if (dto.getUuid() != null) {
			user.setUuid(dto.getUuid());
		}

		user.setOwner(owner);

		return user;
	}

	public static List<User> createUsers(List<UserDTO> users, ResourceOwnerView owner) {
		if (CollectionUtils.isEmpty(users)) {
			return new ArrayList<>();
		}
		return users.stream().map(user -> createUser(user, owner)).collect(Collectors.toList());
	}

}
