package com.creatix.domain.dao;

import com.creatix.domain.entity.EventSlot;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static com.creatix.domain.entity.QEventSlot.eventSlot;


@Repository
@Transactional
public class EventSlotDao extends DaoBase<EventSlot, Long> {

    public List<EventSlot> findByPropertyIdAndStartBetween(Long propertyId, OffsetDateTime beginDt, OffsetDateTime endDt) {
        return queryFactory.selectFrom(eventSlot)
                .where(eventSlot.property.id.eq(propertyId)
                        .and(eventSlot.beginTime.between(beginDt, endDt)))
                .fetch();
    }

}
