package com.creatix.domain.entity.store.business;

import com.creatix.domain.entity.store.attachment.AttachmentId;
import com.creatix.domain.entity.store.attachment.DiscountCouponPhoto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Entity
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class DiscountCoupon implements AttachmentId {

    public static final int UNLIMITED_USE = -1;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private BusinessProfile businessProfile;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(nullable = false)
    private int availableUses = 0;

    @Column
    private String code;

    @Column
    private String title;

    @Column
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    private DiscountCouponPhoto discountCouponPhoto;

}
