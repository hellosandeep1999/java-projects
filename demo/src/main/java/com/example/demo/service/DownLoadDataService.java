package com.example.demo.service;

import com.example.demo.domain.model.*;
import com.example.demo.domain.model.enums.RequestType;
import com.example.demo.domain.model.enums.SelectionType;
import com.example.demo.generated.jooq.Tables;
import com.example.demo.generated.jooq.enums.CandidateAndVotingSelectiontype;
import com.example.demo.generated.jooq.enums.VotersAuthorizedUsertype;
import com.example.demo.generated.jooq.tables.records.CandidateAndVotingRecord;
import com.example.demo.generated.jooq.tables.records.PositionsRecord;
import com.example.demo.generated.jooq.tables.records.VotersAuthorizedRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class DownLoadDataService {

    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);


    @Autowired
    DSLContext dslContext;

    @Autowired
    VotingCountService votingCountService;

    @Autowired
    VoterService voterService;

    @Autowired
    ActionOnVoterService actionOnVoterService;




    public String getFileForVoterList() {
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

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            var title = new Paragraph(" ARTEE ELECTION 2024 ", BOLD_FONT);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Voters List", BOLD_FONT));
            document.add(new Paragraph(" ")); // Add a blank line for spacing

            // Create a table with 3 columns
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 4f, 2f,2f,2f,2f,4f,4f});
            // Define column headers
            var list = List.of("Serial Number","Name","Designation","Organization","Zone","State","Mobile Number","Email");
            addTableHeader(table,list);
            // Add rows to the table
            for (Voter voter : authorizeVoters) {
                table.addCell(voter.serialNumber().toString());
                table.addCell(voter.name());
                table.addCell(voter.designation());
                table.addCell(voter.organization());
                table.addCell(voter.zone());
                table.addCell(voter.state());
                table.addCell(voter.mobileNumber());
                table.addCell(voter.email());
            }
            // Add the table to the document
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return Base64.encodeBase64String(baos.toByteArray());
    }

    private void addTableHeader(PdfPTable table, List<String> list) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        for(String name : list){
            PdfPCell header = new PdfPCell(new Paragraph(name, headerFont));
            table.addCell(header);
        }
    }


    public String getFileForLogs() {
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

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            var title = new Paragraph(" ARTEE ELECTION 2024 ", BOLD_FONT);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Logs", BOLD_FONT));
            document.add(new Paragraph(" ")); // Add a blank line for spacing


            PdfPTable table = new PdfPTable(11);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 4f, 3f,2f,2f,3f,4f,5f,3f,2f,2f});
            // Define column headers
            var list = List.of("Serial Number","Name","Designation","Organization",
                    "Zone","State","Mobile Number","Email","Request Type","Action By","Reject By");
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
            for(String name : list){
                PdfPCell header = new PdfPCell(new Paragraph(name, headerFont));
                table.addCell(header);
            }
            // Add rows to the table
            Font contentFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
            for (Logs log : logs) {
                var serialNumber = log.serialNumber() != null ? log.serialNumber().toString(): "";
                table.addCell(new PdfPCell(new Paragraph(serialNumber,contentFont)));
                table.addCell(new PdfPCell(new Paragraph(log.name(),contentFont)));
                table.addCell(new PdfPCell(new Paragraph(log.designation(),contentFont)));
                table.addCell(new PdfPCell(new Paragraph(log.organization(),contentFont)));
                table.addCell(new PdfPCell(new Paragraph(log.zone(),contentFont)));
                table.addCell(new PdfPCell(new Paragraph(log.state(),contentFont)));
                table.addCell(new PdfPCell(new Paragraph(log.mobileNumber(),contentFont)));
                table.addCell(new PdfPCell(new Paragraph(log.email(),contentFont)));
                table.addCell(new PdfPCell(new Paragraph(String.valueOf(log.requestType()),contentFont)));
                table.addCell(new PdfPCell(new Paragraph(log.actionBy(),contentFont)));
                table.addCell(new PdfPCell(new Paragraph(log.rejectBy(),contentFont)));
            }
            // Add the table to the document
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return Base64.encodeBase64String(baos.toByteArray());
    }

    public String downloadVotingCountByCategory() {
        var voteCountResponse = votingCountService.getVotingCountByCategory();
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            var title = new Paragraph(" ARTEE ELECTION 2024 ", BOLD_FONT);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Voting Count By Category", BOLD_FONT));
            document.add(new Paragraph(" ")); // Add a blank line for spacing

            for(var response : voteCountResponse.positionByCounts()){

                document.add(new Paragraph(response.positionName(), BOLD_FONT));

                PdfPTable table = new PdfPTable(2);
                table.setWidths(new float[]{8f, 5f});
                var list = List.of("Candidate Name","Count");
                addTableHeader(table,list);
                for (var candidate : response.candidateVoteCounts()) {
                    table.addCell(candidate.candidateName());
                    table.addCell(candidate.count().toString());
                }
                document.add(table);
            }
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return Base64.encodeBase64String(baos.toByteArray());
    }

    public String getFileForVotingResponse(String loginKey) {
        var votingResponse = dslContext.select(Tables.VOTERS_AUTHORIZED.NAME,Tables.VOTERS_AUTHORIZED.VOTINGRESPONSE).
                from(Tables.VOTERS_AUTHORIZED)
                .where(Tables.VOTERS_AUTHORIZED.LOGINKEY.eq(loginKey)).fetch();

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        var name = votingResponse.get(0).get(Tables.VOTERS_AUTHORIZED.NAME);
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            var title = new Paragraph(" ARTEE ELECTION 2024 ", BOLD_FONT);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Dear "+name+", Your voting response ", BOLD_FONT));
            document.add(new Paragraph(" Thanks for Voting! "));
            document.add(new Paragraph(" "));

            var votingRequest = getVotingRequest(votingResponse);

            PdfPTable table = new PdfPTable(2);
            table.setWidths(new float[]{5f, 5f});
            var list = List.of("Position Name","Candidate Name");
            addTableHeader(table,list);
            for (var position : votingRequest.voteByPositions()) {
                table.addCell(position.positionName());
                table.addCell(position.CandidateName());
            }
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
        return Base64.encodeBase64String(baos.toByteArray());
    }

    private VotingRequest getVotingRequest(Result<Record2<String, String>> votingResponse) {
        try {
            if(votingResponse!=null&&votingResponse.get(0).get(Tables.VOTERS_AUTHORIZED.VOTINGRESPONSE) != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(votingResponse.get(0).get(Tables.VOTERS_AUTHORIZED.VOTINGRESPONSE), VotingRequest.class);
            }else {
                return new VotingRequest("",List.of());
            }
            } catch (IOException e) {
           throw new RuntimeException(e.getMessage());
        }
    }


    public List<UserModel> uploadUserFile(MultipartFile file, boolean isSaveData) {

        var invalidModels = new ArrayList<UserModel>();
        var validModels = new ArrayList<UserModel>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                // Extract header row
                Row headerRow = sheet.getRow(0);
                Map<String, Integer> headerMap = new HashMap<>();
                for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                    headerMap.put(headerRow.getCell(i).getStringCellValue(), i);
                }

                var dropDowns = voterService.getAllDropDowns();
                var states = dropDowns.states().stream().map(DropDownData::name).toList();
                var zones = dropDowns.zones().stream().map(DropDownData::name).toList();
                var designations = dropDowns.designation().stream().map(DropDownData::name).toList();
                var organizations = dropDowns.organization().stream().map(DropDownData::name).toList();

                // Iterate over rows and create Employee objects
                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    UserModel userModel = new UserModel();
                    if(i%10 ==0){
                        System.out.println(i+" Rows Extracted");
                    }
                    var status = extracted(headerMap, row, userModel,states,zones,designations,organizations, List.of(),false);
                    if(status){
                        validModels.add(userModel);
                    }else {
                        invalidModels.add(userModel);
                    }
                }
                System.out.println("Total "+sheet.getPhysicalNumberOfRows()+" Rows Extracted");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Size of valid -> "+validModels.size());
        System.out.println("Size of Invalid -> "+invalidModels.size());
        if(invalidModels.isEmpty() && isSaveData && isNotDuplicate(validModels)){
            System.out.println("Data Sent for insertion");
            saveDataInAuthorized(validModels);
        }
        return invalidModels;
    }


    private boolean extracted(Map<String, Integer> headerMap, Row row, UserModel userModel
    , List<String> states, List<String> zones, List<String> designations, List<String> organizations, List<String> positions, boolean isForCandidate) {

        // Extract values from row and map to Employee object
        boolean status = true;
        if (headerMap.containsKey("Serial number")) {
            var cell = row.getCell(headerMap.get("Serial number"));
            if(cell != null){
                userModel.setSerialNumber((long)cell.getNumericCellValue());
            }else {
                userModel.setComment("serial number null");
                status = false;
            }
        }
        if (headerMap.containsKey("Name")) {
            var cell = row.getCell(headerMap.get("Name"));
            if(cell != null){
                userModel.setName(cell.getStringCellValue());
            }else {
                userModel.setComment(userModel.getComment() + "Name value null");
                status = false;
            }
        }else {
            userModel.setComment(userModel.getComment() + "Name column not found");
            status = false;
        }
        if (headerMap.containsKey("Zone")) {
            var cell = row.getCell(headerMap.get("Zone"));
            if(cell != null && zones.contains(cell.getStringCellValue())){
                userModel.setZone(cell.getStringCellValue());
            }else {
                userModel.setComment(userModel.getComment() + "Zone value null");
                status = false;
            }
        }else {
            userModel.setComment(userModel.getComment() + "Zone column not found");
            status = false;
        }
        if (headerMap.containsKey("State")) {
            var cell = row.getCell(headerMap.get("State"));
            if(cell != null && states.contains(cell.getStringCellValue())){
                userModel.setState(cell.getStringCellValue());
            }else {
                userModel.setComment(userModel.getComment() + "State value null");
                status = false;
            }
        }else {
            userModel.setComment(userModel.getComment() + "State column not found");
            status = false;
        }
        if (headerMap.containsKey("Designation")) {
            var cell = row.getCell(headerMap.get("Designation"));
            if(cell != null && designations.contains(cell.getStringCellValue())){
                userModel.setDesignation(cell.getStringCellValue());
            }else {
                userModel.setComment(userModel.getComment() + "Designation value null");
                status = false;
            }
        }else {
            userModel.setComment(userModel.getComment() + "Designation column not found");
            status = false;
        }
        if (headerMap.containsKey("Organisation")) {
            var cell = row.getCell(headerMap.get("Organisation"));
            if(cell != null && organizations.contains(cell.getStringCellValue())){
                userModel.setOrganization(cell.getStringCellValue());
            }else {
                userModel.setComment(userModel.getComment() + "Organisation value null");
                status = false;
            }
        }else {
            userModel.setComment(userModel.getComment() + "Organisation column not found");
            status = false;
        }
        if (headerMap.containsKey("Place of Posting")) {
            var cell = row.getCell(headerMap.get("Place of Posting"));
            if(cell != null){
                userModel.setPlaceOfPosting(cell.getStringCellValue());
            }
        }
        if (headerMap.containsKey("email id")) {
            var cell = row.getCell(headerMap.get("email id"));
            if(cell != null){
                userModel.setEmail(cell.getStringCellValue());
            }else {
                status = false;
            }
        }
        if (headerMap.containsKey("Mobile")) {
            var cell = row.getCell(headerMap.get("Mobile"));
            if(cell != null) {
                userModel.setMobile(convertNumericCellToString(cell));
                if(!isForCandidate){
                    userModel.setUserName(convertNumericCellToString(cell));
                    userModel.setPassword(actionOnVoterService.generateUniqueUsername());
                }
            }else {
                userModel.setComment(userModel.getComment() + "Mobile value null");
                status = false;
            }
        }else {
            userModel.setComment(userModel.getComment() + "Mobile column not found");
            status = false;
        }
        // this is for candidate files
        if (isForCandidate && headerMap.containsKey("position")) {
            var cell = row.getCell(headerMap.get("position"));
            if(cell != null && positions.contains(cell.getStringCellValue())){
                userModel.setPositionName(cell.getStringCellValue());
            }else {
                userModel.setComment(userModel.getComment() + "position value null");
                status = false;
            }
        }else {
            userModel.setComment(userModel.getComment() + "position column not found");
            status = false;
        }
        // this is for candidate files
        if (isForCandidate && headerMap.containsKey("Selection Type")) {
            var cell = row.getCell(headerMap.get("Selection Type"));
            var allSelectionTypes = Arrays.stream(SelectionType.class.getEnumConstants()).map(Enum::name).toList();
            if(cell != null && allSelectionTypes.contains(cell.getStringCellValue())){
                userModel.setSelectionType(SelectionType.valueOf(cell.getStringCellValue()));
            }else {
                userModel.setComment(userModel.getComment() + "Selection Type value null");
                status = false;
            }
        }else {
            userModel.setComment(userModel.getComment() + "Selection Type column not found");
            status = false;
        }
        return status;
    }


    private boolean isNotDuplicate(ArrayList<UserModel> validModels) {
        System.out.println("Checking Duplicate Rows");
        var allUserNames = dslContext.select(Tables.VOTERS_AUTHORIZED.USERNAME).
                from(Tables.VOTERS_AUTHORIZED).fetchInto(String.class);
        var seenMobile = new HashSet<>(allUserNames);
        AtomicBoolean flag = new AtomicBoolean(true);
        var duplicate = new HashSet<>();
        validModels.forEach(model->{
            if(seenMobile.contains(model.getUserName())){
                duplicate.add(model.getUserName());
                flag.set(false);
            }
            seenMobile.add(model.getUserName());
        });
        if(!flag.get()){
            throw new RuntimeException("Some mobile numbers are duplicate "+ duplicate);
        }
        return true;
    }

    private void saveDataInAuthorized(ArrayList<UserModel> validModels) {
        dslContext.batchInsert(validModels.stream().map(model-> {
                    var record = new VotersAuthorizedRecord();
                    record.set(Tables.VOTERS_AUTHORIZED.USERNAME, model.getMobile());
                    record.set(Tables.VOTERS_AUTHORIZED.NAME, model.getName());
                    record.set(Tables.VOTERS_AUTHORIZED.DESIGNATION, model.getDesignation());
                    record.set(Tables.VOTERS_AUTHORIZED.ORGANIZATION, model.getOrganization());
                    record.set(Tables.VOTERS_AUTHORIZED.PLACEOFPOSTING, model.getPlaceOfPosting());
                    record.set(Tables.VOTERS_AUTHORIZED.ZONE, model.getZone());
                    record.set(Tables.VOTERS_AUTHORIZED.STATE, model.getState());
                    record.set(Tables.VOTERS_AUTHORIZED.MOBILENUMBER, model.getMobile());
                    record.set(Tables.VOTERS_AUTHORIZED.EMAIL, model.getEmail());
                    record .set(Tables.VOTERS_AUTHORIZED.USERTYPE, VotersAuthorizedUsertype.VOTER);
                    record .set(Tables.VOTERS_AUTHORIZED.PASSWORD, model.getPassword());
                    return record;
                }
        ).toList()).execute();
        System.out.println("Data Inserted Successfully");
    }

    private String convertNumericCellToString(Cell cell) {
        String result = "";
        switch (cell.getCellType()) {
            case NUMERIC:
                NumberFormat numberFormat = new DecimalFormat("0");
                result = numberFormat.format(cell.getNumericCellValue());
                break;
            case STRING:
                result = cell.getStringCellValue();
                break;
            default:
                throw new IllegalArgumentException("Unexpected cell type");
        }
        return result;
    }



    public List<UserModel> uploadCandidates(MultipartFile file, boolean isSaveData) {
        var invalidModels = new ArrayList<UserModel>();
        var validModels = new ArrayList<UserModel>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                // Extract header row
                Row headerRow = sheet.getRow(0);
                Map<String, Integer> headerMap = new HashMap<>();
                for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                    headerMap.put(headerRow.getCell(i).getStringCellValue(), i);
                }

                var dropDowns = voterService.getAllDropDowns();
                var states = dropDowns.states().stream().map(DropDownData::name).toList();
                var zones = dropDowns.zones().stream().map(DropDownData::name).toList();
                var designations = dropDowns.designation().stream().map(DropDownData::name).toList();
                var organizations = dropDowns.organization().stream().map(DropDownData::name).toList();
                var positions = dslContext.selectFrom(Tables.POSITIONS)
                        .fetch().map(PositionsRecord::component2).stream().toList();

                // Iterate over rows and create Employee objects
                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    UserModel userModel = new UserModel();
                    if(i%10 ==0){
                        System.out.println(i+" Rows Extracted");
                    }
                    var status = extracted(headerMap, row, userModel,states,zones,designations,organizations
                    ,positions, true);
                    if(status){
                        validModels.add(userModel);
                    }else {
                        invalidModels.add(userModel);
                    }
                }
                System.out.println("Total "+sheet.getPhysicalNumberOfRows()+" Rows Extracted");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Size of valid -> "+validModels.size());
        System.out.println("Size of Invalid -> "+invalidModels.size());
        if(invalidModels.isEmpty() && isSaveData && isNotDuplicateCandidate(validModels)){
            System.out.println("Data Sent for insertion");
            saveDataInCandidates(validModels);
        }
        return invalidModels;
    }

    private boolean isNotDuplicateCandidate(ArrayList<UserModel> validModels) {
        System.out.println("Checking Duplicate Rows");
        var allUserMobileNumbers = dslContext.select(Tables.CANDIDATE_AND_VOTING.MOBILENUMBER).
                from(Tables.CANDIDATE_AND_VOTING).fetchInto(String.class);
        var seenMobile = new HashSet<>(allUserMobileNumbers);
        AtomicBoolean flag = new AtomicBoolean(true);
        var duplicate = new HashSet<>();
        validModels.forEach(model->{
            if(seenMobile.contains(model.getMobile())){
                duplicate.add(model.getMobile());
                flag.set(false);
            }
            seenMobile.add(model.getMobile());
        });
        if(!flag.get()){
            throw new RuntimeException("Some mobile numbers are duplicate "+ duplicate);
        }
        return true;
    }

    private void saveDataInCandidates(ArrayList<UserModel> validModels) {
        dslContext.batchInsert(validModels.stream().map(model-> {
                    var record = new CandidateAndVotingRecord();
                    record.set(Tables.CANDIDATE_AND_VOTING.NAME, model.getName());
                    record.set(Tables.CANDIDATE_AND_VOTING.DESIGNATION, model.getDesignation());
                    record.set(Tables.CANDIDATE_AND_VOTING.ORGANIZATION, model.getOrganization());
                    record.set(Tables.CANDIDATE_AND_VOTING.ORGANIZATION, model.getPlaceOfPosting());
                    record.set(Tables.CANDIDATE_AND_VOTING.ZONE, model.getZone());
                    record.set(Tables.CANDIDATE_AND_VOTING.STATE, model.getState());
                    record.set(Tables.CANDIDATE_AND_VOTING.MOBILENUMBER, model.getMobile());
                    record.set(Tables.CANDIDATE_AND_VOTING.EMAIL, model.getEmail());
                    record.set(Tables.CANDIDATE_AND_VOTING.POSITION, model.getPositionName());
                    record.set(Tables.CANDIDATE_AND_VOTING.SELECTIONTYPE, CandidateAndVotingSelectiontype.valueOf(model.getSelectionType().name()));
                    return record;
                }
        ).toList()).execute();
        System.out.println("Data Inserted Successfully");
    }
}
