package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.anotation.MarkerOfCreate;
import ru.yandex.practicum.filmorate.anotation.MarkerOfUpdate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
@Data
@Builder
public class FilmDTO {
    @Null(groups = MarkerOfCreate.class)
    @NotNull(groups = MarkerOfUpdate.class,message = "ID can`t null!")
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
