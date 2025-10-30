package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @PutMapping
    public User create(@Valid @RequestBody User user) {
        log.info("создание user");
        UserValidator.validate(user);
        return user;
    }

    @PostMapping
    public User update(@Valid @RequestBody User user) {
        log.info("обновление user");
        UserValidator.validate(user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("вывод списка всех users");
        return new ArrayList<User>();
    }
}
