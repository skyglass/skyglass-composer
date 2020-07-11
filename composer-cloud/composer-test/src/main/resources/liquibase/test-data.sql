insert into BUSINESSOWNER (UUID, NAME) values ('6018d2e1-b94b-424a-840c-9cbae9074f4e', 'BusinessPartner1');
insert into BUSINESSOWNER (UUID, NAME) values ('befd22b7-53d0-4671-9df7-49dbbf38e45e', 'BusinessPartner2');

insert into BUSINESSUNIT (UUID, NAME, OWNER_UUID, PARENT_UUID) values ('DF789ACB-0CC3-4B4C-BF73-1E68DE4C7CA4', 'BusinessPartner1', '6018d2e1-b94b-424a-840c-9cbae9074f4e', null);
insert into BUSINESSUNIT (UUID, NAME, OWNER_UUID, PARENT_UUID) values ('d659dd95-c3b7-4f55-adf0-596a117c12b9', 'BusinessPartner2', 'befd22b7-53d0-4671-9df7-49dbbf38e45e', null);