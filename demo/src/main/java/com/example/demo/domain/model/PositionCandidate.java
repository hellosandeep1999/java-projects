package com.example.demo.domain.model;

import java.util.List;

public record PositionCandidate(String positionName, List<ChooseVote> candidates) {
}
