package com.example.demo.domain.model;

import java.util.List;

public record VoteCountResponse(List<PositionByCount> positionByCounts) {
}
