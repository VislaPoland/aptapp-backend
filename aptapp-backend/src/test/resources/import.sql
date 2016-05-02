CREATE EXTENSION IF NOT EXISTS postgis SCHEMA public;

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- Data for Name: Account; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (1, true, NULL, 'martin.litvaj@thinkcreatix.com', 'Martin', 'Litvaj', '$2a$04$SB0SU8RmJOVALq0IS01S7.oAMeyWWVZRSLWk/PE5ZHa/PbX8dt/MG', 'Administrator');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (2, true, NULL, 'tomas.sedlak@thinkcreatix.com', 'Tomas', 'Sedlak', '$2a$10$OxAKZlCNSiEAeRAGLOcUdu.I.tGUC1ejYBE4DcFb6eLW.ai.8Oapq', 'Trainer');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (5, false, NULL, 'sdf@kjhs.sdf', 'kjh', 'kjh', '$2a$10$kXa1dt/ubA.II/Y0fgfh7upkdeqm25nF6BhRmZYIsMB7NFI0.qR7.', 'GymManager');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (7, true, NULL, 'hg@jhg.dj', 'jhg', 'gh', '$2a$10$YBqtT1ddX0A/9pHA/RDhGecNHtaNkGOgPRwHLLfwdGaghrnEfGj3S', 'Trainer');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (9, true, NULL, 'hg@jhg.djsdf', 'jhg', 'gh', '$2a$10$Toli.U/iOZZFB3T41YO5uecFRxsHhxAEpfOwGfxW77J7j7ZpLlXV6', 'Trainer');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (10, true, NULL, 'jh2kj2@kj.sdf', 'jhg', 'g', '$2a$10$RCEX6Pck44kKw/Inant4ZuzeaSxYEbDl7h4c5i1kP5TSv0hm7dUta', 'Trainer');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (13, true, NULL, 'sdf@jhs.sd', 'kjh', 'kjh', '$2a$10$KV78/bjbxZjoZhx48iS7Q.3Xqn2jVUecUWL8SMZ/7dzY0Ah/sI.aO', 'Trainer');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (14, true, NULL, 'kjh@kjh.sdf', 'kjh', 'kjh', '$2a$10$2UNo1cj1PLeqlze6Ax.5hOgDrRtmRsJirGa.Oo7AC8OpStFy1oNqS', 'GymManager');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (16, true, NULL, 'lool@lool.com', 'sdf', 'jh', '$2a$10$AjwrcacLp9IpKp3o19S.peh2pVpwLZH4QeXNH/QK3SLJ8Sw.hsR7S', 'Trainer');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (17, true, NULL, 'g212@sdf.s', 'kjh', 'kjh', '$2a$10$qXVx4UWchayDQytFEHdPwuLCMH6so0YIB7qnoZCqrJBb8lxJsL0Du', 'Trainer');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (26, true, NULL, 'hasdasdg@jhg.sdfasdasd', 'jhg', 'jhg', '$2a$10$xvIPfzNTVnBwDKiqYfY7X.L./7kvC/vBrH3GBb9Rqngul0cafnsn.', 'GymManager');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (28, true, NULL, 'gh@jhg.sdf', 'jhg', 'jhg', '$2a$10$Yvxy3UesSIuyOU/xFmeNM.hthETfpyHTW0s3kDDxWjy3tmrWWEAeC', 'GymManager');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (38, true, NULL, '3245@post.sk', 'string', 'string', '$2a$10$TrfGMjRQcRESPxZJHyMaj.HuYb24UahxL0mD6YgI.iJYHhRg7SVWG', 'GymManager');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (42, true, NULL, 'kamahl19@gmail.com', 'Martin', 'Litvaj', '$2a$10$IL.fx3i55J.0grIjHOasQORC0nbSjJYsEXSlawC05P7WB1LGV/OKK', 'GymManager');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (3, true, NULL, 'lol@lol.com', 'dfga', 'kjh', '$2a$10$uAiLLn1QgSGxKU8Ov1GYHeGzIlFfDihezKfJolSkQpnrBpoeanC8S', 'GymManager');
INSERT INTO "Account" (id, active, "deleteDate", email, "firstName", "lastName", "passwordHash", role) VALUES (142, true, NULL, 'trainer@trainer.com', 'Aaa', 'Bbb', '$2a$10$gTW8m1E47y66cqucPNiSNecyGJNvOmQ8AXCfYXSWtdmbnSLQqlIqW', 'Trainer');


