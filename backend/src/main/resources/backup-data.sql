SELECT setval('hibernate_sequence', 100);

-- TODO remove later, only for early development

-- administrator
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash,
                            primary_email, primary_phone, role)
VALUES ('Account', 8, true, 'Cool Company', 'Joe', 'Admin', '$2a$10$IXniWKP6M12dNGoTzHW9Feo/7Jn4nke0GeRGvvU7n38WuYkY40eyG',
        'joe.admin@mail.com', '123456789', 'Administrator');

-- property owner
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash,
                            primary_email, primary_phone, role)
VALUES ('PropertyOwner', 1, true, 'Cool Company', 'Joe', 'Owner', '$2a$10$IXniWKP6M12dNGoTzHW9Feo/7Jn4nke0GeRGvvU7n38WuYkY40eyG',
                         'joe.owner@mail.com', '123456789', 'PropertyOwner');

INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash,
                            primary_email, primary_phone, role)
VALUES ('PropertyOwner', 9, true, 'Cool Company', 'Joe', 'Owner2', '$2a$10$IXniWKP6M12dNGoTzHW9Feo/7Jn4nke0GeRGvvU7n38WuYkY40eyG',
        'joe.owner2@mail.com', '123456789', 'PropertyOwner');

-- addresses
INSERT INTO public.address (id, house_number, state, street_name, town, zip_code) VALUES (1, '111', 'MA', 'Street1', 'Boston', '1111111');
INSERT INTO public.address (id, house_number, state, street_name, town, zip_code) VALUES (2, '222', 'MA', 'Street2', 'Boston', '2222222');

-- property schedules
INSERT INTO public.property_schedule(id, start_hour, start_minute, end_hour, end_minute, slots_count) VALUES (1, 9, 0, 17, 0, 8);

-- properties
INSERT INTO public.property (id, name, status, address_id, owner_id, schedule_id) VALUES (1, 'Test property 1', 'Active', 1, 1, 1);
INSERT INTO public.property (id, name, status, address_id, owner_id, schedule_id) VALUES (2, 'Test property 2', 'Active', 2, 9, 1);

-- apartments
INSERT INTO public.apartment (id, unit_number, property_id)
VALUES (1, '1', 1);
INSERT INTO public.apartment (id, unit_number, below_apartment_id, property_id)
VALUES (2, '2', 1, 1);
UPDATE public.apartment SET above_apartment_id = 2 WHERE id = 1;

-- property manager
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash,
                            primary_email, primary_phone, role, managed_property_id)
VALUES ('PropertyManager', 2, true, 'Cool Company', 'Joe', 'Manager1', '$2a$10$M9hvOTL71BvwzoClkcOdYOarTyWIuirEFjV/08EOh7BvNpV.Ew9mG',
                           'joe.manager1@mail.com', '123456789', 'PropertyManager', 1);
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash,
                            primary_email, primary_phone, role, managed_property_id)
VALUES ('PropertyManager', 3, true, 'Cool Company', 'Joe', 'Manager2', '$2a$10$djKfGlbkHPoD2FVM3DvRn.mfKR7OkTquEJyubIc.mJi3dBusOY4Ty',
                           'joe.manager2@mail.com', '123456789', 'PropertyManager', 2);

-- security account
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash,
                            primary_email, primary_phone, role, manager_id)
VALUES ('Employee', 4, true, 'Cool Company', 'Joe', 'Security', '$2a$10$uxh1J.QHtM5iiVoUEIA46.Ndt/Srp71L.0kn8x8bIAE0/kk22AeLK',
                    'joe.security@mail.com', '123456789', 'Security', 2);

-- maintenance account
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash,
                            primary_email, primary_phone, role, manager_id)
VALUES ('Employee', 5, true, 'Cool Company', 'Joe', 'Maintenance', '$2a$10$pjJnFRFriSqLgv9nEi8jWuW/BKk4xnNqyNJPUx5ietHXt3WWetorS',
                    'joe.maintenance@mail.com', '123456789', 'Maintenance', 2);

-- tenants
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash,
                            primary_email, primary_phone, role, type, apartment_id)
VALUES ('Tenant', 6, true, 'Cool Company', 'Joe', 'Tenant1', '$2a$10$/gP.lBHwosvJepgBZimscODeBXghvua0g.85U3HNic6UMsTxfzArq',
                  'joe.tenant1@mail.com', '123456789', 'Tenant', 'Owner', 1);
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash,
                            primary_email, primary_phone, role, type, apartment_id)
VALUES ('Tenant', 7, true, 'Cool Company', 'Joe', 'Tenant2', '$2a$10$N/AhcCcImjmbsPRy4oDfOukA3.6xFE95qKoHAXEJbjOCJ3.JFPgZa',
                  'joe.tenant2@mail.com', '123456789', 'Tenant', 'Owner', 2);

INSERT INTO public.notification (dtype, id, date, title, status, description, type, author_id, created_at)
VALUES ('SecurityNotification', 1, '2016-06-16 15:36:38', 'Security not', 'Pending', 'Security test notification title', 'Security', 6, '2016-06-16 15:36:38');
INSERT INTO public.notification (dtype, id, date, title, status, description, access_if_not_at_home, target_apartment_id, type, author_id, created_at)
VALUES ('MaintenanceNotification', 2, '2016-06-16 15:36:38', 'Maintenance not', 'Pending', 'Maintenance test notification title', true, 2, 'Maintenance', 2, '2016-06-16 15:36:38');
INSERT INTO public.notification (dtype, id, date, title, status, description, target_apartment_id, type, author_id, created_at)
VALUES ('NeighborhoodNotification', 3, '2016-06-19 15:36:38', 'Neighborhood not', 'Pending', 'Neighborhood test notification title', 2, 'Neighborhood', 2, '2016-06-16 15:36:38');

