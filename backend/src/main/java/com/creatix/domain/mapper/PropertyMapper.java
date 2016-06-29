package com.creatix.domain.mapper;

import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.dto.property.contact.CreatePropertyContactRequest;
import com.creatix.domain.dto.property.contact.UpdatePropertyContactRequest;
import com.creatix.domain.dto.property.facility.CreatePropertyFacilityRequest;
import com.creatix.domain.dto.property.facility.UpdatePropertyFacilityRequest;
import com.creatix.domain.entity.Contact;
import com.creatix.domain.entity.Facility;
import com.creatix.domain.entity.Property;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Component
public final class PropertyMapper extends ConfigurableMapper {

    protected void configure(MapperFactory mapperFactory) {
        super.configure(mapperFactory);

        mapperFactory.classMap(Property.class, PropertyDetailsDto.class)
                .byDefault()
                .field("address.fullAddress", "address")
                .register();

        //region Contact
        mapperFactory.classMap(Contact.class, PropertyDetailsDto.Contact.class)
                .byDefault()
                .register();
        mapperFactory.classMap(CreatePropertyContactRequest.class, Contact.class)
                .byDefault()
                .register();
        mapperFactory.classMap(UpdatePropertyContactRequest.class, Contact.class)
                .byDefault()
                .register();
        //endregion

        //region Facility
        mapperFactory.classMap(Facility.class, PropertyDetailsDto.Facility.class)
                .byDefault()
                .register();
        mapperFactory.classMap(CreatePropertyFacilityRequest.class, Facility.class)
                .byDefault()
                .register();
        mapperFactory.classMap(UpdatePropertyFacilityRequest.class, Facility.class)
                .byDefault()
                .register();
        //endregion
    }

    public PropertyDetailsDto toPropertyDetailsDto(@NotNull Property property) {
        Objects.requireNonNull(property);

        return this.map(property, PropertyDetailsDto.class);
    }

    //region Contact
    public PropertyDetailsDto.Contact toPropertyContact(@NotNull Contact contact) {
        Objects.requireNonNull(contact);

        return this.map(contact, PropertyDetailsDto.Contact.class);
    }

    public Contact toPropertyContact(@NotNull CreatePropertyContactRequest request) {
        Objects.requireNonNull(request);

        return this.map(request, Contact.class);
    }

    public void fillPropertyContact(@NotNull UpdatePropertyContactRequest request, @NotNull Contact contact) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(contact);

        this.map(request, contact);
    }
    //endregion

    //region Facility
    public PropertyDetailsDto.Facility toPropertyFacility(@NotNull Facility facility) {
        Objects.requireNonNull(facility);

        return this.map(facility, PropertyDetailsDto.Facility.class);
    }

    public Facility toPropertyFacility(@NotNull CreatePropertyFacilityRequest request) {
        Objects.requireNonNull(request);

        return this.map(request, Facility.class);
    }

    public void fillPropertyFacility(@NotNull UpdatePropertyFacilityRequest request, @NotNull Facility facility) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(facility);

        this.map(request, facility);
    }
    //endregion

}
