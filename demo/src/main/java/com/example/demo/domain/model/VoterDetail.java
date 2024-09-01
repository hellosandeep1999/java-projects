package com.example.demo.domain.model;

import java.util.List;

public record VoterDetail(List<Voter> authorizeVoters
,List<Voter> pendingVoters,List<Voter> unAuthorizeVoters, List<Logs> logs) {


}
