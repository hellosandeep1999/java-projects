package com.example.demo.service;

import com.example.demo.domain.model.*;
import com.example.demo.domain.model.enums.RequestType;
import com.example.demo.domain.model.enums.UserType;
import com.example.demo.generated.jooq.Tables;
import com.example.demo.generated.jooq.enums.ChiefLevelVotersRequesttype;
import com.example.demo.generated.jooq.enums.VotersAuthorizedIsvoted;
import com.example.demo.generated.jooq.tables.records.TabVisibleRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.example.demo.http.api.Controller.USER_NOT_AUTHORIZED;

@Service
public class VoterService {



    @Autowired
    DSLContext dslContext;

    @Autowired
    ActionOnVoterService actionOnVoterService;



    public LoginResponse authenticate(String username, String password) {
       var record =  dslContext.selectFrom(Tables.VOTERS_AUTHORIZED).where(Tables.VOTERS_AUTHORIZED.USERNAME.eq(username)
                .and(Tables.VOTERS_AUTHORIZED.PASSWORD.eq(password))).fetchInto(Tables.VOTERS_AUTHORIZED);
       if(record.isEmpty()){
           throw new RuntimeException(USER_NOT_AUTHORIZED);
       }
       var userType = record.get(0).get(Tables.VOTERS_AUTHORIZED.USERTYPE);
       var loginKey = UUID.randomUUID()+"_"+ userType;
       dslContext.update(Tables.VOTERS_AUTHORIZED)
               .set(Tables.VOTERS_AUTHORIZED.LOGINKEY,loginKey)
               .where(Tables.VOTERS_AUTHORIZED.USERNAME.eq(username)).execute();

        var userName = record.get(0).get(Tables.VOTERS_AUTHORIZED.USERNAME);
        var name = record.get(0).get(Tables.VOTERS_AUTHORIZED.NAME);

       return new LoginResponse(userName,UserType.valueOf(userType.name()),name, loginKey);
    }

    public void logout(String username) {
        dslContext.update(Tables.VOTERS_AUTHORIZED)
                .set(Tables.VOTERS_AUTHORIZED.LOGINKEY,(String) null)
                .where(Tables.VOTERS_AUTHORIZED.USERNAME.eq(username)).execute();
    }

    public void signup(Voter voterRequest) {
        var count = dslContext.fetchCount(DSL.selectFrom(Tables.VOTERS_AUTHORIZED)
                .where(Tables.VOTERS_AUTHORIZED.MOBILENUMBER.eq(voterRequest.mobileNumber())
                        .or(Tables.VOTERS_AUTHORIZED.EMAIL.eq(voterRequest.email()))));
        if(count != 0){
            throw new RuntimeException("User Already Exists in Authorized Users");
        }
        dslContext.insertInto(Tables.CHIEF_LEVEL_VOTERS)
                .columns(Tables.CHIEF_LEVEL_VOTERS.NAME,
                        Tables.CHIEF_LEVEL_VOTERS.DESIGNATION,
                        Tables.CHIEF_LEVEL_VOTERS.ORGANIZATION,
                        Tables.CHIEF_LEVEL_VOTERS.PLACEOFPOSTING,
                        Tables.CHIEF_LEVEL_VOTERS.ZONE,
                        Tables.CHIEF_LEVEL_VOTERS.STATE,
                        Tables.CHIEF_LEVEL_VOTERS.MOBILENUMBER,
                        Tables.CHIEF_LEVEL_VOTERS.EMAIL,
                        Tables.CHIEF_LEVEL_VOTERS.REQUESTTYPE)
                .values(voterRequest.name()
                        , voterRequest.designation(),voterRequest.organization(),
                        voterRequest.placeOfPosting(),
                        voterRequest.zone(),voterRequest.state(),
                        voterRequest.mobileNumber(),voterRequest.email(), ChiefLevelVotersRequesttype.SIGNUP_REQUEST).execute();
    }

