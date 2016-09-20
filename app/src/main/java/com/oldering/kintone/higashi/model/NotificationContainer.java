package com.oldering.kintone.higashi.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NotificationContainer {
    Notification item;
    User sender;
}
