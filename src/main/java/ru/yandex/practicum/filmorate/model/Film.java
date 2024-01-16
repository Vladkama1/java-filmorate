package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    @NotBlank(message = "Name can`t null!")
    private String name;
    @Size(min = 1, max = 200, message = "Max size 200!")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Duration not positive")
    private int duration;
    private final Set<Long> likes = new HashSet<>();
}
