package com.creatix.domain.dao;

import com.creatix.domain.entity.store.EventInvite;
import com.creatix.domain.entity.store.account.Account;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

import static com.creatix.domain.entity.store.QEventInvite.eventInvite;

@Repository
@Transactional
public class EventInviteDao extends DaoBase<EventInvite, Long> {
	public List<EventInvite> findByEventSlotIdFilterByAttendantNameOrderByAttendantFirstNameAsc(Long slotId, String nameFilter) {
		BooleanExpression predicate = eventInvite.event.id.eq(slotId);

		if ( !StringUtils.isEmpty(nameFilter) ) {
			predicate = predicate.andAnyOf(
			        eventInvite.attendant.firstName.containsIgnoreCase(nameFilter),
                    eventInvite.attendant.lastName.containsIgnoreCase(nameFilter));
		}

		return queryFactory.selectFrom(eventInvite)
				.where(predicate)
				.orderBy(eventInvite.attendant.firstName.asc())
				.fetch();
	}

	public EventInvite findBySlotIdAndAccount(Long slotId, @NotNull Account account) {
        return queryFactory.selectFrom(eventInvite)
                .where(eventInvite.event.id.eq(slotId)
                        .and(eventInvite.attendant.id.eq(account.getId())))
                .fetchOne();
    }

    public boolean isUserInvitedToEvent(Long slotId, @NotNull Account account) {
	    return !Objects.isNull(findBySlotIdAndAccount(slotId, account));
    }
}
