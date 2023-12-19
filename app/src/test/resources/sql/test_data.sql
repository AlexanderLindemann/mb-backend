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
       ('b906eca8-d928-11ed-afa1-0242ac120002', 7000000, 25000000, 36, 360, 30, 100, true),
       ('b6e7ac3c-dabd-11ed-afa1-0242ac120002', 1000000, 30000000, 20, 240, 10, 90, true);

INSERT INTO public.credit_program_detail (id, credit_purpose_type, real_estate_type, include, exclude)
VALUES ('98a78d76-d925-11ed-afa1-0242ac120002', 'PURCHASE_READY_HOUSE,PURCHASE_UNDER_CONSTRUCTION', 'TOWNHOUSE,APARTMENT', null, 'ROSTOV,PRIMORSKY,PSKOV'),
       ('a0e076e4-d928-11ed-afa1-0242ac120002', 'PURCHASE_READY_HOUSE,PURCHASE_UNDER_CONSTRUCTION', 'TOWNHOUSE,APARTMENT', null, 'ROSTOV,PRIMORSKY,PSKOV'),
       ('eb3d875e-dabd-11ed-afa1-0242ac120002', 'PURCHASE_READY_HOUSE,PURCHASE_UNDER_CONSTRUCTION', 'APARTMENT', 'MOSCOW_REGION,MOSCOW', null);

INSERT INTO public.credit_program (id, credit_program_name, start_program_date, end_program_date, description, full_description, credit_program_detail_id, credit_parameter_id, base_rate, bank_id, created_at, updated_at, created_by, updated_by, salary_client_interest_rate)
VALUES ('bfda8d66-d926-11ed-afa1-0242ac120002', 'testCreditProgramName1', '2023-04-12 14:38:37.000000', NOW() + INTERVAL '6 months', 'testDescription1', 'testFullDescription1', '98a78d76-d925-11ed-afa1-0242ac120002', '090ec5f2-d926-11ed-afa1-0242ac120002', 15.00, '0c371042-d848-11ed-afa1-0242ac120002', '2023-04-12 14:38:37.000000', null, 6666, null, -2.0),
       ('8222cb80-d928-11ed-afa1-0242ac120002', 'testCreditProgramName2', '2023-04-12 14:38:37.000000', NOW() + INTERVAL '6 months', 'testDescription2', 'testFullDescription2', 'a0e076e4-d928-11ed-afa1-0242ac120002', 'b906eca8-d928-11ed-afa1-0242ac120002', 20.00, '0c371042-d848-11ed-afa1-0242ac120002', '2023-04-12 14:38:37.000000', null, 6666, null, -3.0),
       ('4a8d6fe4-dabe-11ed-afa1-0242ac120002', 'testCreditProgramName3', '2023-04-12 14:38:37.000000', NOW() + INTERVAL '6 months', 'testDescription3', 'testFullDescription3', 'eb3d875e-dabd-11ed-afa1-0242ac120002', 'b6e7ac3c-dabd-11ed-afa1-0242ac120002', 12.00, '1fd0708a-d848-11ed-afa1-0242ac120002', '2023-04-12 14:38:37.000000', null, 6666, null, -4.0);

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

INSERT INTO public.mortgage_calculation (id, real_estate_price, down_payment, monthly_payment, credit_term, created_at, updated_at, created_by, updated_by, is_active, is_maternal_capital)
VALUES ('5690400c-0f32-11ee-be56-0242ac120002', 50000000, 1000000, 100000, 360, '2023-06-20 14:18:19.000000', '2023-06-20 14:18:22.000000', 6666, null, true, true),
       ('9cc7641a-0f32-11ee-be56-0242ac120002', 50000000, 1000000, 100000, 360, '2023-06-20 14:18:19.000000', '2023-06-20 14:18:22.000000', 6666, null, true, true);


