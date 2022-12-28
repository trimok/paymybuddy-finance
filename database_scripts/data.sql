INSERT INTO BANK (id, name) values(1, 'BUDDY_BANK');
INSERT INTO BANK (id, name) values(2, 'USER_BANK');


INSERT INTO PERSON (id, email, enabled, name, password) VALUES(1, 'BUDDY',1, 'BUDDY', '$2y$10$1R.PcD/61WRFZVqngcynOeX/B/9Ia0QZWFbsNOlnlEb48GkSzU5EK');
INSERT INTO PERSON (id, email, enabled, name, password) VALUES(2, 'guest@guest.mail',1, 'guest@guest.mail', '$2a$10$/TjoybEFT1Hqu2AUoJyg3ehlGnNUbkmBTGFgxh5SI0CZHS3tvzTsq');
INSERT INTO PERSON (id, email, enabled, name, password) VALUES(3, 'gogolgoogelisant@gmail.com',1, 'Gogol Googelisant', '$2a$10$OypheY861OkA2cQl/IWFeeK0.42yF5vHnoGn1qN11NvGNPdwouVOq');

INSERT INTO ACCOUNT(id, amount, bank_id, person_id) VALUES (1,25,1,1);
INSERT INTO ACCOUNT(id, amount, bank_id, person_id) VALUES (2,9000,2,2);
INSERT INTO ACCOUNT(id, amount, bank_id, person_id) VALUES (3,475,1,2);
INSERT INTO ACCOUNT(id, amount, bank_id, person_id) VALUES (4,10000,2,3);
INSERT INTO ACCOUNT(id, amount, bank_id, person_id) VALUES (5,500,1,3);

INSERT INTO PERSON_CONTACTACCOUNT (person_id, account_id) values (2,5);

INSERT INTO ROLE (id, name, roles, person_id) values (1, 'BUDDY', 'AUTHORITY_USER', 1);
INSERT INTO ROLE (id, name, roles, person_id) values (2, 'guest@guest.mail', 'AUTHORITY_USER', 2);
INSERT INTO ROLE (id, name, roles, person_id) values (3, 'Gogol Googelisant', 'AUTHORITY_OIDC_USER', 3);


INSERT INTO TRANSACTION (id, amount, description, transaction_date, transaction_type, account_from_id, account_to_id) values(1, 1000, '','2022-12-28 14:35:33', 0, 2, 3);
INSERT INTO TRANSACTION (id, amount, description, transaction_date, transaction_type, account_from_id, account_to_id) values(2, 25, 'Commission','2022-12-28 14:35:33', 4, 3, 1);
INSERT INTO TRANSACTION (id, amount, description, transaction_date, transaction_type, account_from_id, account_to_id) values(3, 500, '','2022-12-28 14:35:33', 1, 3, 5);