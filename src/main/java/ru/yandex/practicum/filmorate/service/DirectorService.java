package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.DirectorDTO;

import java.util.List;

public interface DirectorService {

    DirectorDTO findById(Long id);

    List<DirectorDTO> getAllDirector();

    DirectorDTO saveDirector(DirectorDTO directorDTO);

    DirectorDTO updateDirector(DirectorDTO directorDTO);

    void delete(Long id);
}
