package com.oldering.kintone.higashi.model;

import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiResponse {
    private ApiResponse() {
    }

    private String code;
    private boolean success;
    private String message;
    private JsonObject errors;
    private JsonObject result;
}
