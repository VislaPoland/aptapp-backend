/* insert data to history */
insert into notification_history (id, author_id, notification_id, created_at, status)
  (
  /* all notification was created with status Pending */
  select nextval('hibernate_sequence'), n.author_id, n.id as notification_id, n.created_at, 'Pending' as status
  from notification n
  where n.dtype='MaintenanceNotification' OR n.dtype='SecurityNotification'
  union
  /* when set deleted at it should be also in notification history as Deleted and Closed */
  select nextval('hibernate_sequence'), null, n.id as notification_id, n.closed_at, n.status
  from notification n
  where (n.dtype='MaintenanceNotification' OR n.dtype='SecurityNotification') and (n.status = 'Deleted' or n.status = 'Closed') and n.closed_at IS NOT NULL
  union
    /* when notification is Resolved and type of Security then it should be also in notification history as Resolved  */
  select nextval('hibernate_sequence'), null, n.id as notification_id, n.responded_at, n.status
  from notification n
  where n.dtype='SecurityNotification' and n.status = 'Resolved' and n.responded_at IS NOT NULL and n.closed_at IS NULL
  union
  /* when notification is Closed or Resolved and type of Maintenance then it should be also in notification history as Confirmation, Rejection, Reschedule  */
  select nextval('hibernate_sequence'), null, n.id as notification_id, n.responded_at, m.status
  from notification n
  inner join maintenance_reservation m on n.id = m.notification_id
  where n.dtype='MaintenanceNotification' and n.responded_at IS NOT NULL
    and
    (n.status = 'Resolved') or (n.status = 'Closed' and n.closed_at IS NOT NULL)
  union
 /* when notification is only Rescheduled and waiting for user response create Reschedule history record  */
  select nextval('hibernate_sequence'), null, n.id as notification_id, n.responded_at, 'Rescheduled'
  from notification n
  where n.dtype='MaintenanceNotification' and n.status = 'Pending' and n.created_at < n.responded_at
  order by notification_id
);