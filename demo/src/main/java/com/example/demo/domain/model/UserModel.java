package com.example.demo.domain.model;

import com.example.demo.domain.model.enums.SelectionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


public class UserModel {



    private Long serialNumber;

    private String userName;
    private String name;
    private String designation;
    private String zone;
    private String organization;
    private String state;
    private String email;
    private String voterType;
    private String password;
    private String positionName;
    private SelectionType selectionType;
    private String mobile;
    private String comment;

    public UserModel(Long serialNumber, String userName, String name, String designation, String zone, String organization, String state, String email, String voterType, String password, String positionName, SelectionType selectionType, String mobile, String comment) {
        this.serialNumber = serialNumber;
        this.userName = userName;
        this.name = name;
        this.designation = designation;
        this.zone = zone;
        this.organization = organization;
        this.state = state;
        this.email = email;
        this.voterType = voterType;
        this.password = password;
        this.positionName = positionName;
        this.selectionType = selectionType;
        this.mobile = mobile;
        this.comment = comment;
    }

    public UserModel() {
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVoterType() {
        return voterType;
    }

    public void setVoterType(String voterType) {
        this.voterType = voterType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
