package com.oldering.kintone.higashi.api.kintone.inputForm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NotificationsGetInputForm {
    // dont know what it is for yet
//    private long baseId;
    private boolean checkNew = false;
    /**
     * Filters by Read/unread flag.
     * can be null yo to get both
     */
//    private boolean read;
    /**
     * Filters by mentioned flag.
     */
    private boolean mentioned;
    /**
     * Filters by flagged.
     */
    private boolean flagged;
    /**
     * Filters by filter.
     */
//    private long filterId;
    /**
     * Size of notification to fetch.
     * default: 50
     */
//    private int size;
    /**
     * Offset
     * default: 0
     */
//    private int offset;
    /**
     * If the user doesn't have mentioned notifications, the API returns whether he has non-mentioned notifications or
     * not.
     */
    private boolean checkIgnoreMention;
}
