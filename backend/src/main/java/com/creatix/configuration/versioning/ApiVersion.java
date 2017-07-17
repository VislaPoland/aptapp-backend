package com.creatix.configuration.versioning;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author Tomas Michalek
 *         23/02/2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface ApiVersion {


    @AliasFor("minVersion")
    double value() default 0.0;

    /**
     *
     * @return
     */
    @AliasFor("value")
    double minVersion() default 0.0;

    /**
     *
     * @return
     */
    double maxVersion() default 0.0;

}

