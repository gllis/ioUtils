package com.gllis.util;

import io.netty.util.internal.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author gllis
 * @date 2023/7/31
 */
public class DateUtil {
    public static final ZoneId zoneId = ZoneId.systemDefault();

    public static String now() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        return dateFormat.format(LocalDateTime.now());
    }

    public static String format(String timestamp) {
        if (StringUtil.isNullOrEmpty(timestamp)) {
            return null;
        }
        long ts = Long.parseLong(timestamp);
        if (timestamp.length() == 10) {
            ts = ts * 1000;
        }
        Date date = new Date(ts);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String parse(String dateString) {
        if (StringUtil.isNullOrEmpty(dateString)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date != null ? String.valueOf(date.getTime()) : null;
    }
}
