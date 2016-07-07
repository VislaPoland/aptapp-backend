package com.creatix.domain.dao;

import com.creatix.domain.entity.store.MaintenanceSlot;
import com.creatix.domain.entity.store.MaintenanceSlotSchedule;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static com.creatix.domain.entity.store.QMaintenanceSlot.maintenanceSlot;


@Repository
@Transactional
public class MaintenanceSlotDao extends DaoBase<MaintenanceSlot, Long> {

    public List<MaintenanceSlot> findByPropertyIdAndStartBetween(Long propertyId, OffsetDateTime beginDt, OffsetDateTime endDt) {
        return queryFactory.selectFrom(maintenanceSlot)
                .where(maintenanceSlot.property.id.eq(propertyId)
                        .and(maintenanceSlot.beginTime.between(beginDt, endDt)))
                .fetch();
    }

    public List<MaintenanceSlot> findByScheduleAndStartAfter(MaintenanceSlotSchedule schedule, OffsetDateTime beginDt) {
        return queryFactory.selectFrom(maintenanceSlot)
                .where(maintenanceSlot.schedule.eq(schedule)
                        .and(maintenanceSlot.beginTime.after(beginDt)))
                .fetch();
    }

}
