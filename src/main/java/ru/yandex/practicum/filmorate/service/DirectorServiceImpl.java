package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.DirectorDAO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorDAO directorDAO;
    private final DirectorMapper mapper;

    @Override
    public DirectorDTO findById(Long id) {
        return mapper.toDTO(directorDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<DirectorDTO> getAllDirector() {
        return mapper.toListDTO(directorDAO.findAll());
    }

    @Override
    public DirectorDTO saveDirector(DirectorDTO directorDTO) {
        return mapper.toDTO(directorDAO.save(mapper.toModel(directorDTO)));
    }

    @Override
    public DirectorDTO updateDirector(DirectorDTO directorDTO) {
        return mapper.toDTO(directorDAO.update(mapper.toModel(directorDTO))
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public void delete(Long id) {
        boolean deleted = directorDAO.delete(id);
        if (!deleted) {
            throw new NotFoundException("Режиссёр не найден", HttpStatus.NOT_FOUND);
        }
    }
}
