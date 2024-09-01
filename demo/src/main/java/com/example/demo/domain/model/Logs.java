package com.example.demo.domain.model;

import com.example.demo.domain.model.enums.RequestType;

public record Logs( Long serialNumber,
                   String name,
                   String designation,
                   String organization,
                   String zone,
                   String state,
                   String mobileNumber,
                   String email,
                   RequestType requestType,
                   String time,
                   String actionBy,
                   String approvedBy,
                   String rejectBy) {
}
