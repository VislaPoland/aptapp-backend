SELECT setval('hibernate_sequence', 99999);

-------------------
-- DEMO DATA
-------------------

-- administrator
INSERT INTO public.account (dtype, id, active, first_name, last_name, password_hash, primary_email, primary_phone, role, created_at) VALUES ('Account', 108, true, 'Joe', 'Admin', '$2a$10$IXniWKP6M12dNGoTzHW9Feo/7Jn4nke0GeRGvvU7n38WuYkY40eyG','joe.admin@mail.com', '123456789', 'Administrator', '2016-06-16 15:36:38');

-- property owner
INSERT INTO public.account (dtype, id, active, first_name, last_name, password_hash, primary_email, primary_phone, role, created_at) VALUES ('PropertyOwner', 1, true, 'Helen', 'Owner', '$2a$10$iNna7fvFEhY61WvFbc76Xe7ezV5uSHenCu/Yj8BS8x/d5yyxzd426', 'helen.owner@apartments.com', '(743) 635-5652', 'PropertyOwner', '2016-06-16 15:36:38');

-- address
INSERT INTO public.address (id, house_number, state, street_name, town, zip_code) VALUES (1, '747', 'IL', 'Main Street', 'Chicago', '606609');

-- property schedule
INSERT INTO public.maintenance_slot_schedule(id, initial_capacity, unit_duration_minutes) VALUES (1, 1, 30);

-- property
INSERT INTO public.property (id, name, status, address_id, owner_id, schedule_id, time_zone, enable_sms) VALUES (1, 'South Water Apartments', 'Active', 1, 1, 1, 'America/Chicago', true);

-- business profile categories
INSERT INTO public.business_category (id, name) VALUES(1, 'GENERAL');

INSERT INTO public.business_contact (id, communication_type, house_number, state, street, zip_code) VALUES (1, 'Phone', '50/1', 'Ilava', 'Sesame street', '018 51');

INSERT INTO public.business_profile(id, name, description, lat, lng, contact_id, property_id) VALUES (1, 'First business profile', 'Description of business profile', 1.4554, 15.677, 1, 1);

-- property contacts
INSERT INTO public.contact (id, communication_type, type, value) VALUES (1, 'Email', 'OtherUseful', 'southwater@apartments.com');
INSERT INTO public.contact (id, communication_type, type, value) VALUES (2, 'Phone', 'OtherUseful', '(785) 123-9653');

-- properties contacts
INSERT INTO public.contact(id, type, communication_type, value) VALUES (3, 'Police', 'Phone', '1-541-754-3010'),(4, 'Police', 'Email', 'police@gmail.com'),(5, 'MedicalEmergency', 'Phone', '1-541-754-3010'),(6, 'MedicalEmergency', 'Email', 'medical@gmail.com'),(7, 'FireService', 'Phone', '1-541-754-3010'),(8, 'FireService', 'Email', 'fire@gmail.com');

INSERT INTO public.property_contacts(property_id, contacts_id) VALUES (1, 3),(1, 4),(1, 5),(1, 6),(1, 7),(1, 8);

-- property contacts connections
INSERT INTO public.property_contacts (property_id, contacts_id) VALUES (1, 1);
INSERT INTO public.property_contacts (property_id, contacts_id) VALUES (1, 2);

-- property manager
INSERT INTO public.account (dtype, id, active, first_name, last_name, password_hash, primary_email, primary_phone, role, managed_property_id, created_at) VALUES ('PropertyManager', 2, true, 'Mark', 'Building', '$2a$10$R6ntl54odFxGtmo/vFx.Kem91x5dXSVxjHLF7bCvHgpryYGShWUSq', 'mark.building@apartments.com', '(854) 253-6566', 'PropertyManager', 1, '2016-06-16 15:36:38');

