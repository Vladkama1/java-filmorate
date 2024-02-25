package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MpaMapper {
    MPA toModel(MpaDTO mpaDTO);

    MpaDTO toDTO(MPA mpa);

    List<MPA> toListModels(List<MpaDTO> mpaDTOList);

    List<MpaDTO> toListDTO(List<MPA> mpaList);
}
