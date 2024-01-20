package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FilmMapper {
    Film toModel(FilmDTO filmDTO);

    FilmDTO toDTO(Film film);

    List<Film> toListModels(List<FilmDTO> filmDTOList);

    List<FilmDTO> toListDTO(List<Film> filmList);
}
