package com.creatix.domain.entity.store;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.BatchSize;

import lombok.Data;

/**
 *
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
@Entity
@BatchSize(size = 40)
@Data
public class DurationPerDayOfWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MaintenanceSlotSchedule schedule;

    @NotNull
    @Column(nullable = false)
    private LocalTime beginTime;

    @NotNull
    @Column(nullable = false)
    private LocalTime endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @NotNull
    @Column(nullable = false)
    private String timeZone;

    public DurationPerDayOfWeek() {
    }

    public DurationPerDayOfWeek(MaintenanceSlotSchedule schedule, LocalTime beginTime, LocalTime endTime, DayOfWeek dayOfWeek, String timeZone) {
        this.schedule = schedule;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.timeZone = timeZone;
    }

    @Transient
    public ZoneOffset getZoneOffset(LocalDateTime dt) {
        return getZoneId().getRules().getOffset(dt);
    }

    @Transient
    private ZoneId getZoneId() {
        return ZoneId.of(getTimeZone());
    }
}