    public void dataCorrection(Voter voterRequest) {
        var count = dslContext.fetchCount(DSL.selectFrom(Tables.VOTERS_AUTHORIZED)
                .where(Tables.VOTERS_AUTHORIZED.SERIALNUMBER.eq(voterRequest.serialNumber())));
        if(count == 0){
            throw new RuntimeException("Voter not found system, Please verify serial number!");
        }
        dslContext.insertInto(Tables.CHIEF_LEVEL_VOTERS)
                .columns(Tables.CHIEF_LEVEL_VOTERS.SERIALNUMBER,
                        Tables.CHIEF_LEVEL_VOTERS.NAME,
                        Tables.CHIEF_LEVEL_VOTERS.DESIGNATION,
                        Tables.CHIEF_LEVEL_VOTERS.ORGANIZATION,
                        Tables.CHIEF_LEVEL_VOTERS.PLACEOFPOSTING,
                        Tables.CHIEF_LEVEL_VOTERS.ZONE,
                        Tables.CHIEF_LEVEL_VOTERS.STATE,
                        Tables.CHIEF_LEVEL_VOTERS.MOBILENUMBER,
                        Tables.CHIEF_LEVEL_VOTERS.EMAIL,Tables.CHIEF_LEVEL_VOTERS.REQUESTTYPE)
                .values(voterRequest.serialNumber(),voterRequest.name()
                        , voterRequest.designation(),voterRequest.organization(),
                        voterRequest.placeOfPosting(),
                        voterRequest.zone(),voterRequest.state(),
                        voterRequest.mobileNumber(),voterRequest.email(), ChiefLevelVotersRequesttype.UPDATE_REQUEST).execute();
    }

