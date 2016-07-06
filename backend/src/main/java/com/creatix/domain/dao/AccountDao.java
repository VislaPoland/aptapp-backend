package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.AccountRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.creatix.domain.entity.store.account.QEmployee.employee;
import static com.creatix.domain.entity.store.account.QTenant.tenant;
import static com.creatix.domain.entity.store.account.QPropertyManager.propertyManager;
import static com.creatix.domain.entity.store.account.QPropertyOwner.propertyOwner;
import static com.creatix.domain.entity.store.account.QSubTenant.subTenant;
import static com.creatix.domain.entity.store.account.QAccount.account;


@Repository
@Transactional
public class AccountDao extends DaoBase<Account, Long> {

    public List<Account> findByRolesAndPropertyId(@NotNull AccountRole[] roles, Long propertyId) {
        Objects.requireNonNull(roles);

        final List<Account> accounts;
        if ( propertyId == null ) {
            accounts = queryFactory.selectFrom(account)
                    .where(account.role.in(roles).and(account.deletedAt.isNull()))
                    .fetch();
        }
        else {
            accounts = new ArrayList<>();
            for ( AccountRole role : roles ) {
                if ( role == AccountRole.Tenant ) {
                    accounts.addAll(queryFactory.selectFrom(tenant)
                            .where(tenant.apartment.property.id.eq(propertyId)
                                    .and(tenant.deletedAt.isNull()))
                            .fetch());
                }
                else if ( role == AccountRole.Maintenance ) {
                    accounts.addAll(queryFactory.selectFrom(employee)
                            .where(employee.manager.managedProperty.id.eq(propertyId)
                                    .and(employee.role.eq(AccountRole.Security))
                                    .and(employee.deletedAt.isNull()))
                            .fetch());
                }
                else if ( role == AccountRole.Security ) {
                    accounts.addAll(queryFactory.selectFrom(employee)
                            .where(employee.manager.managedProperty.id.eq(propertyId)
                                    .and(employee.role.eq(AccountRole.Security))
                                    .and(employee.deletedAt.isNull()))
                            .fetch());
                }
                else if ( role == AccountRole.PropertyManager ) {
                    accounts.addAll(queryFactory.selectFrom(propertyManager)
                            .where(propertyManager.managedProperty.id.eq(propertyId)
                                    .and(propertyManager.deletedAt.isNull()))
                            .fetch());
                }
                else if ( role == AccountRole.PropertyOwner ) {
                    accounts.addAll(queryFactory.selectFrom(propertyOwner)
                            .where(propertyOwner.ownedProperties.any().id.eq(propertyId)
                                    .and(propertyOwner.deletedAt.isNull()))
                            .fetch());
                }
                else if ( role == AccountRole.SubTenant ) {
                    accounts.addAll(queryFactory.selectFrom(subTenant)
                            .where(subTenant.parentTenant.apartment.property.id.eq(propertyId)
                                    .and(subTenant.deletedAt.isNull()))
                            .fetch());
                }
            }
        }

        return accounts;
    }

    public List<Account> findAll() {
        return queryFactory
                .selectFrom(account)
                .where(account.deletedAt.isNull())
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
        return queryFactory.selectFrom(account)
                .where(account.primaryEmail.eq(email))
                .fetchOne();
    }

    public Account findByActionToken(String actionToken) {
        return queryFactory.selectFrom(account)
                .where(account.actionToken.eq(actionToken))
                .fetchOne();
    }

    @Override
    public void persist(Account notification) {
        if ( notification.getCreatedAt() == null ) {
            notification.setCreatedAt(new Date());
            notification.setUpdatedAt(new Date());
        }
        else {
            notification.setUpdatedAt(new Date());
        }

        super.persist(notification);
    }
}
