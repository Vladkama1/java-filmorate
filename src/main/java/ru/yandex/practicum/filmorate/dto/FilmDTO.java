package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.anotation.MarkerOfCreate;
import ru.yandex.practicum.filmorate.anotation.MarkerOfUpdate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class FilmDTO {
    @Null(groups = MarkerOfCreate.class)
    @NotNull(groups = MarkerOfUpdate.class, message = "ID can`t null!")
    private Long id;
    @NotBlank(message = "Name can`t null!")
    private String name;
    @Size(min = 1, max = 200, message = "Max size 200!")
    private String description;
    //@DateIsAfter(value = FILM_RELEASE_DATE, message = "Data release before 28.12.1895 year")
    private LocalDate releaseDate;
    @Positive(message = "Duration not positive")
    private Integer duration;
    @JsonIgnore
    private Set<Long> likes;
    private Set<Genre> genres;
    private MPA mpa;
}
