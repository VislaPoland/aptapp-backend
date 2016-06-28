package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class NotificationPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 128)
    private String fileName;

    @Column(nullable = false, length = 2048)
    private String filePath;

    @ManyToOne(optional = false)
    private Notification notification;
}
