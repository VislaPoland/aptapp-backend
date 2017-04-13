package com.creatix.domain.entity.store.business;

import com.creatix.domain.entity.store.Property;
import lombok.Data;

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

    @Column(length = 120)
    private String name;

    @OneToMany
    List<BusinessCategory> businessCategoryList;

    @OneToOne
    private BusinessContact contact;

    @Column(length = 120)
    private String description;

    @ManyToOne
    private Property property;

    @OneToMany
    List<DiscountCoupon> discountCouponList;

}
