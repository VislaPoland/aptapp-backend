package com.creatix.validator;

import com.creatix.domain.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Tomas Sedlak on 6.9.2017.
 */
@Component("beforeCreateUniqueSubtenantValidator")
public class UniqueSubtenantValidator implements ConstraintValidator<UniqueEntityIdentifier, String> {

    private final AccountDao accountDao;

    public UniqueSubtenantValidator(@Autowired AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void initialize(UniqueEntityIdentifier constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean valid = true;
        if ( accountDao.findByEmail(value) != null ) {
            valid = false;
        }
        return valid;
    }
}
