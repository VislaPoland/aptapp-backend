package com.creatix.domain.entity.store;

import com.creatix.domain.entity.store.account.Tenant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"unitNumber", "property_id"})
}, indexes = {
        @Index(columnList = "property_id"),
        @Index(columnList = "above_id"),
        @Index(columnList = "below_id"),
        @Index(columnList = "left_id"),
        @Index(columnList = "right_id"),
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "unitNumber"})
public class Apartment {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String unitNumber;

    @Column(length = 16)
    private String floor;

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private Property property;

    @OneToOne(mappedBy = "apartment", fetch = FetchType.LAZY)
    private Tenant tenant;

    @Embedded
    @NotNull
    private ApartmentNeighbors neighbors;
}
