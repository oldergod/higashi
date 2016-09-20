package com.oldering.kintone.higashi.api.kintone.inputForm;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class NotificationMarkInputForm {
    private List<MessageForm> messages;

    public void addMessage(long baseId, String groupKey, boolean read) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(new MessageForm(baseId, groupKey, read));
    }

    @Getter
    @Setter
    @ToString
    private static class MessageForm {
        private long baseId;
        private String groupKey;
        private boolean read;

        MessageForm(long baseId, String groupKey, boolean read) {
            this.baseId = baseId;
            this.groupKey = groupKey;
            this.read = read;
        }
    }
}
