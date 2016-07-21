package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.Apartment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(indexes = {
        @Index(columnList = "target_apartment_id")
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public class NeighborhoodNotification extends Notification {
    @ManyToOne
    @JoinColumn
    @NotNull
    private Apartment targetApartment;
}
