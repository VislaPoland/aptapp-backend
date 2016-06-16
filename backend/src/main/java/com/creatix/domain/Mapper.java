package com.creatix.domain;

import com.creatix.domain.dto.account.AccountDto;
import com.creatix.domain.dto.notification.MaintenanceNotificationDto;
import com.creatix.domain.dto.notification.NeighborhoodNotificationDto;
import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.entity.*;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        final Map<Integer, List<NotificationDto>> result = new HashMap<>();
        notifications.forEach((day, nList) -> result.put(day, mapperFactory.getMapperFacade().mapAsList(nList, NotificationDto.class)));
        return result;
    }

    public NotificationDto toNotificationDto(@NotNull Notification notification) {
        return mapperFactory.getMapperFacade().map(notification, NotificationDto.class);
    }

    public MaintenanceNotificationDto toMaintenanceNotificationDto(@NotNull MaintenanceNotification n) {
        return mapperFactory.getMapperFacade().map(n, MaintenanceNotificationDto.class);
    }

    public NeighborhoodNotificationDto toNeighborhoodNotificationDto(@NotNull NeighborhoodNotification n) {
        return mapperFactory.getMapperFacade().map(n, NeighborhoodNotificationDto.class);
    }
}
