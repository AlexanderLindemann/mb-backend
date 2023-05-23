INSERT INTO public.bank (id, name, order_number, logo_attachment_id, created_at,updated_at, created_by, updated_by)
VALUES ('0c371042-d848-11ed-afa1-0242ac120002'::UUID, 'aTestBank1', 40, NULL::BIGINT, '2023-04-12 14:38:37.000000', null, 6666, null),
       ('1fd0708a-d848-11ed-afa1-0242ac120002'::UUID, 'bTestBank2', 30, NULL::BIGINT, '2023-04-12 14:38:37.000000', null, 6666, null),
       ('2708daa4-d848-11ed-afa1-0242ac120002'::UUID, 'cTestBank3', 20, NULL::BIGINT, '2023-04-12 14:38:37.000000', null, 6666, null),
       ('2ae84592-d848-11ed-afa1-0242ac120002'::UUID, 'dTestBank4', 10, NULL::BIGINT, '2023-04-12 14:38:37.000000', null, 6666, null);

INSERT INTO public.bank_contact (id, full_name, email, bank_id)
VALUES ('48d91494-d868-11ed-afa1-0242ac120002'::UUID, 'Alexander Testovich', 'test-email@gmail.com','0c371042-d848-11ed-afa1-0242ac120002'::UUID),
       ('09bdec64-dabe-11ed-afa1-0242ac120002'::UUID, 'Alexander Testovich2', 'test-email@gmail2.com','0c371042-d848-11ed-afa1-0242ac120002'::UUID);

INSERT INTO public.credit_parameter (id, min_mortgage_sum, max_mortgage_sum, min_credit_term, max_credit_term, min_down_payment, max_down_payment, is_maternal_capital)
VALUES ('090ec5f2-d926-11ed-afa1-0242ac120002', 5000000, 20000000, 36, 360, 20, 100, true),
       ('b906eca8-d928-11ed-afa1-0242ac120002', 7000000, 25000000, 36, 360, 30, 100, false),
       ('b6e7ac3c-dabd-11ed-afa1-0242ac120002', 1000000, 30000000, 20, 240, 10, 90, true);

INSERT INTO public.credit_program_detail (id, credit_purpose_type, real_estate_type, include, exclude)
VALUES ('98a78d76-d925-11ed-afa1-0242ac120002', 'PURCHASE_READY_HOUSE,PURCHASE_UNDER_CONSTRUCTION', 'TOWNHOUSE,APARTMENT', null, 'ROSTOV,PRIMORSKY,PSKOV'),
       ('a0e076e4-d928-11ed-afa1-0242ac120002', 'PURCHASE_READY_HOUSE,PURCHASE_UNDER_CONSTRUCTION', 'TOWNHOUSE,APARTMENT', null, 'ROSTOV,PRIMORSKY,PSKOV'),
       ('eb3d875e-dabd-11ed-afa1-0242ac120002', 'PURCHASE_READY_HOUSE,PURCHASE_UNDER_CONSTRUCTION', 'APARTMENT', 'MOSCOW_REGION,MOSCOW', null);

INSERT INTO public.credit_program (id, credit_program_name, start_program_date, end_program_date, description, full_description, credit_program_detail_id, credit_parameter_id, base_rate, bank_id, created_at, updated_at, created_by, updated_by)
VALUES ('bfda8d66-d926-11ed-afa1-0242ac120002', 'testCreditProgramName1', '2023-04-12 14:38:37.000000', '2023-11-12 14:38:39.000000', 'testDescription1', 'testFullDescription1', '98a78d76-d925-11ed-afa1-0242ac120002', '090ec5f2-d926-11ed-afa1-0242ac120002', 15.00, '0c371042-d848-11ed-afa1-0242ac120002', '2023-04-12 14:38:37.000000', null, 6666, null),
       ('8222cb80-d928-11ed-afa1-0242ac120002', 'testCreditProgramName2', '2023-04-12 14:38:37.000000', '2023-11-12 14:38:39.000000', 'testDescription2', 'testFullDescription2', 'a0e076e4-d928-11ed-afa1-0242ac120002', 'b906eca8-d928-11ed-afa1-0242ac120002', 20.00, '0c371042-d848-11ed-afa1-0242ac120002', '2023-04-12 14:38:37.000000', null, 6666, null),
       ('4a8d6fe4-dabe-11ed-afa1-0242ac120002', 'testCreditProgramName3', '2023-04-12 14:38:37.000000', '2023-11-12 14:38:39.000000', 'testDescription3', 'testFullDescription3', 'eb3d875e-dabd-11ed-afa1-0242ac120002', 'b6e7ac3c-dabd-11ed-afa1-0242ac120002', 12.00, '1fd0708a-d848-11ed-afa1-0242ac120002', '2023-04-12 14:38:37.000000', null, 6666, null);

