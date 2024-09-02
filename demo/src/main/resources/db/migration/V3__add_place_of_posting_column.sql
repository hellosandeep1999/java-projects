alter table VOTERS_AUTHORIZED
add column placeOfPosting varchar(254)
after organization;

alter table CHIEF_LEVEL_VOTERS
add column placeOfPosting varchar(254)
after organization;

alter table ADMIN_LEVEL_VOTERS
add column placeOfPosting varchar(254)
after organization;

alter table VOTERS_UNAUTHORIZED
add column placeOfPosting varchar(254)
after organization;

alter table LOGS
add column placeOfPosting varchar(254)
after organization;

alter table CANDIDATE_AND_VOTING
add column placeOfPosting varchar(254)
after organization;
