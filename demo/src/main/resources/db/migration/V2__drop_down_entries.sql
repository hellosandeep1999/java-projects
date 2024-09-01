create table `ZONE` (
    id bigint primary key AUTO_INCREMENT,
    name varchar(64) not null
);

create table `STATE` (
    id bigint primary key AUTO_INCREMENT,
    name varchar(64) not null
);

create table `ORGANIZATION` (
    id bigint primary key AUTO_INCREMENT,
    name varchar(64) not null
);

create table `DESIGNATION` (
    id bigint primary key AUTO_INCREMENT,
    name varchar(64) not null
);

INSERT INTO ZONE (name) VALUES
('EZ'),
('NEZ'),
('NZ'),
('SZ'),
('WZ');

INSERT INTO STATE (name) VALUES
('Andhra Pradesh'),
('Arunachal Pradesh'),
('Assam'),
('A&N Islands'),
('Bihar'),
('Chhattisgarh'),
('Delhi'),
('Goa'),
('Gujarat'),
('Haryana'),
('Himachal Pradesh'),
('Jammu & Kashmir'),
('Jharkhand'),
('Karnataka'),
('Kerala'),
('Madhya Pradesh'),
('Maharastra'),
('Manipur'),
('Meghalaya'),
('Mizoram'),
('Nagaland'),
('Odisha'),
('Punjab'),
('Rajasthan'),
('Sikkim'),
('Tamil Nadu & Pondicherry'),
('Telangana'),
('Tripura'),
('Uttar Pradesh'),
('Uttarakhand'),
('West Bengal');


INSERT INTO ORGANIZATION (name) VALUES
('TV'),
('AIR');

INSERT INTO DESIGNATION (name) VALUES
('SRTECH'),
('TECH'),
('SEA'),
('EA'),
('HELPER'),
('AE');
