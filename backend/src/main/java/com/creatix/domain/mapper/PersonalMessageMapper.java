package com.creatix.domain.mapper;

import com.creatix.domain.dto.notification.message.PersonalMessageAccountDto;
import com.creatix.domain.dto.notification.message.PersonalMessageDto;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.notification.PersonalMessage;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by kvimbi on 29/05/2017.
 */
@Component
public class PersonalMessageMapper extends ConfigurableMapper {

    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(PersonalMessage.class, PersonalMessageDto.class)
                .byDefault()
                .customize(new CustomMapper<PersonalMessage, PersonalMessageDto>() {
                    @Override
                    public void mapAtoB(PersonalMessage a, PersonalMessageDto b, MappingContext context) {
                        if ( a.getPersonalMessageGroup() != null ) {
                            b.setRecipients(factory.getMapperFacade().mapAsList(a.getPersonalMessageGroup().getMessages().stream().map(PersonalMessage::getToAccount).collect(Collectors.toList()), PersonalMessageAccountDto.class));
                        }
                    }
                })
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
