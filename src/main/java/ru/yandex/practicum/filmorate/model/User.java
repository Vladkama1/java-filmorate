package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static ru.yandex.practicum.filmorate.constants.UserConstant.EMAIL_REGEX;
import static ru.yandex.practicum.filmorate.constants.UserConstant.LOGIN_REGEX;

@Data
@Builder
public class User {
    private Long id;
    @NotBlank(message = "Email not null.")
    @Email(regexp = EMAIL_REGEX, message = "Incorrect Email.")
    private String email;
    @NotBlank(message = "Login not null.")
    @Pattern(regexp = LOGIN_REGEX, message = "Login can`t spase.")
    private String login;
    private String name;
    @Past(message = "Birthday don`t future.")
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();
}
