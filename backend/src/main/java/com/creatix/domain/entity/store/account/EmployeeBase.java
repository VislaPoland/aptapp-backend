package com.creatix.domain.entity.store.account;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class EmployeeBase extends Account {

}
