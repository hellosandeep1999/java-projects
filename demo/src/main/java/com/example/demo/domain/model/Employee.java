package com.example.demo.domain.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "name", "address"})
public class Employee {

    @JsonProperty(value = "id",required = true)
    private Long id;
    @JsonProperty(value = "name",required = true)
    private String name;

    @JsonProperty(value = "address",required = true)
    private String address;

    public Employee(
            @JsonProperty(value = "idd",required = true)  Long id,
            @JsonProperty(value = "name",required = true) String name,
            @JsonProperty(value = "address",required = true) String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
