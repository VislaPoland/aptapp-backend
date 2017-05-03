package com.creatix.domain.mapper;

import com.creatix.domain.dto.property.BasicAccountDto;
import com.creatix.domain.dto.property.PropertyDto;
import com.creatix.domain.dto.property.contact.CreatePropertyContactRequest;
import com.creatix.domain.dto.property.contact.UpdatePropertyContactRequest;
import com.creatix.domain.dto.property.facility.CreatePropertyFacilityRequest;
import com.creatix.domain.dto.property.facility.UpdatePropertyFacilityRequest;
import com.creatix.domain.dto.property.slot.MaintenanceSlotScheduleDto;
import com.creatix.domain.entity.store.*;
import com.creatix.domain.entity.store.account.Account;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Objects;

@Component
public final class PropertyMapper extends ConfigurableMapper {

    protected void configure(MapperFactory mapperFactory) {
        super.configure(mapperFactory);

        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(OffsetDateTime.class, OffsetDateTime.class));
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalTime.class, LocalTime.class));

        mapperFactory.classMap(Property.class, PropertyDto.class)
                .byDefault()
                .field("address.fullAddress", "address")
                .register();

        //region Account
        mapperFactory.classMap(Account.class, BasicAccountDto.class)
                .byDefault()
                .register();
        //endregion

        //region Contact
        mapperFactory.classMap(Contact.class, PropertyDto.ContactDto.class)
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
        mapperFactory.classMap(Facility.class, PropertyDto.FacilityDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(CreatePropertyFacilityRequest.class, Facility.class)
                .byDefault()
                .register();
        mapperFactory.classMap(UpdatePropertyFacilityRequest.class, Facility.class)
                .byDefault()
                .register();
        //endregion

        //region Schedule
        mapperFactory.classMap(MaintenanceSlotScheduleDto.class, MaintenanceSlotSchedule.class)
                .byDefault()
                .register();
        mapperFactory.classMap(MaintenanceSlotSchedule.class, MaintenanceSlotScheduleDto.class)
                .byDefault()
                .register();
        //endregion
    }

    public PropertyDto toPropertyDetailsDto(@NotNull Property property) {
        Objects.requireNonNull(property);

        return this.map(property, PropertyDto.class);
    }

    //region Account
    public BasicAccountDto toBasicAccount(@NotNull Account account) {
        Objects.requireNonNull(account);

        return this.map(account, BasicAccountDto.class);
    }
    //endregion

    //region Contact
    public PropertyDto.ContactDto toPropertyContact(@NotNull Contact contact) {
        Objects.requireNonNull(contact);

        return this.map(contact, PropertyDto.ContactDto.class);
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
    public PropertyDto.FacilityDto toPropertyFacility(@NotNull Facility facility) {
        Objects.requireNonNull(facility);

        return this.map(facility, PropertyDto.FacilityDto.class);
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
    public MaintenanceSlotScheduleDto toMaintenanceSlotScheduleDto(@NotNull MaintenanceSlotSchedule schedule) {
        Objects.requireNonNull(schedule);

        return this.map(schedule, MaintenanceSlotScheduleDto.class);
    }

    public MaintenanceSlotSchedule toMaintenanceSlotSchedule(@NotNull MaintenanceSlotScheduleDto request) {
        Objects.requireNonNull(request);
        return this.map(request, MaintenanceSlotSchedule.class);
    }

    public void fillMaintenanceSlotSchedule(@NotNull MaintenanceSlotScheduleDto request, @NotNull MaintenanceSlotSchedule schedule) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(schedule);

        this.map(request, schedule);
    }
    //endregion
}
