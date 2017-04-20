package com.creatix.domain.entity.store.photo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Created by kvimbi on 19/04/2017.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(of = "id")
@Data
public class GenericPhotoStore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 128)
    private String fileName;

    @Column(nullable = false, length = 2048)
    private String filePath;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StoredPhotoType storedPhotoType;
}
