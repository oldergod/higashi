package com.oldering.kintone.higashi.model;


import java.util.List;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class NotificationsWrapper {
    private boolean hasMore;
    private List<NotificationM> notifications;
}