-- employees
INSERT INTO public.account (dtype, id, active, first_name, last_name, password_hash, primary_email, primary_phone, role, created_at, manager_id) VALUES ('SecurityEmployee', 44, true, 'Martin', 'Security', '$2a$10$iNna7fvFEhY61WvFbc76Xe7ezV5uSHenCu/Yj8BS8x/d5yyxzd426', 'martin.security@apartments.com', '(743) 635-5651', 'Security', '2016-06-16 15:36:38', 2);
INSERT INTO public.account (dtype, id, active, first_name, last_name, password_hash, primary_email, primary_phone, role, created_at, manager_id) VALUES ('MaintenanceEmployee', 45, true, 'Martin', 'Maintenance', '$2a$10$iNna7fvFEhY61WvFbc76Xe7ezV5uSHenCu/Yj8BS8x/d5yyxzd426', 'martin.maintenance@apartments.com', '(743) 635-5651', 'Maintenance', '2016-06-16 15:36:38', 2);


-- apartments
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (11, '11', '1', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (12, '12', '1', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (13, '13', '1', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (14, '14', '1', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (15, '15', '1', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (16, '16', '1', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (17, '17', '1', 1);

INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (21, '21', '2', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (22, '22', '2', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (23, '23', '2', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (24, '24', '2', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (25, '25', '2', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (26, '26', '2', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (27, '27', '2', 1);

INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (31, '31', '3', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (32, '32', '3', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (33, '33', '3', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (34, '34', '3', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (35, '35', '3', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (36, '36', '3', 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (37, '37', '3', 1);

INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (11, '11', 11);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (12, '12', 12);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (13, '13', 13);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (14, '14', 14);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (15, '15', 15);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (16, '16', 16);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (17, '17', 17);

INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (21, '21', 21);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (22, '22', 22);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (23, '23', 23);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (24, '24', 24);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (25, '25', 25);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (26, '26', 26);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (27, '27', 27);

INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (31, '31', 31);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (32, '32', 32);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (33, '33', 33);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (34, '34', 34);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (35, '35', 35);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (36, '36', 36);
INSERT INTO public.apartment_neighbor (id, unit_number, apartment_id) VALUES (37, '37', 37);


UPDATE public.apartment SET right_id = 21, above_id = 31 WHERE id = 11;
UPDATE public.apartment SET left_id = 11, right_id = 22, above_id = 32 WHERE id = 12;
UPDATE public.apartment SET left_id = 12, right_id = 23, above_id = 33 WHERE id = 13;
UPDATE public.apartment SET left_id = 13, right_id = 24, above_id = 34 WHERE id = 14;
UPDATE public.apartment SET left_id = 14, right_id = 25, above_id = 35 WHERE id = 15;
UPDATE public.apartment SET left_id = 15, right_id = 26, above_id = 36 WHERE id = 16;
UPDATE public.apartment SET left_id = 16, right_id = 27, above_id = 37 WHERE id = 17;
UPDATE public.apartment SET below_id = 11 WHERE id = 31;
UPDATE public.apartment SET left_id = 12 WHERE id = 22;


-- tenant
INSERT INTO public.account (dtype, id, active, first_name, last_name, password_hash, primary_email, primary_phone, role, apartment_id, action_token, action_token_valid_until, created_at, enable_sms) VALUES ('Tenant', 3, true, 'John', 'Tenant', '$2a$10$pTLqZgRdpj/s.SP.ebNKauZXGOIOMxahdeKAswKgx24c7Q2YdLdCS', 'apt@test.com', '(905) 545-0256', 'Tenant', 22, '123456', '2017-06-16 15:36:38', '2016-06-16 15:36:38', false);
INSERT INTO public.account (dtype, id, active, first_name, last_name, password_hash, primary_email, primary_phone, role, apartment_id, action_token, action_token_valid_until, created_at, enable_sms) VALUES ('Tenant', 451, true, 'Tomas', 'Sedlak', '$2a$10$pTLqZgRdpj/s.SP.ebNKauZXGOIOMxahdeKAswKgx24c7Q2YdLdCS', 'tomas.sedlak@thinkcreatix.com', '+421948519283', 'Tenant', 21, null, null, '2016-06-16 15:36:38', true);
INSERT INTO public.account (dtype, id, active, first_name, last_name, password_hash, primary_email, primary_phone, role, apartment_id, action_token, action_token_valid_until, created_at, enable_sms) VALUES ('Tenant', 434, false, 'Peter', 'Pan', null, 'inactive.tenant@test.com', '', 'Tenant', 33, null, null, '2016-06-16 15:36:38', true);
INSERT INTO public.account (dtype, id, active, first_name, last_name, password_hash, primary_email, primary_phone, role, apartment_id, action_token, action_token_valid_until, created_at, enable_sms) VALUES ('Tenant', 4, false, 'Test', 'Tenant', '$2a$10$pTLqZgRdpj/s.SP.ebNKauZXGOIOMxahdeKAswKgx24c7Q2YdLdCS', 'apt3@test.com', '(905) 545-0256', 'Tenant', 33, '1234567', '2017-06-16 15:36:38', '2016-06-16 15:36:38', false);
INSERT INTO public.account (dtype, id, active, first_name, last_name, password_hash, primary_email, primary_phone, role, apartment_id, action_token, action_token_valid_until, created_at, enable_sms, parent_tenant_id) VALUES ('SubTenant', 133245, true, 'Alan', 'SubTenant', '$2a$10$pTLqZgRdpj/s.SP.ebNKauZXGOIOMxahdeKAswKgx24c7Q2YdLdCS', 'apt2@test.com', '(901) 222-3213', 'SubTenant', 22, null, null, '2016-06-16 15:36:38', true, 3);

INSERT INTO public.notification_group (id, created_at) VALUES (1, '2016-02-01 18:30:00');

-- notification
INSERT INTO public.notification (dtype, id, created_at, deleted_at, description, response, status, title, type, updated_at, access_if_not_at_home, author_id, property_id, target_apartment_id, notification_group_id) VALUES ('MaintenanceNotification', 1, '2016-07-07 16:00:00', null, null, null, 'Pending', 'Maint. test 1', 'Maintenance', '2016-07-07 16:00:00', false, 3, 1, 22, 1);

-- slots
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title, audience) VALUES ('EventSlot', 100, '2016-02-01 18:30:00', '2016-02-01 19:00:00', 30, 1, null, 'Event slot 1', 'Event 1', 'Everyone');
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title, audience) VALUES ('EventSlot', 101, '2016-02-01 19:00:00', '2016-02-01 19:30:00', 30, 1, null, 'Event slot 2', 'Event 2', 'Everyone');
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title, audience) VALUES ('EventSlot', 102, '2016-02-01 19:30:00', '2016-02-01 20:00:00', 30, 1, null, 'Event slot 3', 'Event 3', 'Everyone');
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title, audience) VALUES ('EventSlot', 103, '2016-02-02 20:00:00', '2016-02-02 20:30:00', 30, 1, null, 'Event slot 4', 'Event 4', 'Everyone');
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title, audience) VALUES ('EventSlot', 104, '2016-02-02 20:30:00', '2016-02-02 21:00:00', 30, 1, null, 'Event slot 5', 'Event 5', 'Everyone');
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title, audience) VALUES ('EventSlot', 105, '2016-02-03 21:00:00', '2016-02-03 21:30:00', 30, 1, null, 'Event slot 6', 'Event 6', 'Everyone');
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 200, '2016-02-01 18:30:00', '2016-02-01 19:00:00', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 201, '2016-02-01 19:00:00', '2016-02-01 19:30:00', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 202, '2016-02-01 19:30:00', '2016-02-01 20:00:00', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 203, '2016-02-02 20:00:00', '2016-02-02 20:30:00', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 204, '2016-02-02 20:30:00', '2016-02-02 21:00:00', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 205, '2016-02-03 21:00:00', '2016-02-03 21:30:00', 30, 1, null, null, null);

INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title, audience) VALUES ('EventSlot', 420, '2016-07-07 15:00:00.000000', '2016-07-07 18:00:00.000000', 180, 1, null, 'everybody is welcome', 'Domova schodza', 'Tenants');
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title, audience) VALUES ('EventSlot', 421, '2016-07-09 16:00:00.000000', '2016-07-09 17:00:00.000000', 60, 1, null, 'please bring adidas with at least 3 stripes', 'Slavsquat training', 'Everyone');
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title, audience) VALUES ('EventSlot', 422, '2016-07-09 18:00:00.000000', '2016-07-09 19:00:00.000000', 60, 1, null, 'only russian sabaka allowed', 'Dog walking competition', 'Employees');
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 455, '2016-07-11 15:00:00.000000', '2016-07-11 23:00:00.000000', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 458, '2016-07-16 13:30:00.000000', '2016-07-16 14:00:00.000000', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 472, '2016-07-18 15:00:00.000000', '2016-07-18 23:00:00.000000', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 489, '2016-07-25 15:00:00.000000', '2016-07-25 23:00:00.000000', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 506, '2016-08-01 15:00:00.000000', '2016-08-01 23:00:00.000000', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 523, '2016-08-08 15:00:00.000000', '2016-08-08 23:00:00.000000', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 540, '2016-08-15 15:00:00.000000', '2016-08-15 23:00:00.000000', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 557, '2016-08-22 15:00:00.000000', '2016-08-22 23:00:00.000000', 30, 1, null, null, null);
INSERT INTO public.slot (dtype, id, begin_time, end_time, unit_duration_minutes, property_id, schedule_id, description, title) VALUES ('MaintenanceSlot', 574, '2016-08-29 15:00:00.000000', '2016-08-29 23:00:00.000000', 30, 1, null, null, null);

-- reservations
INSERT INTO public.notification (dtype, id, created_at, deleted_at, description, response, status, title, type, updated_at, access_if_not_at_home, author_id, property_id, target_apartment_id, notification_group_id) VALUES ('MaintenanceNotification', 254, '2016-02-01 07:26:04', null, 'Somebody broke my window', '', 'Pending', 'Broken window', 'Maintenance', '2016-02-01 07:26:04', false, 451, 1, 21, 1);
INSERT INTO public.maintenance_reservation (id, begin_time, capacity, duration_minutes, end_time, note, version, employee_id, notification_id, slot_id, status) VALUES (1, '2016-02-01 19:00:00', 1, 30, '2016-02-01 19:30:00', 'Broken window', 1, 45, 254, 201, 'Confirmed');
INSERT INTO public.notification (dtype, id, created_at, deleted_at, description, response, status, title, type, updated_at, access_if_not_at_home, author_id, property_id, target_apartment_id, notification_group_id) VALUES ('MaintenanceNotification', 203, '2016-02-01 07:26:04', null, 'There is a gas leaking in my kitchen', '', 'Pending', 'Leaking gas', 'Maintenance', '2016-02-01 04:16:44', true, 3, 1, 22, 1);
INSERT INTO public.maintenance_reservation (id, begin_time, capacity, duration_minutes, end_time, note, version, employee_id, notification_id, slot_id, status) VALUES (2, '2016-02-01 19:00:00', 1, 30, '2016-02-01 19:30:00', 'Urgent', 1, 45, 203, 201, 'Pending');


-- slot units
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (471, 1, 1, 15, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (470, 1, 1, 14, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (469, 1, 1, 13, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (468, 1, 1, 12, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (467, 1, 1, 11, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (466, 1, 1, 10, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (465, 1, 1, 9, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (464, 1, 1, 8, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (463, 1, 1, 7, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (462, 1, 1, 6, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (461, 1, 1, 5, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (460, 1, 1, 4, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (459, 1, 1, 3, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (458, 1, 1, 2, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (457, 1, 1, 1, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (456, 1, 1, 0, 0, 455);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (488, 1, 1, 15, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (487, 1, 1, 14, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (486, 1, 1, 13, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (485, 1, 1, 12, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (484, 1, 1, 11, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (483, 1, 1, 10, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (482, 1, 1, 9, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (481, 1, 1, 8, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (480, 1, 1, 7, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (479, 1, 1, 6, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (478, 1, 1, 5, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (477, 1, 1, 4, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (476, 1, 1, 3, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (475, 1, 1, 2, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (474, 1, 1, 1, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (473, 1, 1, 0, 0, 472);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (505, 1, 1, 15, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (504, 1, 1, 14, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (503, 1, 1, 13, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (502, 1, 1, 12, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (501, 1, 1, 11, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (500, 1, 1, 10, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (499, 1, 1, 9, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (498, 1, 1, 8, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (497, 1, 1, 7, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (496, 1, 1, 6, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (495, 1, 1, 5, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (494, 1, 1, 4, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (493, 1, 1, 3, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (492, 1, 1, 2, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (491, 1, 1, 1, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (490, 1, 1, 0, 0, 489);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (522, 1, 1, 15, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (521, 1, 1, 14, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (520, 1, 1, 13, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (519, 1, 1, 12, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (518, 1, 1, 11, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (517, 1, 1, 10, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (516, 1, 1, 9, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (515, 1, 1, 8, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (514, 1, 1, 7, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (513, 1, 1, 6, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (512, 1, 1, 5, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (511, 1, 1, 4, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (510, 1, 1, 3, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (509, 1, 1, 2, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (508, 1, 1, 1, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (507, 1, 1, 0, 0, 506);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (539, 1, 1, 15, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (538, 1, 1, 14, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (537, 1, 1, 13, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (536, 1, 1, 12, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (535, 1, 1, 11, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (534, 1, 1, 10, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (533, 1, 1, 9, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (532, 1, 1, 8, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (531, 1, 1, 7, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (530, 1, 1, 6, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (529, 1, 1, 5, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (528, 1, 1, 4, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (527, 1, 1, 3, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (526, 1, 1, 2, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (525, 1, 1, 1, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (524, 1, 1, 0, 0, 523);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (556, 1, 1, 15, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (555, 1, 1, 14, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (554, 1, 1, 13, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (553, 1, 1, 12, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (552, 1, 1, 11, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (551, 1, 1, 10, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (550, 1, 1, 9, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (549, 1, 1, 8, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (548, 1, 1, 7, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (547, 1, 1, 6, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (546, 1, 1, 5, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (545, 1, 1, 4, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (544, 1, 1, 3, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (543, 1, 1, 2, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (542, 1, 1, 1, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (541, 1, 1, 0, 0, 540);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (573, 1, 1, 15, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (572, 1, 1, 14, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (571, 1, 1, 13, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (570, 1, 1, 12, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (569, 1, 1, 11, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (568, 1, 1, 10, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (567, 1, 1, 9, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (566, 1, 1, 8, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (565, 1, 1, 7, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (564, 1, 1, 6, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (563, 1, 1, 5, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (562, 1, 1, 4, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (561, 1, 1, 3, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (560, 1, 1, 2, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (559, 1, 1, 1, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (558, 1, 1, 0, 0, 557);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (590, 1, 1, 15, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (589, 1, 1, 14, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (588, 1, 1, 13, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (587, 1, 1, 12, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (586, 1, 1, 11, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (585, 1, 1, 10, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (584, 1, 1, 9, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (583, 1, 1, 8, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (582, 1, 1, 7, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (581, 1, 1, 6, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (580, 1, 1, 5, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (579, 1, 1, 4, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (578, 1, 1, 3, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (577, 1, 1, 2, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (576, 1, 1, 1, 0, 574);
INSERT INTO public.slot_unit (id, capacity, initial_capacity, "offset", version, slot_id) VALUES (575, 1, 1, 0, 0, 574);

-- assign tenant to apartment
UPDATE public.account SET apartment_id = 14 WHERE id = 451;
UPDATE public.account SET apartment_id = 13 WHERE id = 434;

-------------------
-- /DEMO DATA
-------------------

