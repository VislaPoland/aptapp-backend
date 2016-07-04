package com.creatix.domain.entity.account;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AssistantPropertyManager extends EmployeeBase {
    @ManyToOne
    private PropertyManager manager;
}
