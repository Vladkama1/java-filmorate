package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
    Validator validator;
    User user = User.builder()
            .name("user name")
            .email("lovt.vlad@mail.ru")
            .login("kama")
            .birthday(LocalDate.of(1997, 3, 24))
            .build();

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    void emailNotNullThisStandart() {
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        String valMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(""));
        assertEquals(2, violations.size());
        boolean iSmessageCheck = false;
        if (valMessage.equals("Incorrect Email.Email not null.") ||
                valMessage.equals("Email not null.Incorrect Email.")) {
            iSmessageCheck = true;
        }
        assertTrue(iSmessageCheck);
    }

    @Test
    void loginNotNullAndSpase() {
        user.setLogin(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        String valMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(""));
        assertEquals(2, violations.size());
        boolean iSmessageCheck = false;
        if (valMessage.equals("Login can`t spase.Login not null.") ||
                valMessage.equals("Login not null.Login can`t spase.")) {
            iSmessageCheck = true;
        }
        assertTrue(iSmessageCheck);
    }

    @Test
    void birthdayPast() {
        user.setBirthday(LocalDate.of(2050, 12, 28));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Birthday don`t future.", violations.iterator().next().getMessage());
    }
}