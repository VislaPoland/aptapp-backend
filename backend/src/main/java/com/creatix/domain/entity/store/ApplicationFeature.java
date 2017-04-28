package com.creatix.domain.entity.store;

import com.creatix.domain.enums.ApplicationFeatureType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * Created by kvimbi on 11/04/2017.
 */
@Table(uniqueConstraints =
    @UniqueConstraint(columnNames = {"property_id", "applicationFeatureType"})
)
@Entity
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "applicationFeatureType", "property", "enabled"})
@Data
@Accessors(chain = true)
public class ApplicationFeature {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationFeatureType applicationFeatureType;

    @Column
    private boolean enabled;

    @ManyToOne
    private Property property;

}
