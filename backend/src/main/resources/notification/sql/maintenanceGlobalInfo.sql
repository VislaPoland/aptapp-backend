select cou.requests,
       (
         CASE
           WHEN cou.requests = 0 THEN 0
           ELSE 100 - (r.resolved / Cast(cou.requests AS FLOAT)) * 100
           END
         ) as openRequests,
       (
        CASE
            WHEN pdd.pass_due_date IS NULL THEN 0
            ELSE pdd.pass_due_date
            END
        ) as passDueDateRequests,
       (
        CASE
            WHEN a1.averageTimeToResponse IS NULL THEN 0
            ELSE a1.averageTimeToResponse
        END
        ) as averageTimeToResponse,
        (
        CASE
            WHEN a2.averageTimeToResolve IS NULL THEN 0
            ELSE a2.averageTimeToResolve
        END
        ) as averageTimeToResolve
from
/* get count of notification for given range */
  (
    select count(nh1.*) requests
    from notification_history nh1
    where nh1.property_id = :propertyId AND nh1.created_at between :from and :to
    and nh1.type = :type and nh1.status = 'Pending'
  ) as cou,
  /* get count of resolved notifications for given range */
 (
   select count(DISTINCT nh.notification_id) resolved
   from notification_history nh
          join notification n on nh.notification_id = n.id and n.status = 'Resolved'
   where n.property_id = :propertyId AND n.created_at between :from and :to and n.type = :type
 ) as r,
  /* get count of pass due date notifications */
 (
   select SUM(
              CASE
                WHEN (nh.status = 'Confirmed' or nh.status = 'Rejected' or nh.status = 'Rescheduled') AND nh.created_at > mr.begin_time
                  then 1
                WHEN mr.status = 'Pending' AND now() > mr.begin_time
                  then 1
                ELSE 0
                END
            ) as pass_due_date
   from notification_history nh
   join notification n on n.id = nh.notification_id
          join maintenance_reservation mr ON mr.notification_id = nh.notification_id
   where n.property_id = :propertyId and n.created_at between :from and :to and n.type = :type
     and (nh.status = 'Confirmed' or mr.status = 'Pending')
 ) as pdd,
  /* get average time to confirm notification & average time to resolve notification */
  (
    select avg(extract(epoch FROM (nh2.created_at - nh1.created_at))) as averageTimeToResponse
    from notification_history nh1
    JOIN notification_history nh2 on nh1.notification_id = nh2.notification_id
    where nh1.property_id = :propertyId and nh1.created_at between :from and :to and nh1.type = :type
    AND nh1.status = 'Pending'
    AND nh2.status IN (select status from notification_status_flow WHERE global_status = 'Responded' AND type = :type)
  ) as a1,
  (
    select avg(extract(epoch FROM (nh2.created_at - nh1.created_at))) as averageTimeToResolve
    from notification_history nh1
    join notification n on n.id = nh1.notification_id
    JOIN notification_history nh2 on nh1.notification_id = nh2.notification_id
    where nh1.property_id = :propertyId and n.created_at between :from and :to and nh1.type = :type
    AND nh1.status = 'Pending'
    AND nh2.status IN (select status from notification_status_flow WHERE global_status = 'Resolved' AND type = :type)
  ) as a2
