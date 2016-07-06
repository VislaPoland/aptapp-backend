package com.creatix.domain.entity.store;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
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
