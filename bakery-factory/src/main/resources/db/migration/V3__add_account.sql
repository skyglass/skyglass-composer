CREATE TABLE account (
    email VARCHAR(255) NOT NULL PRIMARY KEY,
    password VARCHAR(255) NOT NULL);

CREATE TABLE address (
    email VARCHAR(255) NOT NULL PRIMARY KEY,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    postcode VARCHAR(10) NOT NULL);