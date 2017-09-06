package com.creatix.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by Tomas Sedlak on 6.9.2017.
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { UniqueSubtenantValidator.class })
public @interface UniqueEntityIdentifier {
    String message() default "Email address already taken.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
