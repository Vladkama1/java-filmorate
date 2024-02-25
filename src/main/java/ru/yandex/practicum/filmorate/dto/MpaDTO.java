package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
public class MpaDTO {
    @Null
    @NotNull(message = "The ID must not be empty.")
    private Long id;
    @NotBlank(message = "The name of the mpa must not be empty.")
    private String name;
}
