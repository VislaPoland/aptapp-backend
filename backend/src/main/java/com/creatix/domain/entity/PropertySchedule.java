package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class PropertySchedule {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Integer startHour;

    @Column(nullable = false)
    @NotNull
    private Integer startMinute;

    @Column(nullable = false)
    @NotNull
    private Integer endHour;

    @Column(nullable = false)
    @NotNull
    private Integer endMinute;

    @Column(nullable = false)
    @NotNull
    private Integer periodLength;

    @Column(nullable = false)
    @NotNull
    private Integer slotsPerPeriod;
}
