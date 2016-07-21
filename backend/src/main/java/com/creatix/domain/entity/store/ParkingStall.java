package com.creatix.domain.entity.store;

import com.creatix.domain.entity.store.account.Tenant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(indexes = {
    @Index(columnList = "using_tenant_id"),
    @Index(columnList = "parking_vehicle_id")
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "number")
public class ParkingStall {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String number;

    @ManyToOne
    @JoinColumn
    private Tenant usingTenant;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Vehicle parkingVehicle;
}
