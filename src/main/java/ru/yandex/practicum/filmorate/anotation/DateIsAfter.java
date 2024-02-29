package ru.yandex.practicum.filmorate.anotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = DateAfterValidator.class)
public @interface DateIsAfter {
    String message() default "дата не может быть раньше заданного значения {value}";

    Class<?>[] groups() default {};

    String value() default "1970-01-01";

    Class<? extends Payload>[] payload() default {};
}
