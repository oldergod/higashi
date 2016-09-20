package com.oldering.kintone.higashi.model;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

/**
 * @overview
 */

@Getter
@ToString
public class Content {
    private Data title;
    private Data subTitle;
    private Data message;

    private String icon;
    private List<String> contents;
}
