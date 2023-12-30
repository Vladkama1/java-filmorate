package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank(message = "Name can`t null!")
    private String name;
    @Size(min = 1,max = 200,message = "Max size 200!")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Duration not positive")
    private int duration;
}
