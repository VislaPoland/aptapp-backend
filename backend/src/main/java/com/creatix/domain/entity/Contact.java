package com.creatix.domain.entity;

import com.creatix.domain.enums.CommunicationType;
import com.creatix.domain.enums.ContactType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Contact {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ContactType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CommunicationType communicationType;

    @Column(nullable = false)
    @NotNull
    private String value;
}
