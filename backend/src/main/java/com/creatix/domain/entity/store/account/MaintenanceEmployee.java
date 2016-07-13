package com.creatix.domain.entity.store.account;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaintenanceEmployee extends ManagedEmployee { }
