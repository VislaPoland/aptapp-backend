package com.creatix.configuration.security;

import com.creatix.domain.dao.ApplicationFeatureDao;
import com.creatix.domain.entity.store.ApplicationFeature;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.ApplicationFeatureType;
import com.creatix.security.AuthenticatedUserDetails;
import com.creatix.security.RoleSecured;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by Tomas Michalek on 11/04/2017.
 */
@Component
public class FeatureAccessDecisionVoter implements AccessDecisionVoter<Object> {

    @Autowired
    private ApplicationFeatureDao applicationFeatureDao;

    int ACCESS_ABSTAIN = 0;

    private static final HashSet<String> SUPER_PRIVILEGES = new HashSet<>();

    @PostConstruct
    private void init() {
        Stream.of(AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager)
            .forEach(accountRole -> SUPER_PRIVILEGES.add("ROLE_" + accountRole.name().toUpperCase()));
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(MethodInvocation.class);
    }

    private boolean hasSuperPrivilege(Collection<? extends GrantedAuthority> authorities) {
        for (GrantedAuthority authority: authorities) {
            if (SUPER_PRIVILEGES.contains(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public Set<Property> listAccountProperties(Account account) {
        if (account instanceof Tenant) {
            return Collections.singleton(((Tenant) account).getApartment().getProperty());
        } else if (account instanceof SubTenant) {
            return Collections.singleton(((SubTenant) account).getApartment().getProperty());
        } else if (account instanceof SecurityEmployee) {
            return Collections.singleton(((SecurityEmployee) account).getManager().getManagedProperty());
        } else if (account instanceof PropertyOwner) {
            return ((PropertyOwner) account).getOwnedProperties();
        } else if (account instanceof PropertyManager) {
            return Collections.singleton(((PropertyManager) account).getManagedProperty());
        } else if (account instanceof MaintenanceEmployee) {
            return Collections.singleton(((MaintenanceEmployee) account).getManager().getManagedProperty());
        } else if (account instanceof AssistantPropertyManager) {
            return Collections.singleton(((AssistantPropertyManager) account).getManager().getManagedProperty());
        }
        return null;
    }


    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {

        if (hasSuperPrivilege(authentication.getAuthorities())) {
            return ACCESS_ABSTAIN;
        }

        if (! (authentication.getPrincipal() instanceof AuthenticatedUserDetails)) {
            return ACCESS_ABSTAIN;
        }

        RoleSecured annotation = ((MethodInvocation) object).getMethod().getAnnotation(RoleSecured.class);
        if (annotation == null || annotation.feature() == ApplicationFeatureType.NONE) {
            return ACCESS_ABSTAIN;
        }

        AuthenticatedUserDetails userDetails = (AuthenticatedUserDetails) authentication.getPrincipal();

        Set<Property> properties = listAccountProperties(userDetails.getAccount());
        if (properties.size() != 1) {
            return ACCESS_ABSTAIN;
        }

        ApplicationFeature feature = applicationFeatureDao.findByFeatureTypeAndApartment(
                annotation.feature(),
                properties.iterator().next()
        );

        if (!feature.isEnabled()) {
            throw new AccessDeniedException("Access denied");
        }

        return ACCESS_ABSTAIN;
    }
}
