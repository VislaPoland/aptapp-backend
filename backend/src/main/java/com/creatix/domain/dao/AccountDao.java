package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.AccountRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.creatix.domain.entity.store.account.QAccount.account;
import static com.creatix.domain.entity.store.account.QMaintenanceEmployee.maintenanceEmployee;
import static com.creatix.domain.entity.store.account.QPropertyManager.propertyManager;
import static com.creatix.domain.entity.store.account.QPropertyOwner.propertyOwner;
import static com.creatix.domain.entity.store.account.QSecurityEmployee.securityEmployee;
import static com.creatix.domain.entity.store.account.QSubTenant.subTenant;
import static com.creatix.domain.entity.store.account.QTenant.tenant;
import static com.creatix.domain.entity.store.account.QAssistantPropertyManager.assistantPropertyManager;

@Repository
@Transactional
public class AccountDao extends DaoBase<Account, Long> {

    public List<Account> findByRolesAndPropertyIdList(@NotNull AccountRole[] roles, Collection<Long> propertyIdList) {
        Objects.requireNonNull(roles);

        final List<Account> accounts;
        if ( (propertyIdList == null) || propertyIdList.isEmpty() ) {
            accounts = queryFactory.selectFrom(account)
                    .where(account.role.in(roles).and(account.deletedAt.isNull()))
                    .orderBy(account.lastName.asc())
                    .orderBy(account.firstName.asc())
                    .fetch();
        }
        else {
            accounts = new ArrayList<>();
            for ( AccountRole role : roles ) {
                if ( role == AccountRole.Tenant ) {
                    accounts.addAll(queryFactory.selectFrom(tenant)
                            .where(tenant.apartment.property.id.in(propertyIdList)
                                    .and(tenant.deletedAt.isNull()))
                            .orderBy(tenant.lastName.asc())
                            .orderBy(tenant.firstName.asc())
                            .fetch());
                }
                else if ( role == AccountRole.Maintenance ) {
                    accounts.addAll(queryFactory.selectFrom(maintenanceEmployee)
                            .where(maintenanceEmployee.manager.managedProperty.id.in(propertyIdList)
                                    .and(maintenanceEmployee.deletedAt.isNull()))
                            .orderBy(maintenanceEmployee.lastName.asc())
                            .orderBy(maintenanceEmployee.firstName.asc())
                            .fetch());
                }
                else if ( role == AccountRole.Security ) {
                    accounts.addAll(queryFactory.selectFrom(securityEmployee)
                            .where(securityEmployee.manager.managedProperty.id.in(propertyIdList)
                                    .and(securityEmployee.deletedAt.isNull()))
                            .orderBy(securityEmployee.lastName.asc())
                            .orderBy(securityEmployee.firstName.asc())
                            .fetch());
                }
                else if ( role == AccountRole.PropertyManager ) {
                    accounts.addAll(queryFactory.selectFrom(propertyManager)
                            .where(propertyManager.managedProperty.id.in(propertyIdList)
                                    .and(propertyManager.deletedAt.isNull()))
                            .orderBy(propertyManager.lastName.asc())
                            .orderBy(propertyManager.firstName.asc())
                            .fetch());
                }
                else if ( role == AccountRole.PropertyOwner ) {
                    accounts.addAll(queryFactory.selectFrom(propertyOwner)
                            .where(propertyOwner.ownedProperties.any().id.in(propertyIdList)
                                    .and(propertyOwner.deletedAt.isNull()))
                            .orderBy(propertyOwner.lastName.asc())
                            .orderBy(propertyOwner.firstName.asc())
                            .fetch());
                }
                else if ( role == AccountRole.AssistantPropertyManager ) {
                    accounts.addAll(queryFactory.selectFrom(assistantPropertyManager)
                            .where(assistantPropertyManager.manager.managedProperty.id.in(propertyIdList)
                                    .and(assistantPropertyManager.deletedAt.isNull()))
                            .orderBy(assistantPropertyManager.lastName.asc())
                            .orderBy(assistantPropertyManager.firstName.asc())
                            .fetch());
                }
                else if ( role == AccountRole.SubTenant ) {
                    accounts.addAll(queryFactory.selectFrom(subTenant)
                            .where(subTenant.parentTenant.apartment.property.id.in(propertyIdList)
                                    .and(subTenant.deletedAt.isNull()))
                            .orderBy(subTenant.lastName.asc())
                            .orderBy(subTenant.firstName.asc())
                            .fetch());
                }
            }
        }

        return accounts;
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
    public void persist(Account account) {
        if ( account.getCreatedAt() == null ) {
            account.setCreatedAt(new Date());
            account.setUpdatedAt(new Date());
        }
        else {
            account.setUpdatedAt(new Date());
        }

        super.persist(account);
    }
}
