package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.anotation.MarkerOfCreate;
import ru.yandex.practicum.filmorate.anotation.MarkerOfUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
public class ReviewDTO {
    @Null(groups = MarkerOfCreate.class)
    @NotNull(groups = MarkerOfUpdate.class, message = "ID can`t null!")
    private Long reviewId;
    @NotBlank(message = "Content can`t null!")
    private String content;
    @NotNull(message = "IsPositive can`t null!")
    private Boolean isPositive;
    @NotNull(message = "UserId can`t null!")
    private Long userId;
    @NotNull(message = "FilmId can`t null!")
    private Long filmId;
    private Integer useful;
}
