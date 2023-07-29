package com.gllis.util;

/**
 * 系统工具
 *
 * @author gllis
 * @date 2023/7/27
 */
public class SystemUtil {
    public static final String OS_NAME = System.getProperty("os.name");
    public static boolean isMacOs() {
        return OS_NAME.contains("Mac");
    }
}