    public VoterDetail getVoterDetails(String userType) {
        var authorizeVoters = dslContext.selectFrom(Tables.VOTERS_AUTHORIZED)
                .fetchInto(Tables.VOTERS_AUTHORIZED)
                .stream().map(record->
                        new Voter(0L,
                                record.get(Tables.VOTERS_AUTHORIZED.SERIALNUMBER),
                                record.get(Tables.VOTERS_AUTHORIZED.NAME),
                                record.get(Tables.VOTERS_AUTHORIZED.DESIGNATION),
                                record.get(Tables.VOTERS_AUTHORIZED.ORGANIZATION),
                                record.get(Tables.VOTERS_AUTHORIZED.PLACEOFPOSTING),
                                record.get(Tables.VOTERS_AUTHORIZED.ZONE),
                                record.get(Tables.VOTERS_AUTHORIZED.STATE),
                                record.get( Tables.VOTERS_AUTHORIZED.MOBILENUMBER),
                                record.get( Tables.VOTERS_AUTHORIZED.EMAIL),
                                null
                        )).toList();

        var unAuthorizeVoters = dslContext.selectFrom(Tables.VOTERS_UNAUTHORIZED)
                .fetchInto(Tables.VOTERS_UNAUTHORIZED)
                .stream().map(record->
                        new Voter(0L,
                                record.get(Tables.VOTERS_UNAUTHORIZED.SERIALNUMBER),
                                record.get(Tables.VOTERS_UNAUTHORIZED.NAME),
                                record.get(Tables.VOTERS_UNAUTHORIZED.DESIGNATION),
                                record.get(Tables.VOTERS_UNAUTHORIZED.ORGANIZATION),
                                record.get(Tables.VOTERS_UNAUTHORIZED.PLACEOFPOSTING),
                                record.get(Tables.VOTERS_UNAUTHORIZED.ZONE),
                                record.get(Tables.VOTERS_UNAUTHORIZED.STATE),
                                record.get( Tables.VOTERS_UNAUTHORIZED.MOBILENUMBER),
                                record.get(Tables.VOTERS_UNAUTHORIZED.EMAIL),
                                RequestType.valueOf(record.get( Tables.VOTERS_UNAUTHORIZED.REQUESTTYPE).toString())
                        )).toList();
        var logs = dslContext.selectFrom(Tables.LOGS)
                .fetchInto(Tables.LOGS)
                .stream().map(record->
                        new Logs(
                                record.get(Tables.LOGS.SERIALNUMBER),
                                record.get(Tables.LOGS.NAME),
                                record.get(Tables.LOGS.DESIGNATION),
                                record.get(Tables.LOGS.ORGANIZATION),
                                record.get(Tables.LOGS.PLACEOFPOSTING),
                                record.get(Tables.LOGS.ZONE),
                                record.get(Tables.LOGS.STATE),
                                record.get( Tables.LOGS.MOBILENUMBER),
                                record.get(Tables.LOGS.EMAIL),
                                RequestType.valueOf(record.get(Tables.LOGS.REQUESTTYPE).toString()),
                                record.get(Tables.LOGS.TIME),
                                record.get(Tables.LOGS.ACTIONBY),
                                record.get(Tables.LOGS.APPROVEDBY),
                                record.get(Tables.LOGS.REJECTEDBY)
                        )).toList();
        List<Voter> pendingVoters = List.of();
        if(userType.equals(UserType.CHIEF.name())){
         pendingVoters = dslContext.selectFrom(Tables.CHIEF_LEVEL_VOTERS)
                .fetchInto(Tables.CHIEF_LEVEL_VOTERS)
                .stream().map(record->
                        new Voter(
                                record.get(Tables.CHIEF_LEVEL_VOTERS.ID),
                                record.get(Tables.CHIEF_LEVEL_VOTERS.SERIALNUMBER),
                                record.get(Tables.CHIEF_LEVEL_VOTERS.NAME),
                                record.get(Tables.CHIEF_LEVEL_VOTERS.DESIGNATION),
                                record.get(Tables.CHIEF_LEVEL_VOTERS.ORGANIZATION),
                                record.get(Tables.CHIEF_LEVEL_VOTERS.PLACEOFPOSTING),
                                record.get(Tables.CHIEF_LEVEL_VOTERS.ZONE),
                                record.get(Tables.CHIEF_LEVEL_VOTERS.STATE),
                                record.get( Tables.CHIEF_LEVEL_VOTERS.MOBILENUMBER),
                                record.get( Tables.CHIEF_LEVEL_VOTERS.EMAIL),
                                RequestType.valueOf(record.get( Tables.CHIEF_LEVEL_VOTERS.REQUESTTYPE).toString())
                        )).toList();
        }
        else if(userType.equals(UserType.ADMIN.name())){
            pendingVoters = dslContext.selectFrom(Tables.ADMIN_LEVEL_VOTERS)
                    .fetchInto(Tables.ADMIN_LEVEL_VOTERS)
                    .stream().map(record->
                            new Voter(
                                    record.get(Tables.ADMIN_LEVEL_VOTERS.ID),
                                    record.get(Tables.ADMIN_LEVEL_VOTERS.SERIALNUMBER),
                                    record.get(Tables.ADMIN_LEVEL_VOTERS.NAME),
                                    record.get(Tables.ADMIN_LEVEL_VOTERS.DESIGNATION),
                                    record.get(Tables.ADMIN_LEVEL_VOTERS.ORGANIZATION),
                                    record.get(Tables.ADMIN_LEVEL_VOTERS.PLACEOFPOSTING),
                                    record.get(Tables.ADMIN_LEVEL_VOTERS.ZONE),
                                    record.get(Tables.ADMIN_LEVEL_VOTERS.STATE),
                                    record.get( Tables.ADMIN_LEVEL_VOTERS.MOBILENUMBER),
                                    record.get( Tables.ADMIN_LEVEL_VOTERS.EMAIL),
                                    RequestType.valueOf(record.get( Tables.ADMIN_LEVEL_VOTERS.REQUESTTYPE).toString())
                            )).toList();
        }
        return new VoterDetail(authorizeVoters, pendingVoters, unAuthorizeVoters,logs);
    }


