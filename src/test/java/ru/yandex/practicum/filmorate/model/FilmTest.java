package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmTest {
    Validator validator;
    FilmDTO film = FilmDTO.builder()
            .name("film name")
            .description("film description")
            .releaseDate(LocalDate.of(1997, 3, 24))
            .duration(100)
            .build();

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    void notNullName() {
        film.setName("");
        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Name can`t null!", violations.iterator().next().getMessage());
    }

    @Test
    void descriptionSizeMax() {
        film.setDescription("В альтернативном Лос-Анджелесе кого только не встретишь\n" +
                "- бок о бок с людьми живут эльфы, орки и даже кентавры. Эльфы, правда, \n" +
                "немного брезгуют всеми остальными, поэтому устроили себе отдельный район, \n" +
                "куда въезд только по пропускам. А вот людям приходится терпеть грубых и склонных\n" +
                " к преступлениям орков, те предпочитают селиться в криминальных гетто.\n" +
                "Патрульный полицейский Дэрил Ворд вынужден работать в паре с орком-полицейским Джакоби\n" +
                " - та ещё невидаль.");
        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Max size 200!", violations.iterator().next().getMessage());
    }

    @Test
    void createFailDuration() {
        film.setDuration(-100);
        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Duration not positive", violations.iterator().next().getMessage());
    }
}