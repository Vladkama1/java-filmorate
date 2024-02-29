package ru.yandex.practicum.filmorate.anotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE, CONSTRUCTOR})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NameByLoginValidator.class)

public @interface NameByLogin {
    String message() default "{}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
