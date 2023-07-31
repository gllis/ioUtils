package com.gllis.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
}
