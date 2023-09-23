package com.goit.finalproject.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequiredArgsConstructor
@Secured("ADMIN")
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public ModelAndView showAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("show users for {}", userService.getUserId());
        return new ModelAndView("user/users")
                .addObject("users", userService.listAll(PageRequest.of(page, size)));
    }

    @GetMapping("/user/addUser")
    public ModelAndView getAddUserPage() {
        return new ModelAndView("user/add-user").addObject("user", new User());
    }

    @PostMapping("/user/addUser")
    public ModelAndView addUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("user/add-user");
        }

        if (userService.findUserByUsername(user.getUsername()) != null) {
            return new ModelAndView("user/add-user").addObject("error", true);
        }

        userService.createUser(user);
        log.info("admin {} add new user {}", userService.getUserId(), user.getUsername());
        return new ModelAndView("redirect:/users");
    }

    @GetMapping("/user/edit/{id}")
    public ModelAndView showUpdateForm(@PathVariable("id") Long id) {
        return new ModelAndView("user/edit-user").addObject("user", userService.getReferenceById(id));
    }

    @PostMapping("/user/edit/{id}")
    public ModelAndView updateUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("bindingResult.hasErrors()");
            return new ModelAndView("user/edit-user");
        }
        User existUser = userService.findUserByUsername(user.getUsername());
        if (existUser == null) {
            log.info("existUser: null");
        } else log.info("existUser: {}", existUser.getId());
        log.info("editedUser: {}", user.getUsername());
        if (existUser != null && !user.getId().equals(existUser.getId())) {
            return new ModelAndView("user/edit-user").addObject("error", true);
        }
        userService.updateUser(user);
        log.info("admin {} edit user {}", userService.getUserId(), user.getUsername());
        return new ModelAndView("redirect:/users");
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }

}
