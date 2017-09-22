package com.creatix.service;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.dto.tenant.PersistTenantRequest;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.mock.WithMockCustomUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class TenantServiceTest {

    @Autowired
    private TenantService tenantService;

    @Test
    @WithMockCustomUser("mark.building@apartments.com")
    public void recreateTenantFromRequest() throws Exception {
        final PersistTenantRequest request = new PersistTenantRequest();
        request.setPrimaryEmail("nonexisting@test.com");
        request.setApartmentId(36L);
        request.setFirstName("Missing");
        request.setLastName("Test");
        request.setPrimaryPhone("0000000000000");

        final Tenant tenant = tenantService.createTenantFromRequest(request);
        assertNotNull(tenant);
        assertNotNull(tenant.getId());
        assertNotNull(tenant.getApartment());
        assertEquals("nonexisting@test.com", tenant.getPrimaryEmail());
        assertEquals((Long) 36L, tenant.getApartment().getId());

        final Tenant tenantDeleted = tenantService.deleteTenant(tenant.getId());
        assertNotNull(tenantDeleted);
        assertNotNull(tenantDeleted.getDeletedAt());
        assertFalse(tenant.getActive());

        final Tenant tenantRecreated = tenantService.createTenantFromRequest(request);
        assertNotNull(tenantRecreated);
        assertNotNull(tenantRecreated.getActionToken());
        assertNull(tenantRecreated.getDeletedAt());
    }
}