package com.creatix.domain.entity.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"manager"})
public abstract class EmployeeBase extends Account {

}
