package com.creatix.domain.entity.store.business;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.attachment.AttachmentId;
import com.creatix.domain.entity.store.attachment.BusinessProfilePhoto;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Entity
@Data
public class BusinessProfile implements AttachmentId {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 120, nullable = false)
    private String name;

    @Column
    private Long lat;

    @Column
    private Long lng;

    @ManyToMany
    private List<BusinessCategory> businessCategoryList;

    @OneToMany(mappedBy = "businessProfile", cascade = {CascadeType.REMOVE})
    private List<BusinessProfilePhoto> businessProfilePhotoList;

    @Column
    private Long defaultPhotoId;

    @OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private BusinessContact contact;

    @Column(length = 120, nullable = false)
    private String description;

    @ManyToOne(optional = false)
    private Property property;

    @OneToMany(mappedBy = "businessProfile", cascade = {CascadeType.REMOVE})
    List<DiscountCoupon> discountCouponList;

    @OneToMany(mappedBy = "businessProfile", cascade = {CascadeType.REMOVE})
    List<BusinessProfileCarteItem> businessProfileCarte;

}
