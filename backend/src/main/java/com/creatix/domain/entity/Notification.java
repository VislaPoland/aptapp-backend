package com.creatix.domain.entity;

import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Notification extends HistoryEntity {
    @Column(nullable = false, length = 20)
    @NotNull
    @Size(max = 20)
    private String title;

    @Column(length = 100)
    @Size(max = 100)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private NotificationStatus status;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column
    private String response;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private NotificationType type;

    @ManyToOne(optional = false)
    @JoinColumn
    private Account author;
}
