package com.creatix.domain.mapper;

import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.dto.property.contact.CreatePropertyContactRequest;
import com.creatix.domain.dto.property.contact.UpdatePropertyContactRequest;
import com.creatix.domain.dto.property.facility.CreatePropertyFacilityRequest;
import com.creatix.domain.dto.property.facility.UpdatePropertyFacilityRequest;
import com.creatix.domain.dto.property.schedule.PropertyScheduleDto;
import com.creatix.domain.entity.*;
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
        mapperFactory.classMap(FacilityDetail.class, PropertyDetailsDto.Facility.Detail.class)
                .byDefault()
                .register();
        mapperFactory.classMap(Facility.class, PropertyDetailsDto.Facility.class)
                .byDefault()
                .register();
        mapperFactory.classMap(CreatePropertyFacilityRequest.Detail.class, FacilityDetail.class)
                .byDefault()
                .register();
        mapperFactory.classMap(CreatePropertyFacilityRequest.class, Facility.class)
                .byDefault()
                .customize(
                        new CustomMapper<CreatePropertyFacilityRequest, Facility>() {
                            @Override
                            public void mapAtoB(CreatePropertyFacilityRequest createPropertyFacilityRequest, Facility facility, MappingContext context) {
                                super.mapAtoB(createPropertyFacilityRequest, facility, context);
                                if (facility.getDetails() != null) {
                                    for (FacilityDetail facilityDetail : facility.getDetails()) {
                                        facilityDetail.setFacility(facility);
                                    }
                                }
                            }
                        }
                )
                .register();
        mapperFactory.classMap(UpdatePropertyFacilityRequest.Detail.class, FacilityDetail.class)
                .byDefault()
                .register();
        mapperFactory.classMap(UpdatePropertyFacilityRequest.class, Facility.class)
                .byDefault()
                .customize(
                        new CustomMapper<UpdatePropertyFacilityRequest, Facility>() {
                            @Override
                            public void mapAtoB(UpdatePropertyFacilityRequest updatePropertyFacilityRequest, Facility facility, MappingContext context) {
                                super.mapAtoB(updatePropertyFacilityRequest, facility, context);
                                if (facility.getDetails() != null) {
                                    for (FacilityDetail facilityDetail : facility.getDetails()) {
                                        facilityDetail.setFacility(facility);
                                    }
                                }
                            }
                        }
                )
                .register();
        //endregion

        //region Schedule
        mapperFactory.classMap(PropertyScheduleDto.class, PropertySchedule.class)
                .byDefault()
                .register();
        mapperFactory.classMap(PropertySchedule.class, PropertyScheduleDto.class)
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

    //region Schedule
    public PropertyScheduleDto toPropertyScheduleDto(@NotNull PropertySchedule schedule) {
        Objects.requireNonNull(schedule);

        return this.map(schedule, PropertyScheduleDto.class);
    }

    public PropertySchedule toPropertySchedule(@NotNull PropertyScheduleDto request) {
        Objects.requireNonNull(request);

        return this.map(request, PropertySchedule.class);
    }

    public void fillPropertySchedule(@NotNull PropertyScheduleDto request, @NotNull PropertySchedule schedule) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(schedule);

        this.map(request, schedule);
    }
    //endregion
}
