package com.creatix.controller;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.controller.v1.TenantController;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.tenant.ParkingStallDto;
import com.creatix.domain.dto.tenant.PersistTenantRequest;
import com.creatix.domain.dto.tenant.TenantDto;
import com.creatix.domain.dto.tenant.VehicleDto;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.enums.TenantType;
import com.creatix.mock.WithMockCustomUser;
import com.creatix.service.TenantService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class TenantControllerTest {

    @Autowired
    private TenantController tenantController;
    @Autowired
    private TenantService tenantService;

    @Test
    @WithMockCustomUser("mark.building@apartments.com")
    public void createUpdateTenant() throws Exception {
        PersistTenantRequest request = new PersistTenantRequest();
        request.setType(TenantType.Owner);
        request.setApartmentId(23L);
        request.setFirstName("Michael");
        request.setLastName("Johnson");
        request.setPrimaryPhone("00000000000");
        request.setPrimaryEmail("nonexistingemail@test.com");

        ParkingStallDto parkingStallDto1 = new ParkingStallDto();
        parkingStallDto1.setNumber("76/D");
        request.setParkingStalls(new ArrayList<>());
        request.getParkingStalls().add(parkingStallDto1);

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setColor("red");
        vehicleDto.setMake("Toyota");
        vehicleDto.setModel("Camry");
        vehicleDto.setYear(2016);
        request.setVehicles(new ArrayList<>());
        request.getVehicles().add(vehicleDto);

        final DataResponse<TenantDto> tenantData1 = tenantController.createTenant(request);
        assertNotNull(tenantData1);
        final TenantDto tenantDto1 = tenantData1.getData();
        assertNotNull(tenantDto1);
        assertNotNull(tenantDto1.getId());
        assertNotNull(tenantDto1.getParkingStalls());
        assertEquals(1, tenantDto1.getParkingStalls().size());
        assertNotNull(tenantDto1.getVehicles());
        assertEquals(1, tenantDto1.getVehicles().size());
        assertEquals("76/D", tenantDto1.getParkingStalls().get(0).getNumber());

        final Tenant tenant1 = tenantService.getTenant(tenantDto1.getId());
        assertEquals(tenantDto1.getVehicles().size(), tenant1.getVehicles().size());
        assertEquals(tenantDto1.getParkingStalls().size(), tenant1.getParkingStalls().size());

        request.getVehicles().clear();

        parkingStallDto1.setId(tenantDto1.getParkingStalls().get(0).getId());
        parkingStallDto1.setNumber("76/C");
        ParkingStallDto parkingStallDto2 = new ParkingStallDto();
        parkingStallDto2.setNumber("76/E");
        request.getParkingStalls().add(parkingStallDto2);


        final DataResponse<TenantDto> tenantData2 = tenantController.updateTenant(tenantDto1.getId(), request);
        assertNotNull(tenantData2);
        final TenantDto tenantDto2 = tenantData2.getData();
        assertNotNull(tenantDto2);
        assertNotNull(tenantDto2.getId());
        assertNotNull(tenantDto2.getParkingStalls());
        assertEquals(2, tenantDto2.getParkingStalls().size());
        assertNotNull(tenantDto2.getVehicles());
        assertEquals(0, tenantDto2.getVehicles().size());

        final Tenant tenant2 = tenantService.getTenant(tenantDto2.getId());
        assertEquals(tenantDto2.getVehicles().size(), tenant2.getVehicles().size());
        assertEquals(tenantDto2.getParkingStalls().size(), tenant2.getParkingStalls().size());

        tenant2.getParkingStalls().forEach(p -> {
            if ( Objects.equals(p.getId(), tenantDto1.getParkingStalls().get(0).getId()) ) {
                assertEquals(parkingStallDto1.getNumber(), p.getNumber());
            }
            else {
                assertEquals(parkingStallDto2.getNumber(), p.getNumber());
            }
        });
    }

}