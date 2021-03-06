package com.creatix.domain.entity.store;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(indexes = {
        @Index(columnList = "apartment_id")
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class ApartmentNeighbor {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @OneToOne
    private Apartment apartment;

    @Column
    private String unitNumber;

}
