package com.creatix.security;

import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.ApplicationFeatureType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RoleSecured
{
    AccountRole[] value() default {};
    ApplicationFeatureType feature() default ApplicationFeatureType.NONE;
}
