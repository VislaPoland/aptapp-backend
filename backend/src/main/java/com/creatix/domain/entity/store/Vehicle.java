package com.creatix.domain.entity.store;

import com.creatix.domain.entity.store.account.Tenant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(indexes = {
    @Index(columnList = "owner_id")
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String licensePlate;

    @Column
    private String make;

    @Column
    private String model;

    @Column
    private Integer year;

    @Column
    private String color;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Tenant owner;

}
