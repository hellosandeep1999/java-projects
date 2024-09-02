package com.example.demo.service;

import com.example.demo.domain.model.ActionOnVoterRequest;
import com.example.demo.domain.model.enums.ActionType;
import com.example.demo.domain.model.enums.RequestType;
import com.example.demo.generated.jooq.Tables;
import com.example.demo.generated.jooq.enums.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ActionOnVoterService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int USERNAME_LENGTH = 8; // Desired length of the username
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    DSLContext dslContext;

    @Autowired
    private JavaMailSender mailSender;



    public void chiefLevelActionOnVoter(ActionOnVoterRequest actionOnVoterRequest, String userType) {
        //chief
            //  APPROVE
                // - delete from chief database
                // - insert into admin database
                // - log into logging database with approved by
            // REJECT
                // - delete from chief database
                // - insert into unauthorized database
                // - log into logging database with reject By
        if(actionOnVoterRequest.actionType().equals(ActionType.APPROVE)){
            dslContext.deleteFrom(Tables.CHIEF_LEVEL_VOTERS)
                    .where(Tables.CHIEF_LEVEL_VOTERS.ID.eq(actionOnVoterRequest.id())).execute();
            insertIntoAdminData(actionOnVoterRequest);
            approvedByLog(actionOnVoterRequest,userType);
        }else if(actionOnVoterRequest.actionType().equals(ActionType.REJECT)){
            dslContext.deleteFrom(Tables.CHIEF_LEVEL_VOTERS)
                    .where(Tables.CHIEF_LEVEL_VOTERS.ID.eq(actionOnVoterRequest.id())).execute();
            insertIntoUnauthorizedData(actionOnVoterRequest);
            rejectedByLog(actionOnVoterRequest,userType);
        }
    }




    public void adminLevelActionOnVoter(ActionOnVoterRequest actionOnVoterRequest, String userType) {
        //admin
            //  APPROVE
                // - mail
                // Signup
                      // - generate username password and send mail if not fail here
                     // - delete from admin database
                      // - insert into authorized database as new entry with username and password and as voter type
                // Update
                      // - generate username password and send mail if not fail here
                      // - delete from admin database
                      // - update into authorized database with new username password as voter type using serial number
               // - log into logging database with approved by
            // REJECT
                    // - delete from admin database
                     // - insert into unauthorized database
                      // - send mail for rejection
                      // - log into logging database with rejectBy
        if(actionOnVoterRequest.actionType().equals(ActionType.APPROVE)){
            var userName = actionOnVoterRequest.mobileNumber();
            var password = generateUniqueUsername();
            if(actionOnVoterRequest.requestType().equals(RequestType.SIGNUP_REQUEST)){
                approveEmail(actionOnVoterRequest.email(),actionOnVoterRequest.mobileNumber(),password);
                dslContext.deleteFrom(Tables.ADMIN_LEVEL_VOTERS)
                        .where(Tables.ADMIN_LEVEL_VOTERS.ID.eq(actionOnVoterRequest.id())).execute();
                insertIntoAuthorizedData(actionOnVoterRequest,userName,password);
                approvedByLog(actionOnVoterRequest,userType);
            } else if (actionOnVoterRequest.requestType().equals(RequestType.UPDATE_REQUEST)) {
                approveEmail(actionOnVoterRequest.email(),userName,password);
                dslContext.deleteFrom(Tables.ADMIN_LEVEL_VOTERS)
                        .where(Tables.ADMIN_LEVEL_VOTERS.ID.eq(actionOnVoterRequest.id())).execute();
                updateIntoAuthorizedData(actionOnVoterRequest,userName,password);
                approvedByLog(actionOnVoterRequest,userType);
            }
        }else if(actionOnVoterRequest.actionType().equals(ActionType.REJECT)){
            dslContext.deleteFrom(Tables.ADMIN_LEVEL_VOTERS)
                    .where(Tables.ADMIN_LEVEL_VOTERS.ID.eq(actionOnVoterRequest.id())).execute();
            insertIntoUnauthorizedData(actionOnVoterRequest);
            rejectedByLog(actionOnVoterRequest,userType);
            rejectionEmail(actionOnVoterRequest.email());
        }
    }


    private void insertIntoAdminData(ActionOnVoterRequest actionOnVoterRequest) {
        dslContext.insertInto(Tables.ADMIN_LEVEL_VOTERS)
                .columns(
                        Tables.ADMIN_LEVEL_VOTERS.SERIALNUMBER,
                        Tables.ADMIN_LEVEL_VOTERS.NAME,
                        Tables.ADMIN_LEVEL_VOTERS.DESIGNATION,
                        Tables.ADMIN_LEVEL_VOTERS.ORGANIZATION,
                        Tables.ADMIN_LEVEL_VOTERS.PLACEOFPOSTING,
                        Tables.ADMIN_LEVEL_VOTERS.ZONE,
                        Tables.ADMIN_LEVEL_VOTERS.STATE,
                        Tables.ADMIN_LEVEL_VOTERS.MOBILENUMBER,
                        Tables.ADMIN_LEVEL_VOTERS.EMAIL,
                        Tables.ADMIN_LEVEL_VOTERS.REQUESTTYPE)
                .values(actionOnVoterRequest.serialNumber(),
                        actionOnVoterRequest.name(),
                        actionOnVoterRequest.designation(),
                        actionOnVoterRequest.organization(),
                        actionOnVoterRequest.placeOfPosting(),
                        actionOnVoterRequest.zone(),
                        actionOnVoterRequest.state(),
                        actionOnVoterRequest.mobileNumber(),
                        actionOnVoterRequest.email(),
                        AdminLevelVotersRequesttype.valueOf(actionOnVoterRequest.requestType().toString())).execute();
    }


    private void insertIntoUnauthorizedData(ActionOnVoterRequest actionOnVoterRequest) {
        dslContext.insertInto(Tables.VOTERS_UNAUTHORIZED)
                .columns(
                        Tables.VOTERS_UNAUTHORIZED.SERIALNUMBER,
                        Tables.VOTERS_UNAUTHORIZED.NAME,
                        Tables.VOTERS_UNAUTHORIZED.DESIGNATION,
                        Tables.VOTERS_UNAUTHORIZED.ORGANIZATION,
                        Tables.VOTERS_UNAUTHORIZED.PLACEOFPOSTING,
                        Tables.VOTERS_UNAUTHORIZED.ZONE,
                        Tables.VOTERS_UNAUTHORIZED.STATE,
                        Tables.VOTERS_UNAUTHORIZED.MOBILENUMBER,
                        Tables.VOTERS_UNAUTHORIZED.EMAIL,
                        Tables.VOTERS_UNAUTHORIZED.REQUESTTYPE)
                .values(actionOnVoterRequest.serialNumber(),
                        actionOnVoterRequest.name(),
                        actionOnVoterRequest.designation(),
                        actionOnVoterRequest.organization(),
                        actionOnVoterRequest.placeOfPosting(),
                        actionOnVoterRequest.zone(),
                        actionOnVoterRequest.state(),
                        actionOnVoterRequest.mobileNumber(),
                        actionOnVoterRequest.email(),
                        VotersUnauthorizedRequesttype.valueOf(actionOnVoterRequest.requestType().toString())).execute();
    }


    private void insertIntoAuthorizedData(ActionOnVoterRequest actionOnVoterRequest, String userName, String password) {
        dslContext.insertInto(Tables.VOTERS_AUTHORIZED)
                .columns(Tables.VOTERS_AUTHORIZED.USERNAME,
                        Tables.VOTERS_AUTHORIZED.NAME,
                        Tables.VOTERS_AUTHORIZED.DESIGNATION,
                        Tables.VOTERS_AUTHORIZED.ORGANIZATION,
                        Tables.VOTERS_AUTHORIZED.PLACEOFPOSTING,
                        Tables.VOTERS_AUTHORIZED.ZONE,
                        Tables.VOTERS_AUTHORIZED.STATE,
                        Tables.VOTERS_AUTHORIZED.MOBILENUMBER,
                        Tables.VOTERS_AUTHORIZED.EMAIL,
                        Tables.VOTERS_AUTHORIZED.USERTYPE,
                        Tables.VOTERS_AUTHORIZED.PASSWORD)
                .values(userName,
                        actionOnVoterRequest.name(),
                        actionOnVoterRequest.designation(),
                        actionOnVoterRequest.organization(),
                        actionOnVoterRequest.placeOfPosting(),
                        actionOnVoterRequest.zone(),
                        actionOnVoterRequest.state(),
                        actionOnVoterRequest.mobileNumber(),
                        actionOnVoterRequest.email(),
                        VotersAuthorizedUsertype.VOTER, password).execute();
    }



    private void updateIntoAuthorizedData(ActionOnVoterRequest actionOnVoterRequest, String userName, String password) {
        dslContext.update(Tables.VOTERS_AUTHORIZED).
                set(Tables.VOTERS_AUTHORIZED.USERNAME, userName)
                .set(Tables.VOTERS_AUTHORIZED.NAME, actionOnVoterRequest.name())
                .set(Tables.VOTERS_AUTHORIZED.DESIGNATION,actionOnVoterRequest.designation())
                .set(Tables.VOTERS_AUTHORIZED.ORGANIZATION,actionOnVoterRequest.organization())
                .set(Tables.VOTERS_AUTHORIZED.PLACEOFPOSTING,actionOnVoterRequest.placeOfPosting())
                .set(Tables.VOTERS_AUTHORIZED.ZONE,actionOnVoterRequest.zone())
                .set( Tables.VOTERS_AUTHORIZED.STATE,actionOnVoterRequest.state())
                .set( Tables.VOTERS_AUTHORIZED.MOBILENUMBER,actionOnVoterRequest.mobileNumber())
                .set( Tables.VOTERS_AUTHORIZED.EMAIL,actionOnVoterRequest.email())
                .set( Tables.VOTERS_AUTHORIZED.PASSWORD,password)
                .where(Tables.VOTERS_AUTHORIZED.SERIALNUMBER.eq(actionOnVoterRequest.serialNumber())).execute();
    }




    private void approvedByLog(ActionOnVoterRequest actionOnVoterRequest, String userType) {
        dslContext.insertInto(Tables.LOGS)
                .columns(
                        Tables.LOGS.SERIALNUMBER,
                        Tables.LOGS.NAME,
                        Tables.LOGS.DESIGNATION,
                        Tables.LOGS.ORGANIZATION,
                        Tables.LOGS.PLACEOFPOSTING,
                        Tables.LOGS.ZONE,
                        Tables.LOGS.STATE,
                        Tables.LOGS.MOBILENUMBER,
                        Tables.LOGS.EMAIL,
                        Tables.LOGS.REQUESTTYPE,
                        Tables.LOGS.TIME,
                        Tables.LOGS.ACTIONBY,
                        Tables.LOGS.APPROVEDBY)
                .values(actionOnVoterRequest.serialNumber(),
                        actionOnVoterRequest.name(),
                        actionOnVoterRequest.designation(),
                        actionOnVoterRequest.organization(),
                        actionOnVoterRequest.placeOfPosting(),
                        actionOnVoterRequest.zone(),
                        actionOnVoterRequest.state(),
                        actionOnVoterRequest.mobileNumber(),
                        actionOnVoterRequest.email(),
                        LogsRequesttype.valueOf(actionOnVoterRequest.requestType().toString()),
                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),
                        actionOnVoterRequest.actionBy(),
                        userType
                ).execute();
    }

    private void rejectedByLog(ActionOnVoterRequest actionOnVoterRequest, String userType){
        dslContext.insertInto(Tables.LOGS)
                .columns(
                        Tables.LOGS.SERIALNUMBER,
                        Tables.LOGS.NAME,
                        Tables.LOGS.DESIGNATION,
                        Tables.LOGS.ORGANIZATION,
                        Tables.LOGS.PLACEOFPOSTING,
                        Tables.LOGS.ZONE,
                        Tables.LOGS.STATE,
                        Tables.LOGS.MOBILENUMBER,
                        Tables.LOGS.EMAIL,
                        Tables.LOGS.REQUESTTYPE,
                        Tables.LOGS.TIME,
                        Tables.LOGS.ACTIONBY,
                        Tables.LOGS.REJECTEDBY)
                .values(actionOnVoterRequest.serialNumber(),
                        actionOnVoterRequest.name(),
                        actionOnVoterRequest.designation(),
                        actionOnVoterRequest.organization(),
                        actionOnVoterRequest.placeOfPosting(),
                        actionOnVoterRequest.zone(),
                        actionOnVoterRequest.state(),
                        actionOnVoterRequest.mobileNumber(),
                        actionOnVoterRequest.email(),
                        LogsRequesttype.valueOf(actionOnVoterRequest.requestType().toString()),
                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),
                        actionOnVoterRequest.actionBy(),
                        userType
                ).execute();
    }


    public void sendHtmlMessage(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Set to true to indicate HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle the exception as needed
        }
    }

    private void approveEmail(String to, String userName, String password) {
        String htmlContent = "<h1>Welcome to Our Service!</h1>" +
                "<p>Dear User,</p><br/>" +
                "<p>Congratulations! Your registration has been approved.</p><br/>" +
                "<p>You can now login to vote with the following credentials</p><br/>" +
                "<p>UserName : " + userName + "</p>" +
                "<p>Password : "+ password +"</p>" +
                "<p><br/>Please ensure to keep your login details secure.</p><br/>" +
                "<p>Best Regards,<br/>ARTEE Election Team</p>";
        var subject = "ARTEE Election System - Approval Notification";
        sendHtmlMessage(to, subject, htmlContent);
    }

    private void rejectionEmail(String to) {
        String htmlContent = "<h1>Welcome to Our Service!</h1>" +
                "<p>Dear User,</p><br/>" +
                "<p>Thank you for your request, Your request has been rejected.</p><br/>" +
                "<p>For more details, Please contact authority team.</p><br/>" +
                "<p>Best Regards,<br/>ARTEE Election Team</p>";
        var subject = "ARTEE Election System - Rejection Notification";
        sendHtmlMessage(to, subject, htmlContent);
    }


    public  String generateUniqueUsername() {
        String username;
        do {
            username = generateRandomString();
        } while (!isUsernameUnique(username));
        return username;
    }

    private  String generateRandomString() {
        StringBuilder sb = new StringBuilder(USERNAME_LENGTH);
        for (int i = 0; i < ActionOnVoterService.USERNAME_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private boolean isUsernameUnique(String username)  {
        var count = dslContext.fetchCount(DSL.selectFrom(Tables.VOTERS_AUTHORIZED).where(Tables.VOTERS_AUTHORIZED.PASSWORD
                .eq(username)));
        return count == 0;
    }

}
