package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.anotation.MarkerOfCreate;
import ru.yandex.practicum.filmorate.anotation.MarkerOfUpdate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static ru.yandex.practicum.filmorate.constants.UserConstant.EMAIL_REGEX;
import static ru.yandex.practicum.filmorate.constants.UserConstant.LOGIN_REGEX;
@Data
@Builder
public class UserDTO {
    @Null(groups = MarkerOfCreate.class)
    @NotNull(groups = MarkerOfUpdate.class,message = "ID can`t null!")
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
