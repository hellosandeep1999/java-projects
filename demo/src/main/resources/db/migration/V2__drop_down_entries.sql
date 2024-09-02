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

create table `TAB_VISIBLE` (
    id bigint primary key AUTO_INCREMENT,
    name varchar(64) not null,
    start_date date not null,
    end_date date not null
);

create table `ZONE_WISE` (
    id bigint primary key AUTO_INCREMENT,
    zone varchar(10) not null,
    start_date date not null,
    end_date date not null
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

INSERT INTO TAB_VISIBLE (name,start_date,end_date) VALUES
('Login','2024-08-22','2024-09-30'),
('Update','2024-08-22','2024-09-06'),
('Signup','2024-08-22','2024-09-06'),
('Generate Password','2024-08-22','2024-09-30');

INSERT INTO ZONE_WISE (zone,start_date,end_date) VALUES
('EZ','2024-08-22','2024-09-30'),
('NEZ','2024-08-22','2024-09-30'),
('NZ','2024-08-22','2024-09-30'),
('SZ','2024-08-22','2024-09-30'),
('WZ','2024-08-22','2024-09-30');
