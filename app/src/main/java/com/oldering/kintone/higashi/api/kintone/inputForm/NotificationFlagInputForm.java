package com.oldering.kintone.higashi.api.kintone.inputForm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NotificationFlagInputForm {
    private String groupKey;
    private boolean flagged;
}
