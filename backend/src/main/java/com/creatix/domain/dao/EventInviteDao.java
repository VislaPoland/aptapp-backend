package com.creatix.domain.dao;

import com.creatix.domain.entity.store.EventInvite;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.EventInviteResponse;
import com.querydsl.sql.SQLExpressions;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.creatix.domain.entity.store.QEventInvite.eventInvite;

@Repository
@Transactional
public class EventInviteDao extends DaoBase<EventInvite, Long> {

    @Nonnull
    public List<EventInvite> findByEventSlotIdOrderByAttendantFirstNameAsc(@Nonnull Long slotId) {
		return queryFactory.selectFrom(eventInvite)
				.where(eventInvite.event.id.eq(slotId))
				.orderBy(eventInvite.attendant.firstName.asc())
				.fetch();
	}

	@Nullable
	public EventInvite findBySlotIdAndAccount(@Nonnull Long slotId, @Nonnull Account account) {
        return queryFactory.selectFrom(eventInvite)
                .where(eventInvite.event.id.eq(slotId)
                        .and(eventInvite.attendant.id.eq(account.getId())))
                .fetchOne();
    }

    @Nonnull
    public List<EventInvite> findBySlotDateAndInviteResponseAndRemindedAtNull(@Nonnull OffsetDateTime beginDate, @Nonnull EventInviteResponse[] responses) {
		return queryFactory.selectFrom(eventInvite)
				.where(
				    SQLExpressions.date(eventInvite.event.beginTime).eq(beginDate)
                    .and(eventInvite.response.in(responses))
                    .and(eventInvite.remindedAt.isNull()))
                .fetch();
	}

    public boolean isUserInvitedToEvent(@Nonnull Long slotId, @Nonnull Account account) {
	    return !Objects.isNull(findBySlotIdAndAccount(slotId, account));
    }
}
