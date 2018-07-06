package com.creatix.domain.mapper;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.dto.notification.message.PersonalMessageDto;
import com.creatix.domain.dto.notification.message.PersonalMessagePhotoDto;
import com.creatix.domain.entity.store.notification.PersonalMessage;
import com.creatix.domain.entity.store.notification.PersonalMessagePhoto;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.util.Objects;

@Component
public class PersonalMessageMapper extends ConfigurableMapper {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    protected void configure(MapperFactory factory) {
        super.configure(factory);

        factory.classMap(PersonalMessagePhoto.class, PersonalMessagePhotoDto.class)
                .byDefault()
                .customize(new CustomMapper<PersonalMessagePhoto, PersonalMessagePhotoDto>() {
                    @Override
                    public void mapAtoB(PersonalMessagePhoto personalMessagePhoto, PersonalMessagePhotoDto personalMessagePhotoDto, MappingContext context) {
                        try {
                            personalMessagePhotoDto.setFileUrl(getPhotoUrl(personalMessagePhoto));
                        } catch (MalformedURLException e) {
                            throw new IllegalStateException("Failed to create download URL", e);
                        }
                    }
                })
                .register();
    }

    @NotNull
    private String getPhotoUrl(@NotNull PersonalMessagePhoto personalMessagePhoto) throws MalformedURLException {
        return applicationProperties.buildBackendUrl(
                String.format(
                        "api/attachments/%d/%s",
                        personalMessagePhoto.getId(),
                        personalMessagePhoto.getFileName()
                )
        ).toString();
    }

    public PersonalMessageDto toPersonalMessageDto(@NotNull PersonalMessage personalMessage) {
        Objects.requireNonNull(personalMessage, "Personal message must not be null");
        return this.map(personalMessage, PersonalMessageDto.class);
    }


}
