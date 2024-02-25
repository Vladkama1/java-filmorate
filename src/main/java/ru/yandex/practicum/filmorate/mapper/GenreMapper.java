package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    Genre toModel(GenreDTO genreDTO);

    GenreDTO toDTO(Genre genre);

    List<Genre> toListModels(List<GenreDTO> genreDTOList);

    List<GenreDTO> toListDTO(List<Genre> genreList);
}
