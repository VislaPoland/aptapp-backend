package com.creatix.domain.entity.store.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class EmployeeBase extends Account {

}
