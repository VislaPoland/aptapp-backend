package com.creatix.domain;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.dto.ApartmentDto;
import com.creatix.domain.dto.account.AccountDto;
import com.creatix.domain.dto.notification.*;
import com.creatix.domain.dto.property.CreatePropertyRequest;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.dto.property.UpdatePropertyRequest;
import com.creatix.domain.dto.tenant.CreateTenantRequest;
import com.creatix.domain.dto.tenant.UpdateTenantRequest;
import com.creatix.domain.entity.*;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Component
public final class Mapper {
    private MapperFactory mapperFactory;

    public Mapper() {
        mapperFactory = new DefaultMapperFactory.Builder().build();
        configure(mapperFactory);
    }

    private void configure(MapperFactory mapperFactory) {
        mapperFactory.classMap(Account.class, AccountDto.class)
                .byDefault()
                .customize(new CustomMapper<Account, AccountDto>() {
                    @Override
                    public void mapAtoB(Account account, AccountDto accountDto, MappingContext context) {
                        if (account instanceof Tenant) {
                            final Apartment apartment = ((Tenant) account).getApartment();
                            final Property property = apartment.getProperty();
                            PropertyDetailsDto details = toPropertyDetailsDto(property);
                            accountDto.setProperty(details);
                            accountDto.setApartment(toApartmentDto(apartment));
                        } else if (account instanceof PropertyManager) {
                            final Property managedProperty = ((PropertyManager) account).getManagedProperty();
                            if ( managedProperty != null ) {
                                accountDto.setProperty(toPropertyDetailsDto(managedProperty));
                            }
                        }
                    }
                })
                .register();

        mapperFactory.classMap(Property.class, PropertyDetailsDto.class)
                .byDefault()
                .field("address.fullAddress", "address")
                .register();

        mapperFactory.classMap(PropertySchedule.class, PropertyDetailsDto.Schedule.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Facility.class, PropertyDetailsDto.Facility.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Contact.class, PropertyDetailsDto.Contact.class)
                .byDefault()
                .register();

        mapperFactory.classMap(FacilityDetail.class, PropertyDetailsDto.Facility.Detail.class)
                .byDefault()
                .register();

        mapperFactory.classMap(PropertyOwner.class, PropertyDetailsDto.Owner.class)
                .field("primaryEmail", "email")
                .field("fullName", "name")
                .field("website", "web")
                .field("primaryPhone", "phone")
                .register();

        mapperFactory.classMap(Notification.class, NotificationDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(SecurityNotification.class, SecurityNotificationDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(MaintenanceNotification.class, MaintenanceNotificationDto.class)
                .byDefault()
                .field("targetApartment.id", "apartmentId")
                .register();

        mapperFactory.classMap(NotificationPhoto.class, NotificationPhotoDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(NeighborhoodNotification.class, NeighborhoodNotificationDto.class)
                .byDefault()
                .field("targetApartment.id", "apartmentId")
                .register();

        mapperFactory.classMap(CreateSecurityNotificationRequest.class, SecurityNotification.class)
                .byDefault()
                .register();

        mapperFactory.classMap(CreateMaintenanceNotificationRequest.class, MaintenanceNotification.class)
                .byDefault()
                .register();

        mapperFactory.classMap(CreateNeighborhoodNotificationRequest.class, NeighborhoodNotification.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Apartment.class, ApartmentDto.NeighborApartment.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Apartment.class, ApartmentDto.Neighbors.class)
                .field("aboveApartment", "above")
                .field("belowApartment", "below")
                .field("leftApartment", "left")
                .field("rightApartment", "right")
                .field("oppositeApartment", "opposite")
                .field("behindApartment", "behind")
                .register();

        mapperFactory.classMap(Apartment.class, ApartmentDto.class)
                .byDefault()
                .field("tenant.fullName", "fullName")
                .field("tenant.primaryEmail", "primaryEmail")
                .field("property.id", "propertyId")
                .customize(new CustomMapper<Apartment, ApartmentDto>() {
                    @Override
                    public void mapAtoB(Apartment apartment, ApartmentDto apartmentDto, MappingContext context) {
                        apartmentDto.setNeighbors(mapperFactory.getMapperFacade().map(apartment, ApartmentDto.Neighbors.class));
                    }
                })
                .register();

        mapperFactory.classMap(AddressDto.class, Address.class)
                .byDefault()
                .register();

        mapperFactory.classMap(CreatePropertyRequest.class, Property.class)
                .byDefault()
                .register();
        mapperFactory.classMap(UpdatePropertyRequest.class, Property.class)
                .byDefault()
                .register();

        mapperFactory.classMap(CreateTenantRequest.class, Tenant.class)
                .byDefault()
                .register();

    }

    public Tenant toTenant(@NotNull CreateTenantRequest request) {
        Objects.requireNonNull(request);
        return mapperFactory.getMapperFacade().map(request, Tenant.class);
    }

    public void fillTenant(@NotNull UpdateTenantRequest dto, @NotNull Tenant entity) {
        Objects.requireNonNull(dto);
        Objects.requireNonNull(entity);

        mapperFactory.getMapperFacade().map(dto, entity);
    }

    public void fillProperty(@NotNull UpdatePropertyRequest dto, @NotNull Property entity) {
        Objects.requireNonNull(dto);
        Objects.requireNonNull(entity);

        mapperFactory.getMapperFacade().map(dto, entity);
    }

    public Property toProperty(@NotNull CreatePropertyRequest request) {
        Objects.requireNonNull(request);
        return mapperFactory.getMapperFacade().map(request, Property.class);
    }

    public ApartmentDto toApartmentDto(@NotNull Apartment apartment) {
        Objects.requireNonNull(apartment);
        return mapperFactory.getMapperFacade().map(apartment, ApartmentDto.class);
    }

    public AccountDto toAccountDto(Account account) {
        Objects.requireNonNull(account);
        return mapperFactory.getMapperFacade().map(account, AccountDto.class);
    }

    public PropertyDetailsDto toPropertyDetailsDto(@NotNull Property property) {
        Objects.requireNonNull(property);
        return mapperFactory.getMapperFacade().map(property, PropertyDetailsDto.class);
    }

    public NotificationDto toNotificationDto(@NotNull Notification n) {
        Objects.requireNonNull(n);

        return mapperFactory.getMapperFacade().map(n, NotificationDto.class);
    }

    public SecurityNotificationDto toSecurityNotificationDto(@NotNull SecurityNotification n) {
        Objects.requireNonNull(n);
        return mapperFactory.getMapperFacade().map(n, SecurityNotificationDto.class);
    }

    public MaintenanceNotificationDto toMaintenanceNotificationDto(@NotNull MaintenanceNotification n) {
        Objects.requireNonNull(n);
        return mapperFactory.getMapperFacade().map(n, MaintenanceNotificationDto.class);
    }

    public NeighborhoodNotificationDto toNeighborhoodNotificationDto(@NotNull NeighborhoodNotification n) {
        Objects.requireNonNull(n);
        return mapperFactory.getMapperFacade().map(n, NeighborhoodNotificationDto.class);
    }

    public SecurityNotification fromSecurityNotificationRequest(@NotNull CreateSecurityNotificationRequest dto) {
        Objects.requireNonNull(dto);
        return mapperFactory.getMapperFacade().map(dto, SecurityNotification.class);
    }

    public MaintenanceNotification fromMaintenanceNotificationRequest(@NotNull CreateMaintenanceNotificationRequest dto) {
        Objects.requireNonNull(dto);
        return mapperFactory.getMapperFacade().map(dto, MaintenanceNotification.class);
    }

    public NeighborhoodNotification fromNeighborhoodNotificationRequest(@NotNull CreateNeighborhoodNotificationRequest dto) {
        Objects.requireNonNull(dto);
        return mapperFactory.getMapperFacade().map(dto, NeighborhoodNotification.class);
    }
}
