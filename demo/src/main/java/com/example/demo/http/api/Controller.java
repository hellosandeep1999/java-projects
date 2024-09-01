package com.example.demo.http.api;

import com.example.demo.domain.model.*;
import com.example.demo.domain.model.enums.UserType;
import com.example.demo.service.DownLoadDataService;
import com.example.demo.service.OtpService;
import com.example.demo.service.VoterService;
import com.example.demo.service.VotingCountService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {


    public static final String USER_NOT_AUTHORIZED = "Please Verify Credentials!";
    private final VoterService voterService;
    private final DownLoadDataService downLoadDataService;
    private final VotingCountService votingCountService;
    private final OtpService otpService;



    public Controller(VoterService voterService, DownLoadDataService downLoadDataService, VotingCountService votingCountService, OtpService otpService) {
        this.voterService = voterService;
        this.downLoadDataService = downLoadDataService;
        this.votingCountService = votingCountService;
        this.otpService = otpService;
    }

    @GetMapping("/")
    public String home() {
        return "index.html";
    }

    //login API
    @GetMapping("/login")
    public ResponseEntity<LoginResponse> login( @RequestParam String username, @RequestParam String password) {
        var userDetail = voterService.authenticate(username, password);
        return ResponseEntity.ok(userDetail);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String username,@RequestParam String loginKey) {
        if(voterService.verifyLogin(loginKey)){
            throw new RuntimeException(USER_NOT_AUTHORIZED);
        }
        voterService.logout(username);
        return ResponseEntity.ok().build();
    }

    //signup API
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@RequestBody Voter voterRequest) {
        voterService.signup(voterRequest);
        return ResponseEntity.ok(new ResponseMessage("Thank you for signup, Your request sent for Approval"));
    }

    //data-correction API
    @PostMapping("/data-correction")
    public ResponseEntity<ResponseMessage> dataCorrection(@RequestBody Voter voterRequest) {
        voterService.dataCorrection(voterRequest);
        return ResponseEntity.ok(new ResponseMessage("Thank you for data corrections, Your request sent for Approval"));
    }

    //Admin -- Get API All users
    @GetMapping("/admin/get-voter-details")
    public ResponseEntity<VoterDetail> getVoterDetails(@RequestParam String loginKey) {
        if(voterService.verifyLogin(loginKey)){
            throw new RuntimeException(USER_NOT_AUTHORIZED);
        }
        var separateKey = loginKey.split("_");
        var voterDetails = voterService.getVoterDetails(separateKey[1]);
        return ResponseEntity.ok(voterDetails);
    }

    // Admin - see voting count
    @GetMapping("/admin/voting-count-by-category")
    public ResponseEntity<VoteCountResponse> getVotingCountByCategory(@RequestParam String loginKey) {
        if(voterService.verifyLogin(loginKey)){
            throw new RuntimeException(USER_NOT_AUTHORIZED);
        }
        var votingCountByCategory = votingCountService.getVotingCountByCategory();
        return ResponseEntity.ok(votingCountByCategory);
    }

    // Download voters
    @GetMapping("/download-voting-count-by-category")
    public ResponseEntity<PdfResponse> downloadVotingCountByCategory(@RequestParam String loginKey) {
        if(voterService.verifyLogin(loginKey)){
            throw new RuntimeException(USER_NOT_AUTHORIZED);
        }
        String file = downLoadDataService.downloadVotingCountByCategory();
        return ResponseEntity.ok(new PdfResponse("VotingCountByCategory.pdf", file));
    }

    // Action api --> save data into authorized database
    @PostMapping("/admin/action-on-voter")
    public ResponseEntity<VoterDetail> actionOnVoter(@RequestBody ActionOnVoterRequest actionOnVoterRequest) {
        if(voterService.verifyLogin(actionOnVoterRequest.loginKey())){
            throw new RuntimeException(USER_NOT_AUTHORIZED);
        }
        var separateKey = actionOnVoterRequest.loginKey().split("_");
        voterService.actionOnVoter(actionOnVoterRequest,separateKey[1]);
        var voterDetails = voterService.getVoterDetails(separateKey[1]);
        return ResponseEntity.ok(voterDetails);
    }

    // dropdown api zone, state, organization, designation,
    @GetMapping("/get-all-dropdowns")
    public ResponseEntity<DropDownDetails> getAllDropDowns() {
        return ResponseEntity.ok(voterService.getAllDropDowns());
    }


    // Download voters
    @GetMapping("/download-voter-list")
    public ResponseEntity<PdfResponse> downloadVoterList() {
        String file = downLoadDataService.getFileForVoterList();
        return ResponseEntity.ok(new PdfResponse("voters.pdf", file));
    }

    // Voting API
    @GetMapping("/voter/get-voting-data-for-voting")
    public ResponseEntity<VotingDataDetail> getVotingDataForVoting(@RequestParam String loginKey)  {
        if(voterService.verifyLogin(loginKey)){
            throw new RuntimeException(USER_NOT_AUTHORIZED);
        }
        if(voterService.isAlreadyVoted(loginKey)){
            return ResponseEntity.ok(new VotingDataDetail(null,"You have been already voted, Thanks for Voting!",true));
        }
        if(!voterService.isZoneEligible(loginKey)){
            return ResponseEntity.ok(new VotingDataDetail(null,"Your zone not eligible for voting at this time.",true));
        }
        var votingData = votingCountService.getVotingDataForVoting(loginKey);
        return ResponseEntity.ok(new VotingDataDetail(votingData,null,false));
    }

    //submit vote
    @PutMapping("/voter/submit-vote")
    public ResponseEntity<ResponseMessage> submitVote(@RequestBody VotingRequest votingRequest) {
        if(voterService.verifyLogin(votingRequest.loginKey())){
            throw new RuntimeException(USER_NOT_AUTHORIZED);
        }
        if(votingRequest.voteByPositions().isEmpty()){
            throw new RuntimeException("Please choose someone for voting!");
        }
        votingCountService.submitVote(votingRequest);
        return ResponseEntity.ok(new ResponseMessage("Thanks for voting!"));
    }

    // Download Voting response
    @GetMapping("/voter/download-voting-response")
    public ResponseEntity<PdfResponse> downloadVotingResponse(@RequestParam String loginKey) {
        if(voterService.verifyLogin(loginKey)){
            throw new RuntimeException(USER_NOT_AUTHORIZED);
        }
        String file = downLoadDataService.getFileForVotingResponse(loginKey);
        return ResponseEntity.ok(new PdfResponse("VotingResponse.pdf", file));
    }

    // Download voters
    @GetMapping("/download-logs")
    public ResponseEntity<PdfResponse> downloadLogs(@RequestParam String loginKey) {
        if(voterService.verifyLogin(loginKey)){
            throw new RuntimeException(USER_NOT_AUTHORIZED);
        }
        String file = downLoadDataService.getFileForLogs();
        return ResponseEntity.ok(new PdfResponse("logs.pdf", file));
    }

    @GetMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@RequestParam String mobileNumber) throws IOException {
        otpService.sendOtp(mobileNumber);
        return ResponseEntity.ok(new OtpResponse(true,false, "OTP sent successfully!"));
    }

    @GetMapping("/verify-otp")
    public ResponseEntity<OtpResponse> sendOtp(@RequestParam String mobileNumber, @RequestParam String otp) throws IOException {
        otpService.verifyOtp(mobileNumber,otp);
        return ResponseEntity.ok(new OtpResponse(false,true,"OTP Verified!"));
    }

    @GetMapping("/save-password")
    public ResponseEntity<OtpResponse> savePassword(@RequestParam String mobileNumber, @RequestParam String password) throws IOException {
        otpService.savePassword(mobileNumber,password);
        return ResponseEntity.ok(new OtpResponse(false,false,"Your Password updated successfully!"));
    }

    @GetMapping("/tabs-visibility")
    public ResponseEntity<TabsResponse> tabsVisibility() {
        return ResponseEntity.ok(voterService.tabsVisibility());
    }



     // Work
        // Github push
        // document
        // press

    // Final-CHECK


//paras
        // ATREE Election 2024 on loin screen
        // Zone wise message handling on voting page
        // Password 8 characters
        // Tabs visibility api
        // After refresh page going signout issue
        // Mobile no. validation on login, signup, update







    @PostMapping("/upload-users")
    public ResponseEntity<List<UserModel>> uploadUserFile(@RequestPart(value = "file",required = true) MultipartFile file, @RequestParam(defaultValue = "false") boolean isSaveData) throws IOException {
        var data = downLoadDataService.uploadUserFile(file,isSaveData);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/upload-candidates")
    public ResponseEntity<List<UserModel>> uploadCandidates(@RequestPart(value = "file",required = true) MultipartFile file, @RequestParam(defaultValue = "false") boolean isSaveData) throws IOException {
        var data = downLoadDataService.uploadCandidates(file,isSaveData);
        return ResponseEntity.ok(data);
    }

}
