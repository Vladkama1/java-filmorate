package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FilmorateApplication implements CommandLineRunner {
    @Value("${program.url}")
    private String progUrl;

    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Program started on - {}", progUrl);
    }
}
