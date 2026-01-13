package org.upb.users;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.upb.users.dtos.CreateUserDTO;
import org.upb.users.dtos.UserDTO;
import org.upb.users.entity.User;
import org.upb.users.enums.UserStatus;
import org.upb.users.mapper.UserMapper;
import org.upb.users.repository.UserRepository;
import org.upb.users.services.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest // Testul principal verifică doar dacă pornește aplicația
class UserApplicationTests {

	@Test
	void contextLoads() {
	}


	@Nested
	@ExtendWith(MockitoExtension.class)
	class UserServiceTests {

		@Mock
		private UserRepository userRepository;

		@Mock
		private UserMapper userMapper;

		@InjectMocks
		private UserService userService;

		@Test
		void createUser_ShouldReturnUserDTO_WhenValid() {

			CreateUserDTO createDTO = new CreateUserDTO("testuser", "pass", "test@mail.com", "Test", "User");
			User userEntity = new User("testuser", "pass", "test@mail.com", "Test", "User");
			UserDTO expectedDTO = new UserDTO();
			expectedDTO.setUsername("testuser");

			when(userRepository.existsByUsername("testuser")).thenReturn(false);
			when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
			when(userRepository.save(any(User.class))).thenReturn(userEntity);
			when(userMapper.toDTO(userEntity)).thenReturn(expectedDTO);


			UserDTO result = userService.createUser(createDTO);


			assertNotNull(result);
			assertEquals("testuser", result.getUsername());
			verify(userRepository).save(any(User.class));
		}
	}


	@Nested
	@DataJpaTest
	class UserRepositoryTests {

		@Autowired
		private UserRepository userRepository;

		@Test
		void saveAndFindUser_ShouldPersistData() {

			User user = new User("junitUser", "pass123", "junit@test.com", "Junit", "Test");


			User savedUser = userRepository.save(user);
			Optional<User> foundUser = userRepository.findById(savedUser.getId());


			assertTrue(foundUser.isPresent());
			assertEquals("junitUser", foundUser.get().getUsername());
		}

		@Test
		void findByNameContaining_ShouldReturnMatchingUsers() {

			User u1 = new User("u1", "p", "e1@t.com", "Alpha", "One");
			User u2 = new User("u2", "p", "e2@t.com", "Beta", "Two");
			userRepository.save(u1);
			userRepository.save(u2);


			List<User> results = userRepository.findByNameContaining("Alpha");


			assertEquals(1, results.size());
			assertEquals("u1", results.get(0).getUsername());
		}
	}
}