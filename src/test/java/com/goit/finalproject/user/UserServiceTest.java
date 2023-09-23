package com.goit.finalproject.user;

import com.goit.finalproject.role.Role;
import com.goit.finalproject.role.RoleRepository;
import com.goit.finalproject.role.RoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private RoleRepository roleRepository;

    @Mock
    private RoleService roleService;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testLoadUserByUsername() {
        User user = createTestAccount();

        Mockito.when(userRepository.findUserByUsername(user.getUsername())).thenReturn(user);

        User testUser = (User) userService.loadUserByUsername(user.getUsername());
        Assertions.assertEquals(user.getUsername(), testUser.getUsername());
    }

    @Test
    void testGetUserById() {
        User user = createTestAccount();

        userService.createUser(user);

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User testUser = userService.getUserById(user.getId());
        Assertions.assertEquals(user.getUsername(), testUser.getUsername());
    }

    @Test
    void testIncorrectGetUserById() {
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserById(100L));
    }

    @Test
    void testCreateUser() {
        User user = createTestAccount();

        Mockito.when(roleService.getRoleByName("USER")).thenReturn(new Role());
        Mockito.when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        userService.createUser(user);
        Mockito.verify(roleService).getRoleByName("USER");
        Mockito.verify(passwordEncoder).encode(user.getPassword());
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser() {
        User user = createTestAccount();
        User updateUser =  createTestNewAccount();
        userService.updateUser(updateUser);

        Assertions.assertEquals(user.getId(), updateUser.getId());
        Assertions.assertNotEquals(user.getUsername(), updateUser.getUsername());
        Assertions.assertNotEquals(user.getPassword(), updateUser.getPassword());
    }

    private User createTestAccount() {
        return User.builder()
                .username("username")
                .password("testPassword")
                .build();
    }

    private User createTestNewAccount() {
        return User.builder()
                .username("new_username")
                .password("new_testPassword")
                .build();
    }

}
