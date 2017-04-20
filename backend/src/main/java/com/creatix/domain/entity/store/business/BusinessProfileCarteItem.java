package com.creatix.domain.entity.store.business;

import com.creatix.domain.entity.store.photo.BusinessProfileCartePhoto;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by kvimbi on 19/04/2017.
 */
@Entity
@Data
public class BusinessProfileCarteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @OneToOne(optional = true)
    BusinessProfileCartePhoto businessProfileCartePhoto;

    @ManyToOne(optional = false)
    BusinessProfile businessProfile;

}
