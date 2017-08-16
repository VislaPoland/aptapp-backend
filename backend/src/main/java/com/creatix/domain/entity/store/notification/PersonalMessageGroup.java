package com.creatix.domain.entity.store.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class PersonalMessageGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @OneToMany(mappedBy = "personalMessageGroup")
    private List<PersonalMessage> messages;

    @Column(nullable = false)
    private OffsetDateTime createdAt;
}
