package com.creatix.service.property;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.dao.MaintenanceNotificationDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dto.property.schedule.PropertyScheduleDto;
import com.creatix.domain.dto.property.schedule.ScheduleSlotsListingDto;
import com.creatix.domain.dto.property.schedule.ScheduleSlotsListingDto.ScheduleSlot;
import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.PropertySchedule;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.PropertyMapper;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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

    private void validatePropertySchedule(PropertyScheduleDto request) {
        LocalDateTime time1 = LocalDateTime.now().withHour(request.getStartHour()).withMinute(request.getStartMinute());
        LocalDateTime time2 = LocalDateTime.now().withHour(request.getEndHour()).withMinute(request.getEndMinute());
        long workTimeInMinutes = Duration.between(time1, time2).toMinutes();

        if (workTimeInMinutes % request.getPeriodLength() != 0) {
            throw new IllegalArgumentException("Period length needs to be a divisor of the working time in minutes");
        }
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public PropertySchedule createPropertyScheduleFromRequest(Long propertyId, @NotNull PropertyScheduleDto request) {
        Objects.requireNonNull(request);
        validatePropertySchedule(request);

        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property id=%d not found", propertyId)));
        final PropertySchedule schedule = propertyMapper.toPropertySchedule(request);
        property.setSchedule(schedule);
        propertyDao.persist(property);
        return property.getSchedule();
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager})
    public PropertySchedule updatePropertyScheduleFromRequest(Long propertyId, @NotNull PropertyScheduleDto request) {
        Objects.requireNonNull(request);
        validatePropertySchedule(request);

        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property id=%d not found", propertyId)));
        propertyMapper.fillPropertySchedule(request, property.getSchedule());
        propertyDao.persist(property);
        return property.getSchedule();
    }

    @RoleSecured
    public ScheduleSlotsListingDto getScheduleSlotListing(Long propertyId, Date day) {
        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property id=%d not found", propertyId)));
        final PropertySchedule schedule = property.getSchedule();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(property.getTimeZone()));
        calendar.setTime(day);

        calendar.set(Calendar.HOUR_OF_DAY, schedule.getStartHour());
        calendar.set(Calendar.MINUTE, schedule.getStartMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfTheWorkDay = calendar.getTime();
        long startOfTheWorkDayInMilliseconds = startOfTheWorkDay.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, schedule.getEndHour());
        calendar.set(Calendar.MINUTE, schedule.getEndMinute());
        Date endOfTheWorkDay = calendar.getTime();

        long workDayInMilliseconds = endOfTheWorkDay.getTime() - startOfTheWorkDay.getTime();
        long periodLengthInMilliseconds = schedule.getPeriodLength() * 60 * 1000;
        long periodsPerDay = (workDayInMilliseconds / periodLengthInMilliseconds);

        List<ScheduleSlot> slots = new ArrayList<>((int) periodsPerDay);
        for (long i = 0; i < periodsPerDay; i++) {
            long slotStartInMillis = startOfTheWorkDayInMilliseconds + i * periodLengthInMilliseconds;
            Date slotStart = new Date(slotStartInMillis);
            long slotEndInMillis = slotStartInMillis + periodLengthInMilliseconds - 1000;   //subtract 1000 ms (1 s) because of inclusive date params
            Date slotEnd = new Date(slotEndInMillis);

            ScheduleSlot slot = new ScheduleSlot();
            slot.setSlotStart(slotStart);
            slot.setFree(maintenanceNotificationDao.doesDateRangeContainMoreThanNumberWithinProperty(slotStart, slotEnd, schedule.getSlotsPerPeriod(), property));
            slots.add(slot);
        }

        ScheduleSlotsListingDto result = new ScheduleSlotsListingDto();
        result.setSlots(slots);
        return result;
    }
}
