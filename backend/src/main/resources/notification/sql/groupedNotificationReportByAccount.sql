SELECT
a.first_name as firstName,
a.last_name as lastName,
a.first_name || ' ' || a.last_name as fullName,
ap.id as apartment_id,
ap.unit_number,
stats.account_id,
stats.confirmed,
stats.resolved,
(
    CASE
        WHEN a1.averageTimeToConfirm IS NULL THEN 0
        ELSE a1.averageTimeToConfirm
    END
    ) as averageTimeToConfirm,
    (
    CASE
        WHEN a2.averageTimeToResolve IS NULL THEN 0
        ELSE a2.averageTimeToResolve
    END
    ) as averageTimeToResolve
FROM (
    select
        a.id as account_id,
        sum(case when nh.status = 'Confirmed' then 1 end) as confirmed,
        sum(case when nh.status = 'Resolved' then 1 end) as resolved
    FROM notification_history nh
    JOIN notification n on n.id = nh.notification_id
    JOIN account a ON a.id = nh.author_id
    WHERE n.created_at between :from and :to
    and n.type = :type and n.property_id = :propertyId
    AND a.role = :role
    group by a.id
) as stats
JOIN account a ON a.id = stats.account_id
LEFT JOIN apartment ap ON ap.id = a.apartment_id
LEFT JOIN (
    select nh2.author_id, avg(extract(SECONDS FROM (nh2.created_at - nh1.created_at))) as averageTimeToConfirm
    from notification_history nh1
    JOIN notification_history nh2 on nh1.notification_id = nh2.notification_id
    where nh1.property_id = :propertyId and nh1.created_at between :from and :to and nh1.type = :type
    AND nh1.status = 'Pending' AND nh2.status = 'Confirmed'
    group by nh2.author_id
  ) as a1 on a1.author_id = stats.account_id
LEFT JOIN
  (
    select nh2.author_id, avg(extract(SECONDS FROM (nh2.created_at - nh1.created_at))) as averageTimeToResolve
    from notification_history nh1
    JOIN notification n on n.id = nh1.notification_id
    JOIN notification_history nh2 on nh1.notification_id = nh2.notification_id
    where nh1.property_id = :propertyId and n.created_at between :from and :to and n.type = :type
    AND nh1.status = 'Confirmed' AND nh2.status = 'Resolved'
    group by nh2.author_id
  ) as a2 on a2.author_id = stats.account_id
where a.role = :role