package com.creatix.domain.mapper;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.dto.property.slot.EventPhotoDto;
import com.creatix.domain.dto.property.slot.EventSlotDto;
import com.creatix.domain.entity.store.EventSlot;
import com.creatix.domain.entity.store.attachment.EventPhoto;
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
public class EventSlotMapper extends ConfigurableMapper {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    protected void configure(MapperFactory factory) {
        super.configure(factory);

        factory.classMap(EventPhoto.class, EventPhotoDto.class)
                .byDefault()
                .customize(new CustomMapper<EventPhoto, EventPhotoDto>() {
                    @Override
                    public void mapAtoB(EventPhoto eventPhoto, EventPhotoDto eventPhotoDto, MappingContext context) {
                        try {
                            eventPhotoDto.setFileUrl(getPhotoUrl(eventPhoto));
                        } catch (MalformedURLException e) {
                            throw new IllegalStateException("Failed to create download URL", e);
                        }
                    }
                })
                .register();
    }

    @NotNull
    private String getPhotoUrl(@NotNull EventPhoto eventPhoto) throws MalformedURLException {
        return applicationProperties.buildBackendUrl(
                String.format(
                        "api/attachments/%d/%s",
                        eventPhoto.getId(),
                        eventPhoto.getFileName()
                )
        ).toString();
    }

    public EventSlotDto toEventSlotDto(@NotNull EventSlot eventSlot) {
        Objects.requireNonNull(eventSlot, "Event slot must not be null");
        return this.map(eventSlot, EventSlotDto.class);
    }


}
