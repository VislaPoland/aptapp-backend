package com.creatix.domain.entity;

import com.creatix.domain.enums.FacilityType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Facility {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    private Property property;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private FacilityType type;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FacilityDetail> details;
}
