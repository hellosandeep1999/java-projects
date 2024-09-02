package com.example.demo.domain.model;

import com.example.demo.domain.model.enums.RequestType;

public record Voter(Long id, Long serialNumber,
                    String name,
                    String designation,
                    String organization,
                    String placeOfPosting,
                    String zone,
                    String state,
                    String mobileNumber,
                    String email,
                    RequestType requestType) {
}
