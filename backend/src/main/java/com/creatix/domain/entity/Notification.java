package com.creatix.domain.entity;

import com.creatix.domain.enums.ActivityStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(of = "id")
public class Notification {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Column
    private String title;

    @Column(nullable = false, length = 20)
    @NotNull
    @Size(max = 20)
    private String description;

    @Column(length = 100)
    @Size(max = 100)
    private String message;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ActivityStatus status;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column
    private String response;
}