INSERT INTO public.partner_application (id, credit_purpose_type, real_estate_types, real_estate_id, partner_id, created_at, updated_at, created_by, updated_by, is_active, mortgage_calculation_id, partner_application_status, maternal_capital_amount, subsidy_amount, payment_source, insurance)
VALUES ('5ff4b32c-f967-4cb1-8705-7470a321fe34', 'PURCHASE_UNDER_CONSTRUCTION', 'APARTMENT', '2b8850b2-d930-11ed-afa1-0242ac120002', '5fec2326-d92e-11ed-afa1-0242ac120002', '2023-05-05 12:12:22.802637', '2023-05-05 12:12:22.802637', 2966, null, true, '5690400c-0f32-11ee-be56-0242ac120002', 'UPLOADING_DOCS', null, null, null, null),
       ('7addcbef-c1e0-4de1-adeb-377f864efcfa', 'PURCHASE_UNDER_CONSTRUCTION', 'APARTMENT', '2b8850b2-d930-11ed-afa1-0242ac120002', 'dce73f3e-f2db-11ed-a05b-0242ac120003', '2023-05-05 12:12:22.802637', '2023-05-05 12:12:22.802637', 2962, null, true, '9cc7641a-0f32-11ee-be56-0242ac120002', 'UPLOADING_DOCS', null, null, null, null),
       ('f09e9c57-e110-4f55-9124-9fafba8a441c', 'PURCHASE_UNDER_CONSTRUCTION', 'APARTMENT', '51faca54-d930-11ed-afa1-0242ac120002', 'dce73f3e-f2db-11ed-a05b-0242ac120003', '2023-05-05 12:12:22.802637', '2023-05-05 12:12:22.802637', 2962, null, false, '9cc7641a-0f32-11ee-be56-0242ac120002', 'UPLOADING_DOCS', null, null, null, null),
       ('d573aeb5-ea8d-48e7-989c-82ce17e16ac2', 'PURCHASE_READY_HOUSE', 'APARTMENT_COMPLEX', 'bd80be0e-f2db-11ed-a05b-0242ac120003', '5fec2326-d92e-11ed-afa1-0242ac120002', '2023-05-05 12:12:22.802637', '2023-05-05 12:12:22.802637', 2962, null, true, '9cc7641a-0f32-11ee-be56-0242ac120002', 'UPLOADING_DOCS', null, null, null, null);

INSERT INTO public.borrower_employer (id, name, tin, branch, number_of_employees, organization_age, phone, site, work_experience, position, address, created_at, updated_at, created_by, updated_by, is_active, bank_details, manager, is_current_employer)
VALUES ('b0c5a48e-0a2b-4659-b408-33295b6ea968', 'ООО Супер компани', '3453454555', 'IT', 'OVER_100', 'OVER_5', '4223444332', 'suoer_cc.com', 'FROM_6_TO_12', 'Менеджер по клинингу', 'Модный офис', '2023-10-18 12:07:33.619338', '2023-10-18 12:08:31.499206', 2222, 2222, true, null, null, null),
       ('919889ca-2f87-4a56-a33e-5e0cccff0857', 'ООО Ромашка', '523523523523', 'MASSMEDIA', 'FROM_50_TO_100', 'OVER_5', '2323523523', null, 'FROM_6_TO_12', 'Повар', 'Калуга', '2023-10-06 16:08:45.987870', '2023-10-06 16:09:38.827050', 2222, 2222, true, null, null, null);

INSERT INTO public.url_mapping (id, short_link, full_link)
VALUES ('9f8b7286-33f3-4600-9471-4d13812107a7', 'test', 'test_full'),
       ('9b4600ba-8a20-11ee-b9d1-0242ac120002', 'test', 'test_full');

