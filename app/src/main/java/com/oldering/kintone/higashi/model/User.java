package com.oldering.kintone.higashi.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class User {
    private long id;
    private String code;
    private String name;
    private String surName;
    private String givenName;
    private String timezone;
    private PhotoUrlContainer photo;
    private String email;
    private String employeeNumber;
}