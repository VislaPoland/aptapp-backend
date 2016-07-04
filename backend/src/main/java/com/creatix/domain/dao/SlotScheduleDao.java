package com.creatix.domain.dao;

import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.SlotSchedule;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.creatix.domain.entity.QSlotSchedule.slotSchedule;

@Repository
@Transactional
public class SlotScheduleDao extends DaoBase<SlotSchedule, Long> {

    public List<SlotSchedule> findAll() {
        return queryFactory.selectFrom(slotSchedule).fetch();
    }

    public List<SlotSchedule> findByProperty(Property property) {
        return queryFactory.selectFrom(slotSchedule)
                .where(slotSchedule.property.eq(property))
                .fetch();
    }

}