INSERT INTO public.borrower_profile (id, partner_application_id, first_name, last_name, middle_name, phone_number, email, created_at, updated_at, created_by, updated_by, is_active, borrower_application_status, prev_full_name, birthdate, age, gender, marital_status, children, marriage_contract, education, passport_number, passport_issued_date, passport_issued_by_code, passport_issued_by_name, registration_address, residence_address, registration_type, snils, residence_rf, employment_status, total_work_experience, main_income, additional_income, pension, proof_of_income, borrower_real_estate_id, borrower_vehicle_id, borrower_employer_id, residency_outside_ru, long_term_stay_outside_ru, is_public_official, tin, family_relation, related_public_official, public_official_position, tin_foreign, birth_place, citizenship, tax_residency_countries, url_mapping_id)
VALUES ('7cb535d6-f92e-11ed-be56-0242ac120002', '7addcbef-c1e0-4de1-adeb-377f864efcfa', 'Ivan', 'Petrov', 'Petrovich', '9505421842', 'test2@test.com', '2023-05-17 13:46:26.000000', null, 0, null, true, 'DATA_NO_ENTERED', 'Романов Петя', '2023-09-16', 30, 'MALE', 'SINGLE', 4, 'NO', 'HIGHER', '2222222222', '2023-09-22', '144009', 'ФМС РФ', 'Где-то в мире', 'Где-то в мире', null, '32532523523', true, 'SELF_EMPLOYED', 'FROM_3_TO_6', 144124, null, null, 'BANK_REFERENCE', null, null, 'b0c5a48e-0a2b-4659-b408-33295b6ea968', null, null, null, null, null, null, null, null, null, null, null, '9f8b7286-33f3-4600-9471-4d13812107a7'),
       ('1348b508-f476-11ed-a05b-0242ac120003', '5ff4b32c-f967-4cb1-8705-7470a321fe34', 'Ivan', 'Ivanov Perviy', 'Ivanovich', '9876543219', 'test@test.com', '2023-05-17 13:46:26.000000', null, 0, null, true, 'DOCS_SIGNED', 'Романов Петя', '2023-09-16', 30, 'MALE', 'SINGLE', 4, 'NO', 'HIGHER', '2222222222', '2023-09-22', '144009', 'ФМС РФ', 'Где-то в мире', 'Где-то в мире', null, '32532523523', true, 'SELF_EMPLOYED', 'FROM_3_TO_6', 144124, null, null, 'BANK_REFERENCE', null, null, '919889ca-2f87-4a56-a33e-5e0cccff0857', null, null, null, null, null, null, null, null, null, null, null, '9b4600ba-8a20-11ee-b9d1-0242ac120002'),
       ('3e727d06-b8c8-4505-9187-3544180c1001', 'f09e9c57-e110-4f55-9124-9fafba8a441c', 'Alexander', 'Testov', null, '9998999995', 'test3@test.com', '2023-05-17 13:46:26.000000', null, 0, null, true, 'DOCS_SIGNED', 'Романов Петя', '2023-09-16', 30, 'MALE', 'SINGLE', 4, 'NO', 'HIGHER', '2222222222', '2023-09-22', '144009', 'ФМС РФ', 'Где-то в мире', 'Где-то в мире', null, '32532523523', true, 'SELF_EMPLOYED', 'FROM_3_TO_6', 144124, null, null, 'BANK_REFERENCE', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),
       ('1711691d-3f06-4778-8d16-799880a35f58', 'f09e9c57-e110-4f55-9124-9fafba8a441c', 'Vladimir', 'Putin', null, '9999999999', 'test4@test.com', '2023-05-17 13:46:26.000000', null, 0, null, true, 'DATA_NO_ENTERED', 'Романов Петя', '2023-09-16', 30, 'MALE', 'SINGLE', 4, 'NO', 'HIGHER', '2222222222', '2023-09-22', '144009', 'ФМС РФ', 'Где-то в мире', 'Где-то в мире', null, '32532523523', true, 'SELF_EMPLOYED', 'FROM_3_TO_6', 144124, null, null, 'BANK_REFERENCE', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),
       ('477fa93e-6fad-422c-b08a-f267dd4a0440', 'd573aeb5-ea8d-48e7-989c-82ce17e16ac2', 'Vladimir', 'Lenin', null, '9944332218', 'test5@test.com', '2023-05-17 13:46:26.000000', null, 0, null, true, 'DOCS_SIGNED', null, '2023-09-16', 40, 'MALE', 'SINGLE', 4, 'NO', 'HIGHER', '2222222222', '2023-09-22', '144009', 'ФМС РФ', 'Где-то в мире', 'Где-то в мире', null, '32532523523', true, 'SELF_EMPLOYED', 'FROM_3_TO_6', 144124, null, null, 'BANK_REFERENCE', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

INSERT INTO public.bank_application (id, credit_program_id, bank_application_status, monthly_payment, down_payment, credit_term, overpayment, partner_application_id, created_at, updated_at, created_by, updated_by, is_active, real_estate_price, main_borrower, application_number, underwriting_id, real_estate_type)
VALUES ('3b339aa4-5462-485a-9118-5922cd948566', 'bfda8d66-d926-11ed-afa1-0242ac120002', 'DATA_NO_ENTERED', 60000, 500000, 120, 6000000, '5ff4b32c-f967-4cb1-8705-7470a321fe34', '2023-05-09 14:35:58.039296', '2023-05-09 14:35:58.039296', 2929, null, true, 10000000, '1348b508-f476-11ed-a05b-0242ac120003', 10002, null, 'APARTMENT'),
       ('f5cdbc9c-f53f-11ed-a05b-0242ac120003', '8222cb80-d928-11ed-afa1-0242ac120002', 'DATA_NO_ENTERED', 60000, 500000, 120, 6000000, '7addcbef-c1e0-4de1-adeb-377f864efcfa', '2023-05-09 14:35:58.039296', '2023-05-09 14:35:58.039296', 2929, null, true, 10000000, '7cb535d6-f92e-11ed-be56-0242ac120002', 10003, null, 'APARTMENT'),
       ('22cdf786-0f33-11ee-be56-0242ac120002', '8222cb80-d928-11ed-afa1-0242ac120002', 'DATA_NO_ENTERED', 60000, 500000, 120, 6000000, '5ff4b32c-f967-4cb1-8705-7470a321fe34', '2023-05-09 14:35:58.039296', '2023-05-09 14:35:58.039296', 2929, null, true, 10000000, '1348b508-f476-11ed-a05b-0242ac120003', 10004, null, 'APARTMENT'),
       ('bc0c4038-469d-4404-b715-eba126cc7538', '8222cb80-d928-11ed-afa1-0242ac120002', 'DATA_NO_ENTERED', 60000, 500000, 120, 6000000, 'f09e9c57-e110-4f55-9124-9fafba8a441c', '2023-05-09 14:35:58.039296', '2023-05-09 14:35:58.039296', 2929, null, true, 10000000, '1711691d-3f06-4778-8d16-799880a35f58', 10005, null, 'APARTMENT'),
       ('75409038-3845-4899-b3e6-435fe9f29b83', '4a8d6fe4-dabe-11ed-afa1-0242ac120002', 'DATA_NO_ENTERED', 60000, 500000, 120, 6000000, 'f09e9c57-e110-4f55-9124-9fafba8a441c', '2023-05-09 14:35:58.039296', '2023-05-09 14:35:58.039296', 2929, null, true, 10000000, '1711691d-3f06-4778-8d16-799880a35f58', 10006, null, 'APARTMENT'),
       ('31022405-c650-4d71-9524-1ca75443ddb6', '4a8d6fe4-dabe-11ed-afa1-0242ac120002', 'READY_TO_SENDING', 60000, 500000, 120, 6000000, 'd573aeb5-ea8d-48e7-989c-82ce17e16ac2', '2023-05-09 14:35:58.039296', '2023-05-09 14:35:58.039296', 2929, null, true, 10000000, '477fa93e-6fad-422c-b08a-f267dd4a0440', 10001, null, 'APARTMENT_COMPLEX'),
       ('9af7998f-62f7-4541-8a11-8e36cf525fc8', '4a8d6fe4-dabe-11ed-afa1-0242ac120002', 'READY_TO_SENDING', 60000, 500000, 120, 6000000, 'd573aeb5-ea8d-48e7-989c-82ce17e16ac2', '2023-05-09 14:35:58.039296', '2023-05-09 14:35:58.039296', 2929, null, false, 10000000, '477fa93e-6fad-422c-b08a-f267dd4a0440', 10007, null, 'APARTMENT_COMPLEX');


INSERT INTO public.attachment (id, name, size_bytes, mime_type, content_md5, created_at, created_by, updated_at, updated_by, is_active)
VALUES (1, 'test1', 2041, 'text/csv', '42da2445c8bd86312f3bed333ebd3bca', null, null, null, null, true),
       (2, 'test2', 2041, 'text/csv', '42da2445c8bd86312f3bed333ebd3bca', null, null, null, null, true),
       (3, 'test3', 2041, 'text/csv', '42da2445c8bd86312f3bed333ebd3bca', null, null, null, null, true),
       (4, 'test4', 2041, 'text/csv', '42da2445c8bd86312f3bed333ebd3bca', null, null, null, null, true),
       (5, 'test5', 2041, 'text/csv', '42da2445c8bd86312f3bed333ebd3bca', null, null, null, null, true),
       (6, 'test5', 2041, 'text/csv', '42da2445c8bd86312f3bed333ebd3bca', null, null, null, null, true);

INSERT INTO public.borrower_documents (id, borrower_profile_id, document_type, bank_id, created_at, updated_at, created_by, updated_by, is_active, attachment_id, bank_application_id)
VALUES ('a7c9cbca-1d41-47d4-a1ab-d36fc4e7d927', '1348b508-f476-11ed-a05b-0242ac120003', 'GENERATED_FORM', '1fd0708a-d848-11ed-afa1-0242ac120002', '2023-06-21 10:20:31.012936', '2023-06-23 09:55:53.729197', 2962, 2962, true, 1, null),
       ('76213fa4-3253-415d-830d-7534181e15b4', '1348b508-f476-11ed-a05b-0242ac120003', 'INCOME_CERTIFICATE', null, '2023-06-21 10:16:50.974807', '2023-06-23 09:55:48.283698', 2962, 2962, true, 2, null),
       ('eb5e92c8-d36a-495c-a558-b3ad0eb7e8b2', '1348b508-f476-11ed-a05b-0242ac120003', 'CERTIFIED_COPY_TK', null, '2023-06-21 10:16:23.510593', '2023-06-23 09:55:30.853932', 2962, 2962, true, 3, null),
       ('da14a0c8-834d-4474-a35a-887406acc96d', '1348b508-f476-11ed-a05b-0242ac120003', 'BORROWER_SNILS', null, '2023-06-21 10:16:16.975758', '2023-06-23 09:55:15.207849', 2962, 2962, true, 4, null),
       ('be1e073b-34a8-43ba-bfd3-2518404e5d5a', '1348b508-f476-11ed-a05b-0242ac120003', 'BORROWER_PASSPORT', null, '2023-06-22 11:00:06.205104', '2023-06-23 09:51:08.347992', 2962, 2962, true, 5, null),
       ('8f718683-930e-42df-9473-28f06169182c', '1348b508-f476-11ed-a05b-0242ac120003', 'GENERATED_SIGNATURE_FORM', null, '2023-06-22 11:00:06.205104', '2023-06-23 09:51:08.347992', 2962, 2962, true, 6, null);
