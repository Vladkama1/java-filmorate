package ru.yandex.practicum.filmorate.anotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateAfterValidator implements ConstraintValidator<DateIsAfter, LocalDate> {
    private LocalDate valueDate;

    @Override
    public void initialize(DateIsAfter constraintAnnotation) {
        valueDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return !valueDate.isAfter(localDate);
    }
}
