package com.example.demo.domain.model;


import com.example.demo.domain.model.enums.UserType;

public record LoginResponse(String userName,UserType userType, String name, String loginKey) {


}
