package com.creatix.domain.entity.store.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityNotification extends Notification {
}
