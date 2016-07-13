package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.Slot;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static com.creatix.domain.entity.store.QSlot.slot;

@Repository
@Transactional
public class SlotDao extends DaoBase<Slot, Long> {

    public List<Slot> findByPropertyAndDateRange(Property property, OffsetDateTime from, OffsetDateTime to) {
        return queryFactory.selectFrom(slot)
                .where(slot.property.eq(property).and(slot.beginTime.between(from, to)))
                .orderBy(slot.beginTime.asc())
                .fetch();
    }

    public List<Slot> findByPropertyAndSlotIdGreaterOrEqual(Property property, Long slotId, Integer pageSize) {
        return queryFactory.selectFrom(slot)
                .where(slot.property.eq(property).and(slot.id.goe(slotId)))
                .orderBy(slot.beginTime.asc())
                .limit(pageSize)
                .fetch();
    }

    public List<Slot> findByPropertyAndBeginTime(Property property, OffsetDateTime beginTime, Integer pageSize) {
        return queryFactory.selectFrom(slot)
                .where(slot.property.eq(property).and(slot.beginTime.eq(beginTime).or(slot.beginTime.after(beginTime))))
                .orderBy(slot.beginTime.asc())
                .limit(pageSize)
                .fetch();
    }
}
