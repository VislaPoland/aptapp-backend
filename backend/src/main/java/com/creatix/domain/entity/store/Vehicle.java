package com.creatix.domain.entity.store;

import com.creatix.domain.entity.store.account.Tenant;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(indexes = {
    @Index(columnList = "owner_id")
})
@BatchSize(size = 40)
@Setter
@Getter
@NoArgsConstructor
// No hash code override, see https://hibernate.atlassian.net/browse/HHH-3799
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
