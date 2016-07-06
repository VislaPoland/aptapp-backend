package com.creatix.service.apartment;

import com.creatix.domain.Mapper;
import com.creatix.domain.dao.ApartmentDao;
import com.creatix.domain.dao.ApartmentNeighborDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dto.apartment.PersistApartmentRequest;
import com.creatix.domain.entity.Apartment;
import com.creatix.domain.entity.ApartmentNeighbor;
import com.creatix.domain.entity.ApartmentNeighbors;
import com.creatix.domain.entity.Property;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ApartmentService {

    @Autowired
    private ApartmentDao apartmentDao;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private Mapper mapper;
    @Autowired
    private ApartmentNeighborDao apartmentNeighborDao;

    public Apartment getApartment(Long apartmentId) {
        Apartment apartment = apartmentDao.findById(apartmentId);
        if ( apartment == null ) {
            throw new EntityNotFoundException(String.format("Apartment id=%d not found", apartmentId));
        }

        return apartment;
    }

    public List<Apartment> getApartmentsByPropertyId(long propertyId) {
        final Property property = propertyDao.findById(propertyId);
        authorizationManager.checkManager(property);

        return apartmentDao.findByProperty(property);
    }

    public Apartment getApartment(Property property, String unitNumber) {
        final Apartment apartment = apartmentDao.findByUnitNumberWithinProperty(unitNumber, property);
        if ( apartment == null ) {
            throw new EntityNotFoundException(String.format("Apartment no=%s not found in property id=%d", unitNumber, property.getId()));
        }
        return apartment;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.Administrator})
    public Apartment createApartment(@NotNull Long propertyId, @NotNull PersistApartmentRequest request) {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(request);

        final Property property = propertyDao.findById(propertyId);
        if ( property == null ) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }

        final Apartment apartment = new Apartment();
        mapper.fillApartment(request, apartment);
        apartment.setProperty(property);
        apartmentDao.persist(apartment);
        linkNeighbors(apartment);

        return apartment;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.Administrator})
    public Apartment updateApartment(@NotNull Long apartmentId, @NotNull PersistApartmentRequest request) {
        Objects.requireNonNull(apartmentId);
        Objects.requireNonNull(request);

        final Apartment apartment = getApartment(apartmentId);
        mapper.fillApartment(request, apartment);

        apartmentDao.persist(apartment);
        linkNeighbors(apartment);

        return apartment;
    }

    private void linkNeighbors(@NotNull Apartment apartment) {
        Objects.requireNonNull(apartment);

        final ApartmentNeighbors neighbors = apartment.getNeighbors();
        linkNeighbors(apartment.getProperty(),
                neighbors.getLeft(),
                neighbors.getRight(),
                neighbors.getBelow(),
                neighbors.getAbove(),
                neighbors.getOpposite(),
                neighbors.getBehind());
    }

    private void linkNeighbors(@NotNull Property property, ApartmentNeighbor... neighbors) {
        Objects.requireNonNull(property);

        for ( ApartmentNeighbor neighbor : neighbors ) {
            if ( neighbor == null ) {
                continue;
            }

            if ( neighbor.getUnitNumber() != null ) {
                // unit number is set, we are going to create or update link

                if ( neighbor.getApartment() != null ) {
                    // update link to new apartment
                    neighbor.setApartment(apartmentDao.findByUnitNumberWithinProperty(neighbor.getUnitNumber(), property));
                    apartmentNeighborDao.persist(neighbor);
                }
                else {
                    // create new link to neighbor apartment
                    neighbor.setApartment(apartmentDao.findByUnitNumberWithinProperty(neighbor.getUnitNumber(), property));
                    apartmentNeighborDao.persist(neighbor);
                }
            }
            else {
                // unit number is not set

                if ( neighbor.getApartment() != null ) {
                    // remove link from neighbor apartment
                    neighbor.setApartment(null);
                    apartmentNeighborDao.persist(neighbor);
                }

            }
        }
    }
}
