package com.creatix.domain;

import com.creatix.domain.dto.account.AccountDto;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.entity.Account;
import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.PropertyOwner;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Component
public class Mapper {

    private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    static {
        mapperFactory.classMap(Account.class, AccountDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Property.class, PropertyDetailsDto.class)
                .byDefault()
                .field("address.fullAddress", "address")
                .customize(new CustomMapper<Property, PropertyDetailsDto>() {
                    @Override
                    public void mapAtoB(Property property, PropertyDetailsDto propertyDetailsDto, MappingContext context) {

                    }
                })
                .register();

        mapperFactory.classMap(PropertyOwner.class, PropertyDetailsDto.Owner.class)
                .field("primaryEmail", "email")
                .field("fullName", "name")
                .field("website", "web")
                .field("primaryPhone", "phone")
                .register();
    }


    public AccountDto toAccountDto(Account account) {
        Objects.requireNonNull(account);

        return mapperFactory.getMapperFacade().map(account, AccountDto.class);
    }

    public PropertyDetailsDto toPropertyDetailsDto(@NotNull Property property) {
        Objects.requireNonNull(property);
        return mapperFactory.getMapperFacade().map(property, PropertyDetailsDto.class);
    }

}
