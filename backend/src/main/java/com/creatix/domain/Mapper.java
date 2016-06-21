package com.creatix.domain;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.dto.ApartmentDto;
import com.creatix.domain.dto.account.AccountDto;
import com.creatix.domain.dto.notification.*;
import com.creatix.domain.dto.property.CreatePropertyRequest;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.entity.*;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                            accountDto.setProperty(toPropertyDetailsDto(property));
                            accountDto.setApartment(toApartmentDto(apartment));
                        } else if (account instanceof PropertyManager) {
                            accountDto.setProperty(toPropertyDetailsDto(((PropertyManager) account).getManagedProperty()));
                        }
                    }
                })
                .register();

        mapperFactory.classMap(Property.class, PropertyDetailsDto.class)
                .byDefault()
                .field("address.fullAddress", "address")
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

        mapperFactory.classMap(MaintenanceNotification.class, MaintenanceNotificationDto.class)
                .byDefault()
                .field("targetApartment.unitNumber", "unitNumber")
                .register();

        mapperFactory.classMap(NeighborhoodNotification.class, NeighborhoodNotificationDto.class)
                .byDefault()
                .field("targetApartment.unitNumber", "unitNumber")
                .register();

        mapperFactory.classMap(CreateNotificationRequest.class, Notification.class)
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

    public Map<Integer, List<NotificationDto>> toNotificationDtoMap(@NotNull Map<Integer, List<Notification>> notifications) {
        Objects.requireNonNull(notifications);
        final Map<Integer, List<NotificationDto>> result = new HashMap<>();
        notifications.forEach((day, nList) -> result.put(day, mapperFactory.getMapperFacade().mapAsList(nList, NotificationDto.class)));
        return result;
    }

    public NotificationDto toNotificationDto(@NotNull Notification notification) {
        Objects.requireNonNull(notification);
        return mapperFactory.getMapperFacade().map(notification, NotificationDto.class);
    }

    public MaintenanceNotificationDto toMaintenanceNotificationDto(@NotNull MaintenanceNotification n) {
        Objects.requireNonNull(n);
        return mapperFactory.getMapperFacade().map(n, MaintenanceNotificationDto.class);
    }

    public NeighborhoodNotificationDto toNeighborhoodNotificationDto(@NotNull NeighborhoodNotification n) {
        Objects.requireNonNull(n);
        return mapperFactory.getMapperFacade().map(n, NeighborhoodNotificationDto.class);
    }

    public Notification fromNotificationDto(@NotNull CreateNotificationRequest dto) {
        Objects.requireNonNull(dto);
        return mapperFactory.getMapperFacade().map(dto, Notification.class);
    }

    public MaintenanceNotification fromMaintenanceNotificationDto(@NotNull CreateMaintenanceNotificationRequest dto) {
        Objects.requireNonNull(dto);
        return mapperFactory.getMapperFacade().map(dto, MaintenanceNotification.class);
    }

    public NeighborhoodNotification fromNeighborhoodNotificationDto(@NotNull CreateNeighborhoodNotificationRequest dto) {
        Objects.requireNonNull(dto);
        return mapperFactory.getMapperFacade().map(dto, NeighborhoodNotification.class);
    }
}
