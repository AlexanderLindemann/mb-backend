INSERT INTO public.bank (id, name, order_number, logo_attachment_id)
VALUES ('0c371042-d848-11ed-afa1-0242ac120002'::UUID, 'aTestBank1', 40, NULL::BIGINT),
       ('1fd0708a-d848-11ed-afa1-0242ac120002'::UUID, 'bTestBank2', 30, NULL::BIGINT),
       ('2708daa4-d848-11ed-afa1-0242ac120002'::UUID, 'cTestBank3', 20, NULL::BIGINT),
       ('2ae84592-d848-11ed-afa1-0242ac120002'::UUID, 'dTestBank4', 10, NULL::BIGINT);

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

INSERT INTO public.credit_program (id, credit_program_name, start_program_date, end_program_date, description, full_description, credit_program_detail_id, credit_parameter_id, base_rate, bank_id)
VALUES ('bfda8d66-d926-11ed-afa1-0242ac120002', 'testCreditProgramName1', '2023-04-12 14:38:37.000000', '2023-11-12 14:38:39.000000', 'testDescription1', 'testFullDescription1', '98a78d76-d925-11ed-afa1-0242ac120002', '090ec5f2-d926-11ed-afa1-0242ac120002', 15.00, '0c371042-d848-11ed-afa1-0242ac120002'),
       ('8222cb80-d928-11ed-afa1-0242ac120002', 'testCreditProgramName2', '2023-04-12 14:38:37.000000', '2023-11-12 14:38:39.000000', 'testDescription2', 'testFullDescription2', 'a0e076e4-d928-11ed-afa1-0242ac120002', 'b906eca8-d928-11ed-afa1-0242ac120002', 20.00, '0c371042-d848-11ed-afa1-0242ac120002'),
       ('4a8d6fe4-dabe-11ed-afa1-0242ac120002', 'testCreditProgramName3', '2023-04-12 14:38:37.000000', '2023-11-12 14:38:39.000000', 'testDescription3', 'testFullDescription3', 'eb3d875e-dabd-11ed-afa1-0242ac120002', 'b6e7ac3c-dabd-11ed-afa1-0242ac120002', 12.00, '1fd0708a-d848-11ed-afa1-0242ac120002');

INSERT INTO public.partner (id, smart_deal_organization_id, name, type, real_estate_type, credit_purpose_type)
VALUES ('5fec2326-d92e-11ed-afa1-0242ac120002', 2633, 'testName', 'DEVELOPER', 'APARTMENT,APARTMENT_COMPLEX', 'PURCHASE_UNDER_CONSTRUCTION,REFINANCING');

INSERT INTO public.real_estate (id, residential_complex_name, region, address, partner_id)
VALUES ('2b8850b2-d930-11ed-afa1-0242ac120002', 'testResidentialComplexName1', 'VORONEZH', 'testAddress1', '5fec2326-d92e-11ed-afa1-0242ac120002'),
       ('51faca54-d930-11ed-afa1-0242ac120002', 'testResidentialComplexName2', 'CHUKOTKA', 'testAddress2', '5fec2326-d92e-11ed-afa1-0242ac120002');

INSERT INTO public.partner_credit_program (partner_id, credit_program_id)
VALUES ('5fec2326-d92e-11ed-afa1-0242ac120002', 'bfda8d66-d926-11ed-afa1-0242ac120002'),
       ('5fec2326-d92e-11ed-afa1-0242ac120002', '8222cb80-d928-11ed-afa1-0242ac120002'),
       ('5fec2326-d92e-11ed-afa1-0242ac120002', '4a8d6fe4-dabe-11ed-afa1-0242ac120002');