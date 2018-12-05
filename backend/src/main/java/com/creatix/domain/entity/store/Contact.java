package com.creatix.domain.entity.store;

import com.creatix.domain.enums.CommunicationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
public class Contact {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CommunicationType communicationType;

    @Column(nullable = false)
    @NotNull
    private String value;
}