INSERT INTO public.partner (id, smart_deal_organization_id, name, type, real_estate_type, credit_purpose_type)
VALUES ('5fec2326-d92e-11ed-afa1-0242ac120002', 2633, 'testName', 'DEVELOPER', 'APARTMENT,APARTMENT_COMPLEX', 'PURCHASE_UNDER_CONSTRUCTION,REFINANCING'),
       ('dce73f3e-f2db-11ed-a05b-0242ac120003', 2634, 'testName2', 'DEVELOPER', 'APARTMENT,APARTMENT_COMPLEX', 'PURCHASE_UNDER_CONSTRUCTION,REFINANCING');

INSERT INTO public.real_estate (id, residential_complex_name, region, address, partner_id)
VALUES ('2b8850b2-d930-11ed-afa1-0242ac120002', 'testResidentialComplexName1', 'VORONEZH', 'testAddress1', '5fec2326-d92e-11ed-afa1-0242ac120002'),
       ('51faca54-d930-11ed-afa1-0242ac120002', 'testResidentialComplexName2', 'CHUKOTKA', 'testAddress2', '5fec2326-d92e-11ed-afa1-0242ac120002'),
       ('bd80be0e-f2db-11ed-a05b-0242ac120003', 'testResidentialComplexName3', 'MOSCOW', 'testAddress3', '5fec2326-d92e-11ed-afa1-0242ac120002');


INSERT INTO public.partner_credit_program (partner_id, credit_program_id)
VALUES ('5fec2326-d92e-11ed-afa1-0242ac120002', 'bfda8d66-d926-11ed-afa1-0242ac120002'),
       ('5fec2326-d92e-11ed-afa1-0242ac120002', '8222cb80-d928-11ed-afa1-0242ac120002'),
       ('5fec2326-d92e-11ed-afa1-0242ac120002', '4a8d6fe4-dabe-11ed-afa1-0242ac120002'),
       ('dce73f3e-f2db-11ed-a05b-0242ac120003', '4a8d6fe4-dabe-11ed-afa1-0242ac120002');

INSERT INTO public.partner_application (id, credit_purpose_type, real_estate_type, real_estate_id, partner_id, created_at, updated_at, created_by, updated_by, is_active)
VALUES ('5ff4b32c-f967-4cb1-8705-7470a321fe34', 'PURCHASE_UNDER_CONSTRUCTION', 'APARTMENT', '2b8850b2-d930-11ed-afa1-0242ac120002', '5fec2326-d92e-11ed-afa1-0242ac120002', '2023-05-05 12:12:22.802637', '2023-05-05 12:12:22.802637', 2966, null, true),
       ('7addcbef-c1e0-4de1-adeb-377f864efcfa', 'PURCHASE_UNDER_CONSTRUCTION', 'APARTMENT', '2b8850b2-d930-11ed-afa1-0242ac120002', 'dce73f3e-f2db-11ed-a05b-0242ac120003', '2023-05-05 12:12:22.802637', '2023-05-05 12:12:22.802637', 2962, null, true);

INSERT INTO public.borrower_profile (id, partner_application_id, first_name, last_name, middle_name, phone_number, email, created_at, updated_at, created_by, updated_by, is_active)
VALUES ('1348b508-f476-11ed-a05b-0242ac120003', '5ff4b32c-f967-4cb1-8705-7470a321fe34', 'Ivan', 'Ivanov', 'Ivanovich', '+90000000000', 'test@test.com', '2023-05-17 13:46:26.000000', null, 0, null, true),
       ('7cb535d6-f92e-11ed-be56-0242ac120002', '7addcbef-c1e0-4de1-adeb-377f864efcfa', 'Petr', 'Petrov', 'Petrovich', '+90000000000', 'test2@test.com', '2023-05-17 13:46:26.000000', null, 0, null, true);

INSERT INTO public.bank_application (id, credit_program_id, application_status, monthly_payment, down_payment, credit_term, overpayment, partner_application_id, created_at, updated_at, created_by, updated_by, is_active, main_borrower)
VALUES ('3b339aa4-5462-485a-9118-5922cd948566', 'bfda8d66-d926-11ed-afa1-0242ac120002', 'UPLOADING_DOCUMENTS', 60000, 500000, 120, 6000000, '5ff4b32c-f967-4cb1-8705-7470a321fe34', '2023-05-09 14:35:58.039296', '2023-05-09 14:35:58.039296', 2929, null, true, '1348b508-f476-11ed-a05b-0242ac120003'),
       ('f5cdbc9c-f53f-11ed-a05b-0242ac120003', 'bfda8d66-d926-11ed-afa1-0242ac120002', 'UPLOADING_DOCUMENTS', 60000, 500000, 120, 6000000, '5ff4b32c-f967-4cb1-8705-7470a321fe34', '2023-05-09 14:35:58.039296', '2023-05-09 14:35:58.039296', 2929, null, true, '7cb535d6-f92e-11ed-be56-0242ac120002');

