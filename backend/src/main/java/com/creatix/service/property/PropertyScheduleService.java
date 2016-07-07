package com.creatix.service.property;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.dao.MaintenanceNotificationDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dto.property.slot.MaintenanceSlotScheduleDto;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.PropertySchedule;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.PropertyMapper;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Service
@Transactional
public class PropertyScheduleService {
    @Autowired
    private PropertyMapper propertyMapper;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private MaintenanceNotificationDao maintenanceNotificationDao;

    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if (item == null) {
            throw ex;
        }
        return item;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public PropertySchedule createPropertyScheduleFromRequest(Long propertyId, @NotNull MaintenanceSlotScheduleDto request) {
        Objects.requireNonNull(request);

        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property id=%d not found", propertyId)));
        final PropertySchedule schedule = propertyMapper.toPropertySchedule(request);
        property.setSchedule(schedule);
        propertyDao.persist(property);
        return property.getSchedule();
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public PropertySchedule updatePropertyScheduleFromRequest(Long propertyId, @NotNull MaintenanceSlotScheduleDto request) {
        Objects.requireNonNull(request);

        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property id=%d not found", propertyId)));
        propertyMapper.fillPropertySchedule(request, property.getSchedule());
        propertyDao.persist(property);
        return property.getSchedule();
    }

}
