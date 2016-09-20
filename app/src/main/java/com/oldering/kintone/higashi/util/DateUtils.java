package com.oldering.kintone.higashi.util;

import android.annotation.SuppressLint;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @overview
 */

public class DateUtils {
    private static final String TAG = "DateUtils";

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat formatterLong = new SimpleDateFormat("y'/'MM'/'dd HH:mm");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat formatterMedium = new SimpleDateFormat("MM'/'dd HH:mm");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat formatterShort = new SimpleDateFormat("HH:mm");

    private DateUtils() {
    }

    public static String format(Date date) {
        LocalDateTime input = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thisMorning = now.withHour(0).withMinute(0);
        if (input.isAfter(thisMorning)) {
            return formatterShort.format(date);
        }
        LocalDateTime lastYear = thisMorning.withDayOfYear(1);
        if (input.isAfter(lastYear)) {
            return formatterMedium.format(date);
        }
        return formatterLong.format(date);
    }
}
