package com.creatix.domain.entity.store;


import com.creatix.domain.enums.AudienceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EventSlot extends Slot {
    @NotEmpty
    @Column
    private String title;

    @Column(length = 2048)
    private String description;

    @Column
    private String location;

    @Enumerated(EnumType.STRING)
    @Column
    private AudienceType audience;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventInvite> invites = new HashSet<>();

    public void addEventInvite(@Nonnull EventInvite eventInvite) {
        eventInvite.setEvent(this);
        invites.add(eventInvite);
    }
}
