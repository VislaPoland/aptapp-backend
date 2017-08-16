package com.creatix.domain.entity.store.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private List<PersonalMessage> messages = new ArrayList<>(0);

    public void addMessage(@NotNull PersonalMessage message) {
        Objects.requireNonNull(message, "message");

        message.setPersonalMessageGroup(this);
        messages.add(message);
    }

    @Column(nullable = false)
    private OffsetDateTime createdAt;
}
