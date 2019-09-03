package com.creatix.domain.entity.store;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "property_id")
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class PropertyLogo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 128)
    private String fileName;

    @Column(nullable = false, length = 2048)
    private String filePath;

    @OneToOne(optional = false)
    private Property property;
}
