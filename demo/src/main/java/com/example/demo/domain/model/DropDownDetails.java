package com.example.demo.domain.model;

import java.util.List;

public record DropDownDetails( List<DropDownData> zones, List<DropDownData> states, List<DropDownData> organization,
                             List<DropDownData> designation) {
}
