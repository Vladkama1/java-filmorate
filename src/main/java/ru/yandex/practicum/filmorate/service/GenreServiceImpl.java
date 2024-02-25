package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.GenreDAO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreDAO genreDAO;
    private final GenreMapper genreMapper;

    @Override
    public GenreDTO findById(Long id) {
        return genreMapper.toDTO(genreDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<GenreDTO> findAll() {
        return genreMapper.toListDTO(genreDAO.findAll());
    }
}