--
-- Data for Name: Gym; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "Gym" (id, "billRate", city, country, "deleteDate", "insuranceRequired", name, "position", state, street, zip, manager_id, "legalName", "stripeRecipientId", "timeZone") VALUES (6, 5, 'kjh', 'kjh', NULL, true, 'hg', '0101000020E6100000A439B2F2CB1C314087A5811FD5104840', 'jhg', 'jhg', 'jhg', 5, NULL, NULL, 'America/Los_Angeles');
INSERT INTO "Gym" (id, "billRate", city, country, "deleteDate", "insuranceRequired", name, "position", state, street, zip, manager_id, "legalName", "stripeRecipientId", "timeZone") VALUES (15, 54, 'kjh', 'kjhk', NULL, false, 'jhk', '0101000020E6100000A439B2F2CB1C314087A5811FD5104840', 'jhk', 'jhkj', 'hk', 14, NULL, NULL, 'America/Los_Angeles');
INSERT INTO "Gym" (id, "billRate", city, country, "deleteDate", "insuranceRequired", name, "position", state, street, zip, manager_id, "legalName", "stripeRecipientId", "timeZone") VALUES (27, 10, 'jhg', 'kjhg', NULL, false, 'sfdsfdsfd', '0101000020E6100000A439B2F2CB1C314087A5811FD5104840', 'kjhgkj', 'kjhgkj', 'hg', 26, NULL, NULL, 'America/Los_Angeles');
INSERT INTO "Gym" (id, "billRate", city, country, "deleteDate", "insuranceRequired", name, "position", state, street, zip, manager_id, "legalName", "stripeRecipientId", "timeZone") VALUES (29, 10, 'jhg', 'jhg', NULL, false, 'aaa', '0101000020E6100000A439B2F2CB1C314087A5811FD5104840', 'kjhg', 'kjhg', 'kjgh', 28, NULL, NULL, 'America/Los_Angeles');
INSERT INTO "Gym" (id, "billRate", city, country, "deleteDate", "insuranceRequired", name, "position", state, street, zip, manager_id, "legalName", "stripeRecipientId", "timeZone") VALUES (39, 0, 'string', 'string', NULL, true, 'hg', '0101000020E6100000A439B2F2CB1C314087A5811FD5104840', 'string', 'string', 'string', 38, NULL, NULL, 'America/Los_Angeles');
INSERT INTO "Gym" (id, "billRate", city, country, "deleteDate", "insuranceRequired", name, "position", state, street, zip, manager_id, "legalName", "stripeRecipientId", "timeZone") VALUES (43, 0, 'Bratislava', 'Slovensko', NULL, false, 'aaa', '0101000020E6100000A439B2F2CB1C314087A5811FD5104840', '', 'Stratena 7', '83106', 42, NULL, NULL, 'America/Los_Angeles');
INSERT INTO "Gym" (id, "billRate", city, country, "deleteDate", "insuranceRequired", name, "position", state, street, zip, manager_id, "legalName", "stripeRecipientId", "timeZone") VALUES (4, 555, 'kjh', 'kjh', NULL, true, 'aaaf', '0101000020E6100000A439B2F2CB1C314087A5811FD5104840', 'jgh', 'kjgh', 'kjgh', 3, 'hjk', 'rp_17ovRcGQsBuJEX4CoOyBtVJF', 'America/Los_Angeles');
INSERT INTO "Gym" (id, "billRate", city, country, "deleteDate", "insuranceRequired", name, "position", state, street, zip, manager_id, "legalName", "stripeRecipientId", "timeZone") VALUES (900, 10, 'Test132', 'kjh', NULL, true, 'aaaf', ST_GeomFromText('POINT(-87.629798 41.878114)', 4326), 'jgh', 'kjgh', 'kjgh', 3, 'hjk', NULL, 'America/Los_Angeles');


--
-- Data for Name: Amenity; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "Amenity" (id, name) VALUES (56, 'Gay sports');
INSERT INTO "Amenity" (id, name) VALUES (57, 'Barf training');
INSERT INTO "Amenity" (id, name) VALUES (58, 'Mala domov');


--
-- Data for Name: Gym_Amenity; Type: TABLE DATA; Schema: public; Owner: postgres
--
INSERT INTO "Gym_Amenity" ("Gym_id", "amenities_id") VALUES (900, 56);
INSERT INTO "Gym_Amenity" ("Gym_id", "amenities_id") VALUES (900, 57);


