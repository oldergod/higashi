package com.oldering.kintone.higashi.model;

import java.util.Date;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Notification {
    private Date sentTime;
    private Boolean read;
    private Boolean mention;
    private Boolean flagged;
    private String groupKey;
    private int groupKeyCount;
    private Content content;
}
