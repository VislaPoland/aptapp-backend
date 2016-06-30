package com.creatix.domain.dao;

import com.creatix.domain.entity.account.*;
import com.creatix.domain.enums.AccountRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class AccountDao extends DaoBase<Account, Long> {

    public List<Account> findByRolesAndPropertyId(@NotNull AccountRole[] roles, Long propertyId) {
        Objects.requireNonNull(roles);

        final List<Account> accounts;
        if ( propertyId == null ) {
            accounts = queryFactory.selectFrom(QAccount.account)
                    .where(QAccount.account.role.in(roles))
                    .fetch();
        }
        else {
            accounts = new ArrayList<>();
            for ( AccountRole role : roles ) {
                if ( role == AccountRole.Tenant ) {
                    accounts.addAll(queryFactory.selectFrom(QTenant.tenant)
                            .where(QTenant.tenant.apartment.property.id.eq(propertyId))
                            .fetch());
                }
                else if ( EnumSet.of(AccountRole.Maintenance, AccountRole.Security).contains(role) ) {
                    accounts.addAll(queryFactory.selectFrom(QEmployee.employee)
                            .where(QEmployee.employee.manager.managedProperty.id.eq(propertyId))
                            .fetch());
                }
                else if ( role == AccountRole.PropertyManager ) {
                    accounts.addAll(queryFactory.selectFrom(QPropertyManager.propertyManager)
                            .where(QPropertyManager.propertyManager.managedProperty.id.eq(propertyId))
                            .fetch());
                }
                else if ( role == AccountRole.PropertyOwner ) {
                    accounts.addAll(queryFactory.selectFrom(QPropertyOwner.propertyOwner)
                            .where(QPropertyOwner.propertyOwner.ownedProperties.any().id.eq(propertyId))
                            .fetch());
                }
                else if ( role == AccountRole.SubTenant ) {
                    accounts.addAll(queryFactory.selectFrom(QSubTenant.subTenant)
                            .where(QSubTenant.subTenant.parentTenant.apartment.property.id.eq(propertyId))
                            .fetch());
                }
            }
        }

        return accounts;
    }

    public List<Account> findAll() {
        return queryFactory
                .selectFrom(QAccount.account)
                .where(QAccount.account.deletedAt.isNull())
                .fetch();
    }

    /**
     * Find account by primaryEmail address. This method will return even deleted accounts
     * to prevent primaryEmail name clash and user spoofing.
     *
     * @param email unique primaryEmail address to find account by
     * @return found account
     */
    public Account findByEmail(String email) {
        QAccount account = QAccount.account;
        return queryFactory.selectFrom(account)
                .where(account.primaryEmail.eq(email))
                .fetchOne();
    }

    public Account findByActionToken(String actionToken) {
        QAccount account = QAccount.account;
        return queryFactory.selectFrom(account)
                .where(account.actionToken.eq(actionToken))
                .fetchOne();
    }
}
