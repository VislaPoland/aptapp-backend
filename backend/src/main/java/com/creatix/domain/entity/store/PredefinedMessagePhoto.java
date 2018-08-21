package com.creatix.domain.entity.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Photo for predefined message.
 * @author <a href="mailto:martin@thinkcreatix.com">martin dupal</a>
 */
@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class PredefinedMessagePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 128)
    private String fileName;

    @Column(nullable = false, length = 2048)
    private String filePath;

    @ManyToOne(optional = false)
    private PredefinedMessage predefinedMessage;
}
