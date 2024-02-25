package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.storage.MpaDAO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaDAO mpaDAO;
    private final MpaMapper mpaMapper;

    @Override
    public MpaDTO findById(Long id) {
        return mpaMapper.toDTO(mpaDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<MpaDTO> getAllMPA() {
        return mpaMapper.toListDTO(mpaDAO.findAll());
    }
}
