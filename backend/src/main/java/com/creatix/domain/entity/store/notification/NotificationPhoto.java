package com.creatix.domain.entity.store.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "notification_id")
})
@BatchSize(size = 40)
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