--
-- Data for Name: Slot; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (59, '2016-03-18 10:00:00', '2016-03-18 12:00:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (64, '2016-03-11 23:00:00', '2016-03-12 00:30:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (70, '2016-03-12 00:30:00', '2016-03-12 03:00:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (76, '2016-03-12 03:30:00', '2016-03-12 08:30:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (87, '2016-03-12 11:30:00', '2016-03-12 13:30:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (92, '2016-03-12 03:00:00', '2016-03-12 06:00:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (99, '2016-03-12 03:00:00', '2016-03-12 09:00:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (112, '2016-03-12 14:30:00', '2016-03-12 16:30:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (117, '2016-03-18 00:00:00', '2016-03-18 03:00:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (124, '2016-03-18 00:00:00', '2016-03-18 02:30:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (130, '2016-03-18 00:00:00', '2016-03-18 02:30:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (136, '2016-03-17 23:00:00', '2016-03-17 23:30:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (138, '2016-03-17 23:30:00', '2016-03-18 00:00:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (140, '2016-03-17 00:30:00', '2016-03-17 01:00:00', 30, 4);
INSERT INTO "Slot" (id, "beginTime", "endTime", "unitDurationMinutes", gym_id) VALUES (146, '2016-03-17 03:30:00', '2016-03-17 06:30:00', 30, 4);


--
-- Data for Name: Trainer; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "Trainer" (id, "phoneNumber", account_id, "stripeCustomerId") VALUES (143, '123', 142, 'cus_856wiU4KwiIqRP');


--
-- Data for Name: Reservation; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: SlotUnit; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (60, 10, 0, 59, 10, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (61, 10, 1, 59, 10, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (62, 10, 2, 59, 10, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (63, 10, 3, 59, 10, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (65, 4, 0, 64, 4, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (66, 4, 1, 64, 4, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (67, 4, 2, 64, 4, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (71, 2, 0, 70, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (72, 2, 1, 70, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (73, 2, 2, 70, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (74, 2, 3, 70, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (75, 2, 4, 70, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (77, 2, 0, 76, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (78, 2, 1, 76, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (79, 2, 2, 76, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (80, 2, 3, 76, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (81, 2, 4, 76, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (82, 2, 5, 76, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (83, 2, 6, 76, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (84, 2, 7, 76, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (85, 2, 8, 76, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (86, 2, 9, 76, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (88, 2, 0, 87, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (89, 2, 1, 87, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (90, 2, 2, 87, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (91, 2, 3, 87, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (93, 1, 0, 92, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (94, 1, 1, 92, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (95, 1, 2, 92, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (96, 1, 3, 92, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (97, 1, 4, 92, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (98, 1, 5, 92, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (100, 6, 0, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (101, 6, 1, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (102, 6, 2, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (103, 6, 3, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (104, 6, 4, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (105, 6, 5, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (106, 6, 6, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (107, 6, 7, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (108, 6, 8, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (109, 6, 9, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (110, 6, 10, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (111, 6, 11, 99, 6, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (113, 2, 0, 112, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (114, 2, 1, 112, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (115, 2, 2, 112, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (116, 2, 3, 112, 2, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (118, 3, 0, 117, 3, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (119, 3, 1, 117, 3, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (120, 3, 2, 117, 3, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (121, 3, 3, 117, 3, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (122, 3, 4, 117, 3, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (123, 3, 5, 117, 3, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (125, 1, 0, 124, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (126, 1, 1, 124, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (127, 1, 2, 124, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (128, 1, 3, 124, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (129, 1, 4, 124, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (131, 1, 0, 130, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (132, 1, 1, 130, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (133, 1, 2, 130, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (134, 1, 3, 130, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (135, 1, 4, 130, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (137, 1, 0, 136, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (139, 1, 0, 138, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (141, 1, 0, 140, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (147, 1, 0, 146, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (148, 1, 1, 146, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (149, 1, 2, 146, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (150, 1, 3, 146, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (151, 1, 4, 146, 1, 0);
INSERT INTO "SlotUnit" (id, capacity, "offset", slot_id, "initialCapacity", "version") VALUES (152, 1, 5, 146, 1, 0);


--
-- Data for Name: Reservation_SlotUnit; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: Slot_Amenity; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (59, 56);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (59, 57);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (64, 57);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (64, 58);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (70, 57);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (76, 57);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (87, 57);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (92, 57);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (99, 58);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (112, 56);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (112, 57);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (112, 58);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (117, 57);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (124, 57);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (130, 58);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (136, 58);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (138, 56);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (140, 57);
INSERT INTO "Slot_Amenity" ("Slot_id", amenities_id) VALUES (146, 57);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('hibernate_sequence', 152, true);


INSERT INTO "Reservation"(id, "beginTime", capacity, "durationMinutes", note, "paymentStatus", price, "stripeChargeId", version, slot_id, trainer_id) VALUES (1, '2016-04-01 13:30', 2, 90, '', 'Paid', 45.99, 'randomstripechargeid', 0, 136, 143);
INSERT INTO "Reservation"(id, "beginTime", capacity, "durationMinutes", note, "paymentStatus", price, "stripeChargeId", version, slot_id, trainer_id) VALUES (2, '2016-04-01 16:30', 2, 90, '', 'Paid', 45.99, 'randomstripechargeid2', 0, 136, 143);


--
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- PostgreSQL database dump complete
--
