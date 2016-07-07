package com.creatix.domain.dao;

import com.creatix.domain.dto.notification.NotificationRequestType;
import com.creatix.domain.entity.store.notification.QSecurityNotification;
import com.creatix.domain.entity.store.notification.SecurityNotification;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.AssistantPropertyManager;
import com.creatix.domain.entity.store.account.PropertyManager;
import com.creatix.domain.entity.store.account.SecurityEmployee;
import com.creatix.domain.enums.NotificationStatus;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SecurityNotificationDao extends AbstractNotificationDao<SecurityNotification> {
    private Predicate filtersPredicate(final QSecurityNotification securityNotification, NotificationStatus status,
                                       NotificationRequestType type, Account a) {
        final BooleanExpression predicate = securityNotification.status.eq(status);
        switch (type) {
            case Sent:
                return predicate.and(securityNotification.author.eq(a));
            case Received:
                switch (a.getRole()) {
                    case PropertyManager:
                        return predicate.and(securityNotification.property.eq(((PropertyManager) a).getManagedProperty()));
                    case AssistantPropertyManager:
                        return predicate.and(securityNotification.property.eq(((AssistantPropertyManager) a).getManager().getManagedProperty()));
                    case Security:
                        return predicate.and(securityNotification.property.eq(((SecurityEmployee) a).getManager().getManagedProperty()));
                    default:
                        throw new IllegalStateException(String.format("Account type %s is not allowed to read security notifications", a.getRole().toString()));
                }
            default:
                throw new IllegalStateException();
        }
    }

    public long countByStatusAndType(NotificationStatus status, NotificationRequestType type, Account account) {
        final QSecurityNotification securityNotification = QSecurityNotification.securityNotification;

        return queryFactory.selectFrom(securityNotification)
                .where(filtersPredicate(securityNotification, status, type, account))
                .fetchCount();
    }

    public List<SecurityNotification> findPageByStatusAndType(NotificationStatus status, NotificationRequestType type, Account account, long pageNumber, long pageSize) {
        final QSecurityNotification securityNotification = QSecurityNotification.securityNotification;

        return queryFactory.selectFrom(securityNotification)
                .where(filtersPredicate(securityNotification, status, type, account))
                .orderBy(securityNotification.createdAt.desc())
                .limit(pageNumber * pageSize + pageSize)
                .offset(pageNumber * pageSize)
                .fetch();
    }
}
