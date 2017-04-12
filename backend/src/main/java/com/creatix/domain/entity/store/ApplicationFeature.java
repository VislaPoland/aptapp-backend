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
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "applicationFeatureType", "apartment", "enabled"})
@Data
@Accessors(chain = true)
public class ApplicationFeature {

    @Id
    @Enumerated(EnumType.STRING)
    @GeneratedValue
    private Long id;

    @Column
    private ApplicationFeatureType applicationFeatureType;

    @Column
    private boolean enabled;

    @ManyToOne
    private Apartment apartment;

}
