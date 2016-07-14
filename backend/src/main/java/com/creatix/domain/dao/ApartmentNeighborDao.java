package com.creatix.domain.dao;

import com.creatix.domain.entity.store.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class ApartmentNeighborDao extends DaoBase<ApartmentNeighbor, Long> {

    public void deleteByApartment(@NotNull Apartment apartment) {
        Objects.requireNonNull(apartment);

        final List<ApartmentNeighbor> neighbors = queryFactory.selectFrom(QApartmentNeighbor.apartmentNeighbor)
                .where(QApartmentNeighbor.apartmentNeighbor.apartment.eq(apartment))
                .fetch();

        for ( ApartmentNeighbor neighbor : neighbors ) {
            final Apartment parentApartment = queryFactory.selectFrom(QApartment.apartment).where(
                    QApartment.apartment.neighbors.above.eq(neighbor)
                            .or(QApartment.apartment.neighbors.below.eq(neighbor))
                            .or(QApartment.apartment.neighbors.left.eq(neighbor))
                            .or(QApartment.apartment.neighbors.right.eq(neighbor))
            ).fetchOne();
            unassignFromApartment(neighbor, parentApartment);
            em.persist(parentApartment);

            neighbor.setApartment(null);
            delete(neighbor);
        }
        em.flush();
    }


    private void unassignFromApartment(@NotNull ApartmentNeighbor neighbor, @NotNull Apartment apartment) {
        Objects.requireNonNull(neighbor);
        Objects.requireNonNull(apartment);

        final ApartmentNeighbors neighbors = apartment.getNeighbors();
        if ( Objects.equals(neighbor, neighbors.getAbove()) ) {
            neighbors.setAbove(null);
        }
        if ( Objects.equals(neighbor, neighbors.getBelow()) ) {
            neighbors.setBelow(null);
        }
        if ( Objects.equals(neighbor, neighbors.getLeft()) ) {
            neighbors.setLeft(null);
        }
        if ( Objects.equals(neighbor, neighbors.getRight()) ) {
            neighbors.setRight(null);
        }
    }
}
