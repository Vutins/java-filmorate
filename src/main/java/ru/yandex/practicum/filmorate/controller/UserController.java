package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @PutMapping
    public User create(@Valid @RequestBody User user) {
        return user;
    }

    @PostMapping
    public User update(@Valid @RequestBody User user) {
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<User>();
    }
}
