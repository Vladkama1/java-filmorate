package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toModel(UserDTO userDTO);

    UserDTO toDTO(User user);

    List<User> toListModels(List<UserDTO> userDTOList);

    List<UserDTO> toListDTO(List<User> userList);
}
