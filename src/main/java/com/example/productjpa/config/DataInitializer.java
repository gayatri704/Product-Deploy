package com.example.productjpa.config;

import com.example.productjpa.entity.User;
import com.example.productjpa.entity.UserRole;
import com.example.productjpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        List<User> users = userRepository.findAll();
        boolean changed = false;
        for (User user : users) {
            if (user.getRole() == null) {
                user.setRole(UserRole.CUSTOMER);
                changed = true;
            }
        }
        if (changed) {
            userRepository.saveAll(users);
        }
        if (users.stream().noneMatch(u -> u.getRole() == UserRole.ADMIN)) {
            users.stream().findFirst().ifPresent(first -> {
                first.setRole(UserRole.ADMIN);
                userRepository.save(first);
            });
        }
    }
}
