package com.creatix.domain.dao;

import com.creatix.domain.entity.store.EventSlot;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.AssistantPropertyManager;
import com.creatix.domain.entity.store.account.PropertyManager;
import com.creatix.domain.enums.AudienceType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static com.creatix.domain.entity.store.QEventSlot.eventSlot;

@Repository
@Transactional
public class EventSlotDao extends DaoBase<EventSlot, Long> {
    public List<EventSlot> findByPropertyIdAndAccountAndStartBetween(Long propertyId, Account account, OffsetDateTime beginDt, OffsetDateTime endDt) {
        BooleanExpression predicate = eventSlot.property.id.eq(propertyId).and(eventSlot.beginTime.between(beginDt, endDt));

        if ( !(account instanceof PropertyManager) && !(account instanceof AssistantPropertyManager) ) {
            predicate = predicate.and(eventSlot.invites.any().attendant.id.eq(account.getId()));
        }

        return queryFactory.selectFrom(eventSlot)
                .where(predicate)
                .fetch();
    }

    public List<EventSlot> findActiveEventsForTenant(Long propertyId) {
        BooleanExpression predicate = eventSlot.property.id.eq(propertyId).and(eventSlot.endTime.before(OffsetDateTime.now()));
        predicate = predicate.and(eventSlot.audience.eq(AudienceType.Tenants));

        return queryFactory.selectFrom(eventSlot)
                .where(predicate)
                .fetch();
    }

}
