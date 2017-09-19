package com.creatix.domain.entity.store;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.notification.EventInviteNotification;
import com.creatix.domain.enums.EventInviteResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@ToString(of = {"id", "attendant", "response"})
@EqualsAndHashCode(of = "id")
@BatchSize(size = 80)
public class EventInvite {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private long id;

	@ManyToOne(optional = false)
	@JoinColumn(nullable = false)
	@NotNull
	private EventSlot event;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@NotNull
	private Account attendant;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@NotNull
	private EventInviteResponse response;

	@OneToOne(mappedBy = "eventInvite", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private EventInviteNotification notification;
}
