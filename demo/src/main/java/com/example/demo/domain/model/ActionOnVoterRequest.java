package com.example.demo.domain.model;


import com.example.demo.domain.model.enums.ActionType;
import com.example.demo.domain.model.enums.RequestType;
import com.example.demo.domain.model.enums.UserType;

public record ActionOnVoterRequest(String loginKey, Long id, Long serialNumber,
                                   String name, String designation, String organization, String zone, String state,
                                   String mobileNumber, String email, ActionType actionType, RequestType requestType,
                                   String actionBy) {
}
