package com.creatix.domain.mapper;

import com.creatix.domain.dto.notification.message.PersonalMessageAccountDto;
import com.creatix.domain.dto.notification.message.PersonalMessageDto;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.notification.PersonalMessage;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Created by kvimbi on 29/05/2017.
 */
@Component
public class PersonalMessageMapper extends ConfigurableMapper {

    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(PersonalMessage.class, PersonalMessageDto.class)
                .byDefault()
                .register();


        factory.classMap(Account.class, PersonalMessageAccountDto.class)
                .byDefault()
                .fieldAToB("id", "userId")
                .register();
    }


    public PersonalMessage toPersonalMessage(@NotNull PersonalMessageDto personalMessageDto) {
        Objects.requireNonNull(personalMessageDto, "Personal message object can not be null");
        return this.map(personalMessageDto, PersonalMessage.class);
    }

    public PersonalMessageDto toPersonalMessage(@NotNull PersonalMessage personalMessage) {
        Objects.requireNonNull(personalMessage, "Personal message object can not be null");
        return this.map(personalMessage, PersonalMessageDto.class);
    }
}
