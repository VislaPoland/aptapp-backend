package com.creatix.domain.entity.store.notification;

import com.creatix.domain.dto.apartment.BasicApartmentDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportAccountDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGlobalInfoDto;
import com.creatix.domain.dto.notification.reporting.NotificationReportGroupByAccountDto;
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

@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = Notification.MAINTENANCE_GLOBAL_INFO_MAPPING,
                classes = {
                        @ConstructorResult(
                                targetClass = NotificationReportGlobalInfoDto.class,
                                columns = {
                                        @ColumnResult(name = "requests", type = Long.class),
                                        @ColumnResult(name = "openRequests"),
                                        @ColumnResult(name = "passDueDateRequests", type = Long.class),
                                        @ColumnResult(name = "averageTimeToResponse"),
                                        @ColumnResult(name = "averageTimeToResolve")
                                }
                        )
                }
        ),
        @SqlResultSetMapping(
                name = Notification.NOTIFICATION_REPORT_GROUPED_BY_ACCOUNT_MAPPING,
                classes = {
                        @ConstructorResult(
                                targetClass = NotificationReportGroupByAccountDto.class,
                                columns = {
                                        @ColumnResult(name = "confirmed", type = Long.class),
                                        @ColumnResult(name = "resolved", type = Long.class),
                                        @ColumnResult(name = "averageTimeToResponse", type = Double.class),
                                        @ColumnResult(name = "averageTimeToResolve", type = Double.class)

                                }
                        ),
                        @ConstructorResult(
                                targetClass = NotificationReportAccountDto.class,
                                columns = {
                                        @ColumnResult(name = "account_id", type = Long.class),
                                        @ColumnResult(name = "firstName"),
                                        @ColumnResult(name = "lastName"),
                                        @ColumnResult(name = "fullName")
                                }
                        ),
                        @ConstructorResult(
                                targetClass = BasicApartmentDto.class,
                                columns = {
                                        @ColumnResult(name = "apartment_id", type = Long.class),
                                        @ColumnResult(name = "unit_number")
                                }
                        )
                }
        ),
        @SqlResultSetMapping(
                name = Notification.NOTIFICATION_REPORT_MAPPING,
                classes = {
                        @ConstructorResult(
                                targetClass = NotificationReportDto.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "title"),
                                        @ColumnResult(name = "description"),
                                        @ColumnResult(name = "createdAt", type = OffsetDateTime.class),
                                        @ColumnResult(name = "respondedAt", type = OffsetDateTime.class),
                                        @ColumnResult(name = "responseTime", type = Long.class),
                                        @ColumnResult(name = "resolutionTime", type = Long.class),
                                        @ColumnResult(name = "status"),
                                        @ColumnResult(name = "response")
                                }
                        ),
                        @ConstructorResult(
                                targetClass = BasicApartmentDto.class,
                                columns = {
                                        @ColumnResult(name = "targetApartmentId", type = Long.class),
                                        @ColumnResult(name = "targetApartmentUnitNumber")
                                }
                        ),
                        @ConstructorResult(
                                targetClass = NotificationReportAccountDto.class,
                                columns = {
                                        @ColumnResult(name = "authorId", type = Long.class),
                                        @ColumnResult(name = "authorFirstName"),
                                        @ColumnResult(name = "authorLastName"),
                                        @ColumnResult(name = "authorFullName")
                                }
                        ),
                        @ConstructorResult(
                                targetClass = BasicApartmentDto.class,
                                columns = {
                                        @ColumnResult(name = "authorApartmentId", type = Long.class),
                                        @ColumnResult(name = "authorApartmentUnitNumber")
                                }
                        ),
                        @ConstructorResult(
                                targetClass = NotificationReportAccountDto.class,
                                columns = {
                                        @ColumnResult(name = "respondedById", type = Long.class),
                                        @ColumnResult(name = "respondedByFirstName"),
                                        @ColumnResult(name = "respondedByLastName"),
                                        @ColumnResult(name = "respondedByFullName")
                                }
                        ),
                        @ConstructorResult(
                                targetClass = NotificationReportAccountDto.class,
                                columns = {
                                        @ColumnResult(name = "resolvedById", type = Long.class),
                                        @ColumnResult(name = "resolvedByFirstName"),
                                        @ColumnResult(name = "resolvedByLastName"),
                                        @ColumnResult(name = "resolvedByFullName")
                                }
                        )
                }
        )
}
)
@Entity
@Table(indexes = {
        @Index(columnList = "author_id"),
        @Index(columnList = "recipient_id"),
        @Index(columnList = "property_id")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public abstract class Notification {

    public static final String NOTIFICATION_REPORT_GROUPED_BY_ACCOUNT_MAPPING = "notificationReportGroupedByAccountMapping";
    public static final String MAINTENANCE_GLOBAL_INFO_MAPPING = "maintenanceGlobalInfoMapping";
    public static final String NOTIFICATION_REPORT_MAPPING = "notificationReportMapping";

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
    protected NotificationType type;

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

    @ManyToOne
    @JoinColumn
    private Account recipient;

    @ManyToOne()
    private Property property;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public NotificationGroup notificationGroup;
}
