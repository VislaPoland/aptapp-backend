WITH max_status AS (
  SELECT nh.notification_id, max(nh.created_at) created_at
  FROM notification_history nh
  JOIN notification n ON n.id = nh.notification_id
  WHERE n.created_at BETWEEN :from AND :to AND n.type = :type AND n.property_id = :propertyId
  GROUP BY nh.notification_id
), nhstatus AS (
  SELECT nh.status, nh.notification_id
  FROM notification_history nh
  WHERE nh.notification_id = (SELECT notification_id FROM max_status WHERE created_at = nh.created_at)
)
SELECT
  n.id,
  n.title,
  n.description,
  n.created_at as createdAt,
  n.responded_at as respondedAt,
  (
    CASE WHEN (
    SELECT status FROM nhstatus
    WHERE n.id = notification_id
    ) IS NULL THEN n.status
    ELSE (
      SELECT status FROM nhstatus
      WHERE n.id = notification_id )
    END
    ) as status,
  n.response,
  (
    CASE
      WHEN nh1.id IS NOT NULL THEN EXTRACT(EPOCH FROM (nh1.created_at - n.created_at))
      ELSE NULL
    END
  ) as responseTime,
  (
    CASE
      WHEN nh1.id IS NULL AND nh2.id IS NULL THEN NULL
      WHEN nh1.id IS NULL AND nh2.id IS NOT NULL THEN EXTRACT(EPOCH FROM (nh2.created_at - n.created_at))
      WHEN n.responded_at IS NULL THEN NULL
      ELSE EXTRACT(EPOCH FROM (n.responded_at - n.created_at))
    END
    ) as resolutionTime,
    ac.id as authorId,
    ac.first_name as authorFirstName,
    ac.last_name as authorLastName,
    ac.first_name || ' ' || ac.last_name as authorFullName,
    /* */
    (
    case WHEN ap.id IS NULL THEN parent_apartment.id
    ELSE ap.id
    END
     )as authorApartmentId,
    (
    case when ap.unit_number IS NULL THEN parent_apartment.unit_number
    ELSE ap.unit_number
    END
     )as authorApartmentUnitNumber,
    /* */
    a.id as targetApartmentId,
    a.unit_number as targetApartmentUnitNumber,
    ac1.id as respondedById,
    ac1.first_name as respondedByFirstName,
    ac1.last_name as respondedByLastName,
    ac1.first_name || ' ' || ac1.last_name as respondedByFullName,
    ac2.id as resolvedById,
    ac2.first_name as resolvedByFirstName,
    ac2.last_name as resolvedByLastName,
    ac2.first_name || ' ' || ac2.last_name as resolvedByFullName
FROM
  notification n
  LEFT JOIN notification_history nh1 on nh1.notification_id = n.id AND nh1.status = 'Confirmed'
    LEFT JOIN account ac1 ON ac1.id = nh1.author_id
  LEFT JOIN notification_history nh2 on nh2.notification_id = n.id AND nh2.status = 'Resolved'
    LEFT JOIN account ac2 ON ac2.id = nh2.author_id
  LEFT JOIN apartment a on a.id = n.target_apartment_id
   JOIN account ac ON ac.id = n.author_id
   LEFT JOIN apartment ap ON ap.id = ac.apartment_id
   LEFT JOIN account parent ON ac.parent_tenant_id = parent.id
   LEFT JOIN apartment parent_apartment on parent_apartment.id = parent.apartment_id
WHERE
  n.created_at BETWEEN :from AND :to AND
n.type = :type AND n.property_id = :propertyId
ORDER BY n.id