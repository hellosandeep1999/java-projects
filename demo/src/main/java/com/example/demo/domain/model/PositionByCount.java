package com.example.demo.domain.model;

import java.util.List;

public record PositionByCount(String positionName, List<CandidateVoteCount> candidateVoteCounts) {
}
