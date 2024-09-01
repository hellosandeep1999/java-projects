package com.example.demo.domain.model;

import java.util.List;

public record VotingRequest(String loginKey, List<VoteByPosition> voteByPositions) {
}
