package ru.yandex.practicum.filmorate.anotation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class NameByLoginValidator implements ConstraintValidator<NameByLogin, UserDTO> {

    @Override
    public void initialize(NameByLogin constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserDTO userDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (userDTO.getName() == null || userDTO.getName().isBlank()) {
            userDTO.setName(userDTO.getLogin());
            log.warn("Имя отсутствует, поэтому имя заменено на логин: {}!", userDTO.getLogin());
        }
        return true;
    }
}
