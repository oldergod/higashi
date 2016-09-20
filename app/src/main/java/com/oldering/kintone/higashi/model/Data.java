package com.oldering.kintone.higashi.model;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Data {
    // RESOURCE, PLAIN, HTML, IUSER, APP_NAME
    private String dataType;
    private String text;
    private List<String> args;
}
