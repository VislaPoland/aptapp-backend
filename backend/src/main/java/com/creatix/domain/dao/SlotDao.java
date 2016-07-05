package com.creatix.domain.dao;

import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.Slot;
import com.creatix.domain.entity.SlotSchedule;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static com.creatix.domain.entity.QSlot.slot;


@Repository
@Transactional
public class SlotDao extends DaoBase<Slot, Long> {

    public List<Slot> findByProperty(Property property) {
        return queryFactory.selectFrom(slot)
                .where(slot.property.eq(property))
                .fetch();
    }

    public List<Slot> findByPropertyIdAndStartBetween(Long propertyId, OffsetDateTime beginDt, OffsetDateTime endDt) {
        return queryFactory.selectFrom(slot)
                .where(slot.property.id.eq(propertyId)
                        .and(slot.beginTime.between(beginDt, endDt)))
                .fetch();
    }

    public List<Slot> findByScheduleAndStartAfter(SlotSchedule schedule, OffsetDateTime beginDt) {
        return queryFactory.selectFrom(slot)
                .where(slot.schedule.eq(schedule)
                        .and(slot.beginTime.after(beginDt)))
                .fetch();
    }

}
