package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.QProperty;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.QAccount;
import com.creatix.domain.enums.AccountRole;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.BooleanBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.creatix.domain.entity.store.account.QAccount.account;
import static com.creatix.domain.entity.store.account.QAssistantPropertyManager.assistantPropertyManager;
import static com.creatix.domain.entity.store.account.QMaintenanceEmployee.maintenanceEmployee;
import static com.creatix.domain.entity.store.account.QPropertyManager.propertyManager;
import static com.creatix.domain.entity.store.account.QPropertyOwner.propertyOwner;
import static com.creatix.domain.entity.store.account.QSecurityEmployee.securityEmployee;
import static com.creatix.domain.entity.store.account.QSubTenant.subTenant;
import static com.creatix.domain.entity.store.account.QTenant.tenant;

@Repository
@Transactional
public class AccountDao extends DaoBase<Account, Long> {

    public List<Account> findByRolesAndPropertyIdList(@NotNull AccountRole[] roles, Collection<Long> propertyIdList, String keywords, String sortColumn, String sortOrder) {
        Objects.requireNonNull(roles, "Account roles array is null");

        List<Account> accounts;
        List<Account> accountsFiltered = null;
        OrderSpecifier orderSpecifier;
        OrderSpecifier orderSpecifierSecond = null;
        String keywordsLowercase = null; 
        BooleanExpression predicate = null;
        boolean keywordsContainNumber = false;	
        
        if (keywords != null && keywords.length()>0){
        	keywordsLowercase = keywords.toLowerCase().trim();	    	
	    	Pattern p = Pattern.compile("[0-9]");
	    	Matcher m = p.matcher( keywordsLowercase );
	    	keywordsContainNumber = m.find();
        }
        
        if (keywordsLowercase != null){
	        predicate = account.firstName.toLowerCase().contains(keywordsLowercase)
	        	.or(account.lastName.toLowerCase().contains(keywordsLowercase))
	        	.or ((account.firstName.concat(" ").concat(account.lastName)).toLowerCase().contains(keywordsLowercase))
	        	.or(account.primaryEmail.toLowerCase().contains(keywordsLowercase));
	    	if (keywordsLowercase.contains("inactive")){
	    		predicate = predicate.or(account.active.isFalse());
	    	}else if (keywordsLowercase.contains("active")){
	    		predicate = predicate.or(account.active.isTrue());
	    	}

	    	switch (keywordsLowercase){
	    	 	case "administrator":
	    	 		predicate = predicate.or(account.role.eq(AccountRole.Administrator));
	    	 		break;
	    	 	case "propertyowner":
	    	 		predicate = predicate.or(account.role.eq(AccountRole.PropertyOwner));
	    	 		break;
	    	 	case "propertymanager":
	    	 		predicate = predicate.or(account.role.eq(AccountRole.PropertyManager));
	    	 		break;
	    	 	case "assistantpropertymanager":
	    	 		predicate = predicate.or(account.role.eq(AccountRole.AssistantPropertyManager));
	    	 		break;
	    	 	case "maintenance":
	    	 		predicate = predicate.or(account.role.eq(AccountRole.Maintenance));
	    	 		break;
	    	 	case "security":
	    	 		predicate = predicate.or(account.role.eq(AccountRole.Security));
	    	 		break;
	    	 	case "tenant":
	    	 		predicate = predicate.or(account.role.eq(AccountRole.Tenant));
	    	 		break;
	    	 	case "subtenant":
	    	 		predicate = predicate.or(account.role.eq(AccountRole.SubTenant));
	    	 		break;
	    	};	
        }    	    	
    	
    	if (sortColumn != null){
			if (sortColumn.equals("active")){
				if (sortOrder.equals("descend")){
					orderSpecifier = account.active.desc();
				}else{
					orderSpecifier = account.active.asc();
				}
			}else if (sortColumn.equals("apartment")){
				if (sortOrder.equals("descend")){
					orderSpecifier = account.apartment.unitNumber.lower().desc();
				}else{
					orderSpecifier = account.apartment.unitNumber.lower().asc();
				}
			}else if (sortColumn.equals("primaryEmail")){
				if (sortOrder.equals("descend")){
					orderSpecifier = account.primaryEmail.lower().desc();
				}else{
					orderSpecifier = account.primaryEmail.lower().asc();
				}
			}else{
				if (sortOrder.equals("descend")){
					orderSpecifier = account.firstName.lower().desc();
					orderSpecifierSecond = account.lastName.lower().desc();
				}else{
					orderSpecifier = account.firstName.lower().asc();
					orderSpecifierSecond = account.lastName.lower().asc();
				}
			}
    	}else{
			orderSpecifier = account.firstName.lower().asc();
			orderSpecifierSecond = account.lastName.lower().asc();
    	}
    	
        if (((propertyIdList == null) || propertyIdList.isEmpty())) {
        	JPQLQuery<Account> query = queryFactory.selectFrom(account);
        	
        	if (keywordsLowercase != null && keywordsLowercase.length() > 0 && !keywordsContainNumber){
        		query.where(predicate.and(account.role.in(roles)).and(account.deletedAt.isNull()));
        	}else{
        		// will filter out later
        		query.where(account.role.in(roles).and(account.deletedAt.isNull()));
        	}
            
            query.orderBy(orderSpecifier);
            if (orderSpecifierSecond != null){
            	query.orderBy(orderSpecifierSecond);
            }
            accounts = query.fetch();
        }
        else {
            accounts = new ArrayList<>();
            for ( AccountRole role : roles ) {
                if ( role == AccountRole.Tenant ) {
                    accounts.addAll(queryFactory.selectFrom(tenant)
                            .where(tenant.apartment.property.id.in(propertyIdList)
                                    .and(tenant.deletedAt.isNull()))
                            .orderBy(tenant.firstName.lower().asc())
                            .orderBy(tenant.lastName.lower().asc())
                            .fetch());
                }
                else if ( role == AccountRole.Maintenance ) {
                    accounts.addAll(queryFactory.selectFrom(maintenanceEmployee)
                            .where(maintenanceEmployee.manager.managedProperty.id.in(propertyIdList)
                                    .and(maintenanceEmployee.deletedAt.isNull()))
                            .orderBy(maintenanceEmployee.firstName.lower().asc())
                            .orderBy(maintenanceEmployee.lastName.lower().asc())
                            .fetch());
                }
                else if ( role == AccountRole.Security ) {
                    accounts.addAll(queryFactory.selectFrom(securityEmployee)
                            .where(securityEmployee.manager.managedProperty.id.in(propertyIdList)
                                    .and(securityEmployee.deletedAt.isNull()))
                            .orderBy(securityEmployee.firstName.lower().asc())
                            .orderBy(securityEmployee.lastName.lower().asc())
                            .fetch());
                }
                else if ( role == AccountRole.PropertyManager ) {
                    accounts.addAll(queryFactory.selectFrom(propertyManager)
                            .where(propertyManager.managedProperty.id.in(propertyIdList)
                                    .and(propertyManager.deletedAt.isNull()))
                            .orderBy(propertyManager.firstName.lower().asc())
                            .orderBy(propertyManager.lastName.lower().asc())
                            .fetch());
                }
                else if ( role == AccountRole.PropertyOwner ) {
                    accounts.addAll(queryFactory.selectFrom(propertyOwner)
                            .where(propertyOwner.ownedProperties.any().id.in(propertyIdList)
                                    .and(propertyOwner.deletedAt.isNull()))
                            .orderBy(propertyOwner.firstName.lower().asc())
                            .orderBy(propertyOwner.lastName.lower().asc())
                            .fetch());
                }
                else if ( role == AccountRole.AssistantPropertyManager ) {
                    accounts.addAll(queryFactory.selectFrom(assistantPropertyManager)
                            .where(assistantPropertyManager.manager.managedProperty.id.in(propertyIdList)
                                    .and(assistantPropertyManager.deletedAt.isNull()))
                            .orderBy(assistantPropertyManager.firstName.lower().asc())
                            .orderBy(assistantPropertyManager.lastName.lower().asc())
                            .fetch());
                }
                else if ( role == AccountRole.SubTenant ) {
                    accounts.addAll(queryFactory.selectFrom(subTenant)
                            .where(subTenant.parentTenant.apartment.property.id.in(propertyIdList)
                                    .and(subTenant.deletedAt.isNull()))
                            .orderBy(subTenant.firstName.lower().asc())
                            .orderBy(subTenant.lastName.lower().asc())
                            .fetch());
                }
            }
        }
        
        if ((keywordsLowercase != null && keywordsLowercase.length()>0)){
        	accountsFiltered = new ArrayList<>();
            for (Account account : accounts) {
            	
               if (account.getFullName().toLowerCase().contains(keywordsLowercase) ||
            	account.getPrimaryEmail().toLowerCase().contains(keywordsLowercase)) {
            	   accountsFiltered.add(account);
               }else{
	       	    	if (keywordsLowercase.contains("inactive") ){
	       	    		if (!account.getActive()){
	       	    			accountsFiltered.add(account);
	       	    		}
	    	    	}else if (keywordsLowercase.contains("active") ){
	    	    		if (account.getActive()){
	    	    			accountsFiltered.add(account);
	    	    		}
	    	    	}else{
	    	    		if (account.getApartment() != null){
	    	    			if (account.getApartment().getUnitNumber().contains(keywordsLowercase)){
	    	    				accountsFiltered.add(account);
	    	    			}
	    	    		}
	    	    		
	    	    	}
	       	    	
               }
               
            }
        }
        
        if (accountsFiltered != null){
        	accounts = accountsFiltered;
        }
        
        if (sortColumn == null){
        	accounts.sort(Account.COMPARE_BY_FIRST_LAST_NAME);
        }else{
        	if (sortColumn.equals("active")){
        		if (sortOrder.equals("descend")){
        			accounts.sort(Account.COMPARE_BY_STATUS_DESC);
        		}else{
        			accounts.sort(Account.COMPARE_BY_STATUS);
        		}
        	}else if (sortColumn.equals("apartment")){
        		if (sortOrder.equals("descend")){
        			accounts.sort(Account.COMPARE_BY_UNIT_DESC);
        		}else{
        			accounts.sort(Account.COMPARE_BY_UNIT);
        		}
        	}else if (sortColumn.equals("primaryEmail")){
        		if (sortOrder.equals("descend")){
        			accounts.sort(Account.COMPARE_BY_EMAIL_DESC);
        		}else{
        			accounts.sort(Account.COMPARE_BY_EMAIL);
        		}
        	}else{
        		if (sortOrder.equals("descend")){
        			accounts.sort(Account.COMPARE_BY_FIRST_LAST_NAME_DESC);
        		}else{
        			accounts.sort(Account.COMPARE_BY_FIRST_LAST_NAME);
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
                .where(account.primaryEmail.eq(email).and(account.deletedAt.isNull()))
                .fetchOne();
    }

    public List<Account> findInactiveTenantsAndSubTenants() {
        return queryFactory.selectFrom(account)
                .where(account.active.eq(false)
                .and(account.role.in(AccountRole.Tenant, AccountRole.SubTenant))
                .and(account.deletedAt.isNull())
                ).fetch();
    }

    public Account findByActionToken(String actionToken) {
        return queryFactory.selectFrom(account)
                .where(account.actionToken.eq(actionToken).and(account.deletedAt.isNull()))
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

    @Override
    public void delete(Account entity) {
        super.delete(entity);
        this.em.flush();
    }

    public long countByRoleAndActivationStatus(@NotNull Collection<AccountRole> roles, Boolean isActive) {
        Objects.requireNonNull(roles, "roles");

        final BooleanBuilder builder = new BooleanBuilder(account.role.in(roles).and(account.deletedAt.isNull()));
        if ( isActive != null ) {
            builder.and(account.active.eq(isActive));
        }

        return queryFactory.selectFrom(account).where(builder).fetchCount();
    }
}
