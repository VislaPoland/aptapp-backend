package com.creatix.domain;

import com.creatix.domain.dto.AccountDto;
import com.creatix.domain.entity.Account;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    static {
        mapperFactory.classMap(Account.class, AccountDto.class)
                .byDefault()
                .register();
    }


    public AccountDto toAccountDto(Account account) {
        if ( account == null ) {
            throw new NullPointerException();
        }

        return mapperFactory.getMapperFacade().map(account, AccountDto.class);
    }

}
