package com.oldering.kintone.higashi.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class NotificationM {
    private long id;
    private Date sentTime;
    private boolean read;
    private boolean mentioned;
    private boolean flagged;
    private Long eventId;
    private String moduleType;
    private Long moduleId;
    private Long sender;
    private String senderName;
    private String senderPhoto;
    private String title;
    private String subTitle;
    private String message;
    private String icon;
    private String url;
    private String groupKey;
    private Integer groupKeyCount;

    private boolean isLiked = false;
}
