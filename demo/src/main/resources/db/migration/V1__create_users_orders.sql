create table `VOTERS_AUTHORIZED` (
    serialNumber bigint primary key AUTO_INCREMENT,
    userName varchar(254) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not null unique,
    name varchar(64)  not null,
    designation varchar(64) not null,
    organization varchar(64) not null,
    zone varchar(64) not null,
    state varchar(64) not null,
    mobileNumber varchar(16) not null,
    email varchar(254),
    userType ENUM('VOTER', 'CHIEF', 'ADMIN') NOT NULL,
    isVoted ENUM('NOT_VOTED', 'VOTED') DEFAULT 'NOT_VOTED',
    votingResponse varchar(5000),
    otp varchar(10),
    password varchar(254) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not null,
    loginKey varchar(254)
);

create table `CHIEF_LEVEL_VOTERS` (
    id bigint primary key AUTO_INCREMENT,
    serialNumber bigint,
    name varchar(64) not null,
    designation varchar(64) not null,
    organization varchar(64) not null,
    zone varchar(64) not null,
    state varchar(64) not null,
    mobileNumber varchar(16) not null,
    email varchar(254) not null,
    requestType ENUM('SIGNUP_REQUEST', 'UPDATE_REQUEST') not null
);

create table `ADMIN_LEVEL_VOTERS` (
    id bigint primary key AUTO_INCREMENT,
    serialNumber bigint,
    name varchar(64) not null,
    designation varchar(64) not null,
    organization varchar(64) not null,
    zone varchar(64) not null,
    state varchar(64) not null,
    mobileNumber varchar(16) not null,
    email varchar(254) not null,
    requestType ENUM('SIGNUP_REQUEST', 'UPDATE_REQUEST') not null
);

create table `VOTERS_UNAUTHORIZED` (
    id bigint primary key AUTO_INCREMENT,
    serialNumber bigint,
    name varchar(64) not null,
    designation varchar(64) not null,
    organization varchar(64) not null,
    zone varchar(64) not null,
    state varchar(64) not null,
    mobileNumber varchar(16) not null,
    email varchar(254) not null,
    requestType ENUM('SIGNUP_REQUEST', 'UPDATE_REQUEST') not null
);

create table `LOGS` (
    id bigint primary key AUTO_INCREMENT,
    serialNumber bigint,
    name varchar(64) not null,
    designation varchar(64) not null,
    organization varchar(64) not null,
    zone varchar(64) not null,
    state varchar(64) not null,
    mobileNumber varchar(16) not null,
    email varchar(254) not null,
    requestType ENUM('SIGNUP_REQUEST', 'UPDATE_REQUEST') not null,
    time varchar(64) not null,
    actionBy varchar(64) not null,
    approvedBy varchar(64),
    rejectedBy varchar(64)
);

create table `CANDIDATE_AND_VOTING` (
    id bigint primary key AUTO_INCREMENT,
    name varchar(64)  not null,
    designation varchar(64) not null,
    organization varchar(64) not null,
    zone varchar(64) not null,
    state varchar(64) not null,
    mobileNumber varchar(16) not null,
    email varchar(254),
    position varchar(254) not null,
    selectionType ENUM('ALL', 'ORG', 'ZONE','STATE','ORG_ZONE'),
    voteCount int not null default 0
);

