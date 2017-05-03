package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "property_id")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

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
    @Enumerated(EnumType.STRING)
    @NotNull
    private NotificationType type;

    @ManyToOne(optional = false)
    @JoinColumn
    private Account author;

    @Column(nullable = false)
    @NotNull
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    @NotNull
    private OffsetDateTime updatedAt;

    @Column
    private OffsetDateTime deletedAt;

    @OneToMany(mappedBy = "notification")
    private List<NotificationPhoto> photos = new ArrayList<>(1);

    @ManyToOne(optional = false)
    @NotNull
    private Property property;
}
