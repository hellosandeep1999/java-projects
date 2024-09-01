
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

create table `POSITIONS` (
    id bigint primary key AUTO_INCREMENT,
    name varchar(64) not null
);

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

INSERT INTO POSITIONS (name) VALUES
('PRESIDENT'),
('GENERAL SECRETARY'),
('ADDL GEN. SECRETARY'),
('ADDL GEN. SECRETARY (ORG)'),
('TREASURER'),
('ASST TREASURER'),
('SECRETARY(AE)'),
('SECRETARY(SEA/EA)'),
('SECRETARY(SrTech/Tech)'),
('SECRETARY(Helper)'),
('STATE SECRETARY'),
('VICE PRESIDENT-AIR'),
('ASSISTANT GEN. SEC (AIR)'),
('VICE PRESIDENT-TV'),
('ASSISTANT GEN. SEC (TV)'),
('VICE PRESIDENT'),
('ASSISTANT GEN. SEC (ORG)'),
('JOINT SEC (SEA/EA)'),
('JOINT SEC (SrTech/Tech)'),
('JOINT SEC (Helper)');
