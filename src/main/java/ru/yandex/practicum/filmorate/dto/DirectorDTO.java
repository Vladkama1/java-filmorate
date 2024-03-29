package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.anotation.MarkerOfUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class DirectorDTO {
    @NotNull(groups = MarkerOfUpdate.class, message = "The ID must not be empty.")
    private Long id;
    @NotBlank(message = "The name of the mpa must not be empty.")
    private String name;
}
