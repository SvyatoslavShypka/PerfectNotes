package com.goit.finalproject.user;

import com.goit.finalproject.role.Role;
import com.goit.finalproject.role.RoleRepository;
import com.goit.finalproject.role.RoleService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Data
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public Page<User> listAll(Pageable pageable) {
        System.out.println("AllUsersAmount: " + userRepository.findAll().size());
        return userRepository.findAll(pageable);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("There is no such user"));
    }

    public User getReferenceById(Long id) {
        return userRepository.getReferenceById(id);
    }

    public void createUser(User user) {
        Role userRole = roleService.getRoleByName("USER");
        user.setRoles(new HashSet<Role>(Collections.singletonList(userRole)));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUser(User user) {
        setPassword(user, user.getPassword());
        userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void setPassword(User user, String password) {
        String passwordHash = passwordEncoder.encode(password);
        user.setPassword(passwordHash);
    }

    public String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public Long getUserId() {
        return userRepository.findUserByUsername(getUsername()).getId();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("There is no such user");
        }
        return user;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }
}