    public void actionOnVoter(ActionOnVoterRequest actionOnVoterRequest, String userType) {
        //check is it came from chief or admin
        if(userType.equals(UserType.CHIEF.name())){
            actionOnVoterService.chiefLevelActionOnVoter(actionOnVoterRequest,userType);
        } else if (userType.equals(UserType.ADMIN.name())) {
            actionOnVoterService.adminLevelActionOnVoter(actionOnVoterRequest,userType);
        }
    }


    public boolean verifyLogin(String loginKey) {
        var count = dslContext.fetchCount(DSL.selectFrom(Tables.VOTERS_AUTHORIZED)
                .where(Tables.VOTERS_AUTHORIZED.LOGINKEY.eq(loginKey)));
        return count == 0;
    }

    public boolean isAlreadyVoted(String loginKey) {
        var isVoted = dslContext.select(Tables.VOTERS_AUTHORIZED.ISVOTED).
                from(Tables.VOTERS_AUTHORIZED)
                .where(Tables.VOTERS_AUTHORIZED.LOGINKEY.eq(loginKey)).fetchInto(VotersAuthorizedIsvoted.class);
        return isVoted.get(0).equals(VotersAuthorizedIsvoted.VOTED);
    }

    public boolean isZoneEligible(String loginKey) {
        var zone = dslContext.select(Tables.VOTERS_AUTHORIZED.ZONE).
                from(Tables.VOTERS_AUTHORIZED)
                .where(Tables.VOTERS_AUTHORIZED.LOGINKEY.eq(loginKey)).fetchInto(String.class);
       var zone_wise =  dslContext.selectFrom(Tables.ZONE_WISE)
                .where(Tables.ZONE_WISE.ZONE.eq(zone.get(0))).fetchAny();
        assert zone_wise != null;
        var startDate = zone_wise.getStartDate();
        var endDate = zone_wise.getEndDate();
        var currentDate = LocalDate.now();
        return (startDate.isBefore(currentDate) || startDate.isEqual(currentDate))
                && (currentDate.isBefore(endDate) || currentDate.isEqual(endDate));
    }

    public TabsResponse tabsVisibility() {
        var records = dslContext.selectFrom(Tables.TAB_VISIBLE).fetchInto(Tables.TAB_VISIBLE);
        var names = records.stream().filter(r->{
            var startDate = r.getStartDate();
            var endDate = r.getEndDate();
            var currentDate = LocalDate.now();
            return (startDate.isBefore(currentDate) || startDate.isEqual(currentDate))
                    && (currentDate.isBefore(endDate) || currentDate.isEqual(endDate));
        }).map(TabVisibleRecord::getName).toList();
        var signUp = names.contains("Login");
        var login = names.contains("Update");
        var update = names.contains("Signup");
        var generatePassword = names.contains("Generate Password");
        return new TabsResponse(login,signUp,update,generatePassword);
    }

    public DropDownDetails getAllDropDowns() {
        var zones = dslContext.selectFrom(Tables.ZONE)
                .fetchInto(Tables.ZONE)
                .stream().map(record->
                        new DropDownData(
                                record.get(Tables.ZONE.ID),
                                record.get(Tables.ZONE.NAME)
                        )).toList();
        var states = dslContext.selectFrom(Tables.STATE)
                .fetchInto(Tables.STATE)
                .stream().map(record->
                        new DropDownData(
                                record.get(Tables.STATE.ID),
                                record.get(Tables.STATE.NAME)
                        )).toList();
        var organization = dslContext.selectFrom(Tables.ORGANIZATION)
                .fetchInto(Tables.ORGANIZATION)
                .stream().map(record->
                        new DropDownData(
                                record.get(Tables.ORGANIZATION.ID),
                                record.get(Tables.ORGANIZATION.NAME)
                        )).toList();
        var designation = dslContext.selectFrom(Tables.DESIGNATION)
                .fetchInto(Tables.DESIGNATION)
                .stream().map(record->
                        new DropDownData(
                                record.get(Tables.DESIGNATION.ID),
                                record.get(Tables.DESIGNATION.NAME)
                        )).toList();
        return new DropDownDetails(zones,states,organization,designation);
    }


}
