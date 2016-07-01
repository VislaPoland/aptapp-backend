SELECT setval('hibernate_sequence', 99999);

-------------------
-- DEMO DATA
-------------------

-- administrator
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, created_at) VALUES ('Account', 108, true, 'Cool Company', 'Joe', 'Admin', '$2a$10$IXniWKP6M12dNGoTzHW9Feo/7Jn4nke0GeRGvvU7n38WuYkY40eyG','joe.admin@mail.com', '123456789', 'Administrator', '2016-06-16 15:36:38');

-- property owner
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, created_at) VALUES ('PropertyOwner', 1, true, 'South Water Apartments', 'Helen', 'Owner', '$2a$10$iNna7fvFEhY61WvFbc76Xe7ezV5uSHenCu/Yj8BS8x/d5yyxzd426', 'helen.owner@apartments.com', '(743) 635-5652', 'PropertyOwner', '2016-06-16 15:36:38');

-- address
INSERT INTO public.address (id, house_number, state, street_name, town, zip_code) VALUES (1, '747', 'IL', 'Main Street', 'Chicago', '606609');

-- property schedule
INSERT INTO public.property_schedule(id, start_hour, start_minute, end_hour, end_minute, slots_per_period, period_length) VALUES (1, 9, 0, 17, 0, 8, 30);

-- property
INSERT INTO public.property (id, name, status, address_id, owner_id, schedule_id, time_zone) VALUES (1, 'South Water Apartments', 'Active', 1, 1, 1, 'America/Chicago');

-- property contacts
INSERT INTO public.contact (id, communication_type, type, value) VALUES (1, 'Email', 'OtherUseful', 'southwater@apartments.com');
INSERT INTO public.contact (id, communication_type, type, value) VALUES (2, 'Phone', 'OtherUseful', '(785) 123-9653');

-- properties contacts
INSERT INTO
  public.contact(id, type, communication_type, value)
VALUES
  (3, 'Police', 'Phone', '1-541-754-3010'),
  (4, 'Police', 'Email', 'police@gmail.com'),
  (5, 'MedicalEmergency', 'Phone', '1-541-754-3010'),
  (6, 'MedicalEmergency', 'Email', 'medical@gmail.com'),
  (7, 'FireService', 'Phone', '1-541-754-3010'),
  (8, 'FireService', 'Email', 'fire@gmail.com')
;

INSERT INTO
  public.property_contacts(property_id, contacts_id)
VALUES
  (1, 3),
  (1, 4),
  (1, 5),
  (1, 6),
  (1, 7),
  (1, 8)
;

-- property contacts connections
INSERT INTO public.property_contacts (property_id, contacts_id) VALUES (1, 1);
INSERT INTO public.property_contacts (property_id, contacts_id) VALUES (1, 2);

-- property manager
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, managed_property_id, created_at) VALUES ('PropertyManager', 2, true, 'South Water Apartments', 'Mark', 'Building', '$2a$10$R6ntl54odFxGtmo/vFx.Kem91x5dXSVxjHLF7bCvHgpryYGShWUSq', 'mark.building@apartments.com', '(854) 253-6566', 'PropertyManager', 1, '2016-06-16 15:36:38');

-- employees
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, created_at, manager_id) VALUES ('Employee', 44, true, 'South Water Apartments', 'Martin', 'Security', '$2a$10$iNna7fvFEhY61WvFbc76Xe7ezV5uSHenCu/Yj8BS8x/d5yyxzd426', 'martin.security@apartments.com', '(743) 635-5651', 'Security', '2016-06-16 15:36:38', 2);
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, created_at, manager_id) VALUES ('Employee', 45, true, 'South Water Apartments', 'Martin', 'Maintenance', '$2a$10$iNna7fvFEhY61WvFbc76Xe7ezV5uSHenCu/Yj8BS8x/d5yyxzd426', 'martin.maintenance@apartments.com', '(743) 635-5651', 'Maintenance', '2016-06-16 15:36:38', 2);


-- apartments
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (11, '11', 1, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (12, '12', 1, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (13, '13', 1, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (14, '14', 1, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (15, '15', 1, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (16, '16', 1, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (17, '17', 1, 1);

INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (21, '21', 2, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (22, '22', 2, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (23, '23', 2, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (24, '24', 2, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (25, '25', 2, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (26, '26', 2, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (27, '27', 2, 1);

INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (31, '31', 3, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (32, '32', 3, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (33, '33', 3, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (34, '34', 3, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (35, '35', 3, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (36, '36', 3, 1);
INSERT INTO public.apartment (id, unit_number, floor, property_id) VALUES (37, '37', 3, 1);

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


UPDATE public.apartment SET right_id = 12, above_id = 21 WHERE id = 11;
UPDATE public.apartment SET left_id = 11, right_id = 13, above_id = 22 WHERE id = 12;
UPDATE public.apartment SET left_id = 12, right_id = 14, above_id = 23 WHERE id = 13;
UPDATE public.apartment SET left_id = 13, right_id = 15, above_id = 24 WHERE id = 14;
UPDATE public.apartment SET left_id = 14, right_id = 16, above_id = 25 WHERE id = 15;
UPDATE public.apartment SET left_id = 15, right_id = 17, above_id = 26 WHERE id = 16;
UPDATE public.apartment SET left_id = 16, above_id = 27 WHERE id = 17;

UPDATE public.apartment SET right_id = 22, below_id = 11, above_id = 31 WHERE id = 21;
UPDATE public.apartment SET left_id = 21, right_id = 23, below_id = 12, above_id = 32 WHERE id = 22;
UPDATE public.apartment SET left_id = 22, right_id = 24, below_id = 13, above_id = 33 WHERE id = 23;
UPDATE public.apartment SET left_id = 23, right_id = 25, below_id = 14, above_id = 34 WHERE id = 24;
UPDATE public.apartment SET left_id = 24, right_id = 26, below_id = 15, above_id = 35 WHERE id = 25;
UPDATE public.apartment SET left_id = 25, right_id = 27, below_id = 16, above_id = 36 WHERE id = 26;
UPDATE public.apartment SET left_id = 26, below_id = 17, above_id = 37 WHERE id = 27;

UPDATE public.apartment SET right_id = 32, below_id = 21 WHERE id = 31;
UPDATE public.apartment SET left_id = 31, right_id = 33, below_id = 22 WHERE id = 32;
UPDATE public.apartment SET left_id = 32, right_id = 34, below_id = 23 WHERE id = 33;
UPDATE public.apartment SET left_id = 33, right_id = 35, below_id = 24 WHERE id = 34;
UPDATE public.apartment SET left_id = 34, right_id = 36, below_id = 25 WHERE id = 35;
UPDATE public.apartment SET left_id = 35, right_id = 37, below_id = 26 WHERE id = 36;
UPDATE public.apartment SET left_id = 36, below_id = 27 WHERE id = 37;

-- tenant
INSERT INTO public.account (dtype, id, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, type, apartment_id, action_token, action_token_valid_until, created_at) VALUES ('Tenant', 3, false, 'South Water Apartments', 'John', 'Tenant', '$2a$10$pTLqZgRdpj/s.SP.ebNKauZXGOIOMxahdeKAswKgx24c7Q2YdLdCS', 'apt@test.com', '(905) 545-0256', 'Tenant', 'Owner', 22, '123456', '2017-06-16 15:36:38', '2016-06-16 15:36:38');

-------------------
-- /DEMO DATA
-------------------

