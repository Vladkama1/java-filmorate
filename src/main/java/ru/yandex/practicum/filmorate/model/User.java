package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private int id;
    @NotBlank(message = "Email not null.")
    @Email(regexp = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,5}", message = "Incorrect Email.")
    private String email;
    @NotBlank(message = "Login not null.")
    @Pattern(regexp = "\\S*", message = "Login can`t spase.")
    private String login;
    private String name;
    @Past(message = "Birthday don`t future.")
    private LocalDate birthday;
}
