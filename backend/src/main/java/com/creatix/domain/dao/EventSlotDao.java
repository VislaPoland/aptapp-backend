package com.creatix.domain.dao;

import com.creatix.domain.entity.store.*;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.AssistantPropertyManager;
import com.creatix.domain.entity.store.account.PropertyManager;
import com.creatix.domain.entity.store.notification.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public EventSlot findById(@Nonnull Long slotId) {
        Objects.requireNonNull(slotId, "SlotId is null");
        return queryFactory.selectFrom(eventSlot)
                .where(eventSlot.id.eq(slotId))
                .fetchOne();
    }


    @Override
    public void delete(EventSlot entity) {

        final Set<NotificationGroup> groups = entity.getInvites().stream()
                .map(i -> i.getNotification().getNotificationGroup())
                .collect(Collectors.toSet());
        final Set<EventInviteNotification> notifications = entity.getInvites().stream()
                .map(EventInvite::getNotification)
                .collect(Collectors.toSet());

        final QEventInviteNotification qNotification = QEventInviteNotification.eventInviteNotification;
        queryFactory.delete(qNotification).where(qNotification.id.in(notifications.stream().mapToLong(Notification::getId).boxed().collect(Collectors.toList()))).execute();

        final QNotificationGroup qNotificationGroup = QNotificationGroup.notificationGroup;
        groups.forEach(group -> queryFactory.delete(qNotificationGroup).where(qNotificationGroup.id.eq(group.getId())));
        queryFactory.delete(qNotificationGroup).where(qNotificationGroup.id.in(groups.stream().mapToLong(NotificationGroup::getId).boxed().collect(Collectors.toList()))).execute();

        final QEventInvite qEventInvite = QEventInvite.eventInvite;
        queryFactory.delete(qEventInvite).where(qEventInvite.id.in(entity.getInvites().stream().mapToLong(EventInvite::getId).boxed().collect(Collectors.toList()))).execute();

        final QSlotUnit qSlotUnit = QSlotUnit.slotUnit;
        queryFactory.delete(qSlotUnit).where(qSlotUnit.id.in(entity.getUnits().stream().mapToLong(SlotUnit::getId).boxed().collect(Collectors.toList()))).execute();

        final QEventSlot qEventSlot = QEventSlot.eventSlot;
        queryFactory.delete(qEventSlot).where(qEventSlot.id.eq(entity.getId())).execute();
    }
}
