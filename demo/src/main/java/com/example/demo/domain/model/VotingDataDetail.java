package com.example.demo.domain.model;

import java.util.List;

public record VotingDataDetail(List<PositionCandidate> positionCandidates, String message,boolean download) {
}
