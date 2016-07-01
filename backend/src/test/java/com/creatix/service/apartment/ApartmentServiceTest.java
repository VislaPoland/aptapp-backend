package com.creatix.service.apartment;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.dao.ApartmentDao;
import com.creatix.domain.dto.apartment.PersistApartmentRequest;
import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.Property;
import com.creatix.mock.WithMockCustomUser;
import com.creatix.service.property.PropertyService;
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
public class ApartmentServiceTest extends TestContext {

    @Autowired
    private ApartmentService apartmentService;
    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ApartmentDao apartmentDao;



    @Test
    @WithMockCustomUser("helen.owner@apartments.com")
    public void createUpdateApartment() throws Exception {

        final Property property = propertyService.getProperty(1L);
        assertNotNull(property);
        assertNotNull(property.getId());

        final PersistApartmentRequest req = new PersistApartmentRequest();
        req.setFloor(2);
        req.setUnitNumber("28");
        final PersistApartmentRequest.Neighbors neighbors = new PersistApartmentRequest.Neighbors();
        req.setNeighbors(neighbors);
        final PersistApartmentRequest.NeighborApartment above = new PersistApartmentRequest.NeighborApartment();
        neighbors.setAbove(above);
        above.setUnitNumber("37");

        final Apartment apartment = apartmentService.createApartment(property.getId(), req);
        assertNotNull(apartment);
        assertEquals("28", apartment.getUnitNumber());
        assertNotNull(apartment.getNeighbors());
        assertNull(apartment.getNeighbors().getLeft());
        assertNull(apartment.getNeighbors().getRight());
        assertNotNull(apartment.getNeighbors().getAbove());
        assertEquals("37", apartment.getNeighbors().getAbove().getUnitNumber());
        assertEquals(Long.valueOf(37), apartment.getNeighbors().getAbove().getApartment().getId());
        assertEquals(Integer.valueOf(2), apartment.getFloor());
        assertNotNull(apartment.getNeighbors().getAbove().getId());
    }

    @Test
    @WithMockCustomUser("helen.owner@apartments.com")
    public void updateUpdateApartment() throws Exception {
        final Apartment apartment31 = apartmentDao.findById(31L);
    }
}