package com.creatix.domain.entity.store.business;

import com.creatix.domain.entity.store.Property;
import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.List;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Entity
@Data
public class BusinessProfile {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 120, nullable = false)
    private String name;

    @Column
    private Long lat;

    @Column
    private Long lng;

    @Column(nullable = false)
    private boolean isImageUploaded = false;

    @OneToMany
    List<BusinessCategory> businessCategoryList;

    @OneToOne
    private BusinessContact contact;

    @Column(length = 120, nullable = false)
    private String description;

    @ManyToOne(optional = false)
    private Property property;

    @OneToMany
    List<DiscountCoupon> discountCouponList;

}
