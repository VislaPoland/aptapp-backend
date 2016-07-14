package com.creatix.service.apartment;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.dao.ApartmentDao;
import com.creatix.domain.dto.apartment.PersistApartmentRequest;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.ApartmentNeighbor;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.mock.WithMockCustomUser;
import com.creatix.service.property.PropertyService;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    @PersistenceContext
    protected EntityManager em;

    @After
    public void after() {
        em.flush();
    }

    @Test
    @WithMockCustomUser("helen.owner@apartments.com")
    public void createUpdateApartment() throws Exception {

        final Property property = propertyService.getProperty(1L);
        assertNotNull(property);
        assertNotNull(property.getId());

        final PersistApartmentRequest req = new PersistApartmentRequest();
        req.setFloor(2);
        req.setUnitNumber("28");
        final PersistApartmentRequest.NeighborsDto neighbors = new PersistApartmentRequest.NeighborsDto();
        req.setNeighbors(neighbors);
        final PersistApartmentRequest.NeighborApartmentDto above = new PersistApartmentRequest.NeighborApartmentDto();
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
    public void updateApartment() throws Exception {
        final Property property = propertyService.getProperty(1L);
        assertNotNull(property);
        assertNotNull(property.getId());
        final Apartment apartment31 = apartmentDao.findById(31L);
        assertNotNull(apartment31);
        final ApartmentNeighbor neighbor21 = new ApartmentNeighbor();

        final Apartment apartment21 = apartmentDao.findById(21L);
        assertNotNull(apartment21);
        neighbor21.setApartment(apartment21);
        neighbor21.setUnitNumber(apartment21.getUnitNumber());
        apartment31.getNeighbors().setBelow(neighbor21);
        apartmentDao.persist(apartment31);
        assertNotNull(neighbor21.getId());

        final PersistApartmentRequest req = new PersistApartmentRequest();
        req.setFloor(apartment31.getFloor());
        req.setUnitNumber(apartment31.getUnitNumber());
        final PersistApartmentRequest.NeighborsDto neighbors = new PersistApartmentRequest.NeighborsDto();
        req.setNeighbors(neighbors);
        final PersistApartmentRequest.NeighborApartmentDto above = new PersistApartmentRequest.NeighborApartmentDto();
        neighbors.setAbove(above);
        above.setUnitNumber("33");
        final PersistApartmentRequest.NeighborApartmentDto below = new PersistApartmentRequest.NeighborApartmentDto();
        neighbors.setBelow(below);
        below.setUnitNumber(apartment21.getUnitNumber());

        final Apartment apartment = apartmentService.updateApartment(apartment31.getId(), req);
        assertEquals(apartment31.getId(), apartment.getId());
        assertNotNull(apartment.getNeighbors().getAbove());
        assertNotNull(apartment.getNeighbors().getBelow());
        assertEquals(neighbor21.getId(), apartment.getNeighbors().getBelow().getId());
        assertEquals(neighbor21.getUnitNumber(), apartment.getNeighbors().getBelow().getUnitNumber());
        assertNull(apartment.getNeighbors().getLeft());
        assertNull(apartment.getNeighbors().getRight());
    }


    @Test
    @WithMockCustomUser("helen.owner@apartments.com")
    public void unlinkNeighborApartment() throws Exception {
        final Property property = propertyService.getProperty(1L);
        assertNotNull(property);
        assertNotNull(property.getId());
        final Apartment apartment31 = apartmentDao.findById(31L);
        assertNotNull(apartment31);
        final ApartmentNeighbor neighbor21 = new ApartmentNeighbor();

        final Apartment apartment21 = apartmentDao.findById(21L);
        assertNotNull(apartment21);
        neighbor21.setApartment(apartment21);
        neighbor21.setUnitNumber(apartment21.getUnitNumber());
        apartment31.getNeighbors().setBelow(neighbor21);
        apartmentDao.persist(apartment31);
        assertNotNull(neighbor21.getId());

        final PersistApartmentRequest req = new PersistApartmentRequest();
        req.setFloor(apartment31.getFloor());
        req.setUnitNumber(apartment31.getUnitNumber());
        req.setNeighbors(new PersistApartmentRequest.NeighborsDto());

        final Apartment apartment = apartmentService.updateApartment(apartment31.getId(), req);
        assertNull(apartment.getNeighbors().getAbove());
        assertNull(apartment.getNeighbors().getBelow());
        assertNull(apartment.getNeighbors().getLeft());
        assertNull(apartment.getNeighbors().getRight());
    }

    @Test
    @WithMockCustomUser("helen.owner@apartments.com")
    public void deleteApartment() throws Exception {
        final Apartment apartment = apartmentService.deleteApartment(33L);
        assertNotNull(apartment);
        final Tenant tenant = apartment.getTenant();
        assertNotNull(tenant);
        assertTrue(tenant.isDeleted());
        assertFalse(tenant.getActive());
    }
}