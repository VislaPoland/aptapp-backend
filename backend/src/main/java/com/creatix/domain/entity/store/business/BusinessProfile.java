package com.creatix.domain.entity.store.business;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.photo.BusinessProfilePhoto;
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

    @Column(length = 120, nullable = false)
    private String name;

    @Column
    private Long lat;

    @Column
    private Long lng;

    @OneToMany
    private List<BusinessCategory> businessCategoryList;

    @OneToMany
    private List<BusinessProfilePhoto> businessProfilePhotoList;

    @Column
    private Long defaultPhotoId;

    @OneToOne
    private BusinessContact contact;

    @Column(length = 120, nullable = false)
    private String description;

    @ManyToOne(optional = false)
    private Property property;

    @OneToMany
    List<DiscountCoupon> discountCouponList;

    @OneToMany
    List<BusinessProfileCarteItem> businessProfileCarte;

}
