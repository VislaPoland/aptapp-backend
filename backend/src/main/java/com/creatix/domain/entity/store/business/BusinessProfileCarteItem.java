package com.creatix.domain.entity.store.business;

import com.creatix.domain.entity.store.attachment.AttachmentId;
import com.creatix.domain.entity.store.attachment.BusinessProfileCartePhoto;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by Tomas Michalek on 19/04/2017.
 */
@Entity
@Data
public class BusinessProfileCarteItem implements AttachmentId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @OneToOne(optional = true, cascade = {CascadeType.ALL})
    BusinessProfileCartePhoto businessProfileCartePhoto;

    @ManyToOne(optional = false)
    BusinessProfile businessProfile;

}
