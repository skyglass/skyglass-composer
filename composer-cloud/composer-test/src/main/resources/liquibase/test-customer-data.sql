insert into customer (uuid, email, firstname, lastname) values ('f78c43c4-2a31-11e9-b210-d663bd873d93', 'dave@dmband.com', 'Dave', 'Matthews');
insert into customer (uuid, email, firstname, lastname) values ('f78c4860-2a31-11e9-b210-d663bd873d93', 'carter@dmband.com', 'Carter', 'Beauford');
insert into customer (uuid, email, firstname, lastname) values ('f78c49c8-2a31-11e9-b210-d663bd873d93', 'boyd@dmband.com', 'Boyd', 'Tinsley');

insert into address (uuid, street, city, country, customer_uuid) values ('f78c4b08-2a31-11e9-b210-d663bd873d93', '27 Broadway', 'New York', 'United States', 'f78c43c4-2a31-11e9-b210-d663bd873d93');
insert into address (uuid, street, city, country, customer_uuid) values ('f78c4c34-2a31-11e9-b210-d663bd873d93', '27 Broadway', 'New York', 'United States', 'f78c43c4-2a31-11e9-b210-d663bd873d93');

insert into product (uuid, name, description, price) values ('f78c4fae-2a31-11e9-b210-d663bd873d93', 'iPad', 'Apple tablet device', 499.0);
insert into product (uuid, name, description, price) values ('f78c5102-2a31-11e9-b210-d663bd873d93', 'MacBook Pro', 'Apple notebook', 1299.0);
insert into product (uuid, name, description, price) values ('f78c538c-2a31-11e9-b210-d663bd873d93', 'Dock', 'Dock for iPhone/iPad', 49.0);

insert into product_attributes (attributes_key, product_uuid, attributes) values ('connector', 'f78c4fae-2a31-11e9-b210-d663bd873d93', 'socket');
insert into product_attributes (attributes_key, product_uuid, attributes) values ('connector', 'f78c538c-2a31-11e9-b210-d663bd873d93', 'plug');

insert into orders (uuid, customer_uuid, shipping_address_uuid) values ('f78c54cc-2a31-11e9-b210-d663bd873d93', 'f78c43c4-2a31-11e9-b210-d663bd873d93', 'f78c4c34-2a31-11e9-b210-d663bd873d93');
insert into line_item (uuid, product_uuid, amount, order_uuid, price) values ('f78c55f8-2a31-11e9-b210-d663bd873d93', 'f78c4fae-2a31-11e9-b210-d663bd873d93', 2, 'f78c54cc-2a31-11e9-b210-d663bd873d93', 499.0);
insert into line_item (uuid, product_uuid, amount, order_uuid, price) values ('865fa000-2a32-11e9-b210-d663bd873d93', 'f78c5102-2a31-11e9-b210-d663bd873d93', 1, 'f78c54cc-2a31-11e9-b210-d663bd873d93', 1299.0);