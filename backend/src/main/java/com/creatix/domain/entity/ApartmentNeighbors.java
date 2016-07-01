package com.creatix.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Embeddable
@Setter
@Getter
public class ApartmentNeighbors {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ApartmentNeighbor above;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ApartmentNeighbor below;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ApartmentNeighbor left;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ApartmentNeighbor right;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ApartmentNeighbor opposite;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ApartmentNeighbor behind;

}
