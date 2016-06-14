-- TODO remove later, only for early development
-- property owner
INSERT INTO public.account (dtype, id, action_token, action_token_valid_until, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, secondary_email, secondary_phone, website, type, parent_tenant_id, apartment_unit_number) VALUES ('PropertyOwner', 1, null, null, true, 'Cool Company', 'Joe', 'Owner', '$2a$10$IXniWKP6M12dNGoTzHW9Feo/7Jn4nke0GeRGvvU7n38WuYkY40eyG', 'joe.owner@mail.com', '123456789', 'PropertyOwner', null, null, null, null, null, null);

-- property manager
INSERT INTO public.account (dtype, id, action_token, action_token_valid_until, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, secondary_email, secondary_phone, website, type, parent_tenant_id, apartment_unit_number) VALUES ('PropertyManager', 2, null, null, true, 'Cool Company', 'Joe', 'Manager1', '$2a$10$M9hvOTL71BvwzoClkcOdYOarTyWIuirEFjV/08EOh7BvNpV.Ew9mG', 'joe.manager1@mail.com', '123456789', 'PropertyManager', null, null, null, null, null, null);
INSERT INTO public.account (dtype, id, action_token, action_token_valid_until, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, secondary_email, secondary_phone, website, type, parent_tenant_id, apartment_unit_number) VALUES ('PropertyManager', 3, null, null, true, 'Cool Company', 'Joe', 'Manager2', '$2a$10$djKfGlbkHPoD2FVM3DvRn.mfKR7OkTquEJyubIc.mJi3dBusOY4Ty', 'joe.manager2@mail.com', '123456789', 'PropertyManager', null, null, null, null, null, null);

-- security account
INSERT INTO public.account (dtype, id, action_token, action_token_valid_until, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, secondary_email, secondary_phone, website, type, parent_tenant_id, apartment_unit_number) VALUES ('Account', 10, null, null, true, 'Cool Company', 'Joe', 'Security', '$2a$10$uxh1J.QHtM5iiVoUEIA46.Ndt/Srp71L.0kn8x8bIAE0/kk22AeLK', 'joe.security@mail.com', '123456789', 'Security', null, null, null, null, null, null);

-- maintenance account
INSERT INTO public.account (dtype, id, action_token, action_token_valid_until, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, secondary_email, secondary_phone, website, type, parent_tenant_id, apartment_unit_number) VALUES ('Account', 11, null, null, true, 'Cool Company', 'Joe', 'Maintenance', '$2a$10$pjJnFRFriSqLgv9nEi8jWuW/BKk4xnNqyNJPUx5ietHXt3WWetorS', 'joe.maintenance@mail.com', '123456789', 'Maintenance', null, null, null, null, null, null);

-- addresses
INSERT INTO public.address (id, house_number, state, street_name, town, zip_code) VALUES (4, '111', 'MA', 'Street1', 'Boston', '1111111');
INSERT INTO public.address (id, house_number, state, street_name, town, zip_code) VALUES (5, '222', 'MA', 'Street2', 'Boston', '222222');

-- properties
INSERT INTO public.property (id, additional_information, name, status, address_id, manager_id, owner_id) VALUES (6, 'Bla bla bla', 'Test property 1', 'Active', 4, 2, 1);
INSERT INTO public.property (id, additional_information, name, status, address_id, manager_id, owner_id) VALUES (7, 'Bla bla bla', 'Test property 2', 'Active', 5, 3, 1);

-- apartments
INSERT INTO public.apartment (unit_number, above_apartment_unit_number, behind_apartment_unit_number, below_apartment_unit_number, left_apartment_unit_number, opposite_apartment_unit_number, property_id, right_apartment_unit_number) VALUES ('2', null, null, null, null, null, 6, null);
INSERT INTO public.apartment (unit_number, above_apartment_unit_number, behind_apartment_unit_number, below_apartment_unit_number, left_apartment_unit_number, opposite_apartment_unit_number, property_id, right_apartment_unit_number) VALUES ('1', null, null, '2', null, null, 6, null);
UPDATE public.apartment SET above_apartment_unit_number = '1' WHERE unit_number = '2';

-- tenants
INSERT INTO public.account (dtype, id, action_token, action_token_valid_until, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, secondary_email, secondary_phone, website, type, parent_tenant_id, apartment_unit_number) VALUES ('Tenant', 8, null, null, true, 'Cool Company', 'Joe', 'Tenant1', '$2a$10$/gP.lBHwosvJepgBZimscODeBXghvua0g.85U3HNic6UMsTxfzArq', 'joe.tenant1@mail.com', '123456789', 'Tenant', null, null, null, 'Owner', null, '1');
INSERT INTO public.account (dtype, id, action_token, action_token_valid_until, active, company_name, first_name, last_name, password_hash, primary_email, primary_phone, role, secondary_email, secondary_phone, website, type, parent_tenant_id, apartment_unit_number) VALUES ('Tenant', 9, null, null, true, 'Cool Company', 'Joe', 'Tenant2', '$2a$10$N/AhcCcImjmbsPRy4oDfOukA3.6xFE95qKoHAXEJbjOCJ3.JFPgZa', 'joe.tenant2@mail.com', '123456789', 'Tenant', null, null, null, 'Owner', null, '2');