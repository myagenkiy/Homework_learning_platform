package com.example.learning_platform;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LearningPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningPlatformApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User user = new User("Иван Мишутин", "ivan@example.com");

                Profile profile = new Profile("Люблю Java и базы данных", "https://example.com/avatar.png");
                profile.setUser(user);
                user.setProfile(profile);

                userRepository.save(user);
                System.out.println(">>> Сохранён пользователь с профилем, id = " + user.getId());
            }
        };
    }
}