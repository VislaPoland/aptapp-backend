package com.creatix.domain.entity.store;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class PredefinedMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 1024)
    @NotEmpty
    private String body;

    @ManyToOne(optional = false)
    private Property property;
}
