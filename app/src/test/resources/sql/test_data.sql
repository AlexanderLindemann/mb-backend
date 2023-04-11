INSERT INTO public.bank (id, name, order_number, logo_attachment_id)
VALUES ('0c371042-d848-11ed-afa1-0242ac120002'::UUID, 'aTestBank1', 40, NULL::BIGINT),
       ('1fd0708a-d848-11ed-afa1-0242ac120002'::UUID, 'bTestBank2', 30, NULL::BIGINT),
       ('2708daa4-d848-11ed-afa1-0242ac120002'::UUID, 'cTestBank3', 20, NULL::BIGINT),
       ('2ae84592-d848-11ed-afa1-0242ac120002'::UUID, 'dTestBank4', 10, NULL::BIGINT);

INSERT INTO public.bank_contact (id, full_name, email, bank_id)
VALUES ('48d91494-d868-11ed-afa1-0242ac120002'::UUID, 'Alexander Testovich', 'test-email@gmail.com','0c371042-d848-11ed-afa1-0242ac120002'::UUID);