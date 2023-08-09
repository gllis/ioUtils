package com.gllis.util;

import com.gllis.conf.AppConstant;
import io.netty.util.internal.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 配置文件工具类
 *
 * @author gllis
 * @date 2023/7/31
 */
public class AppConfUtils {

    /**
     * 获取配置文件
     *
     * @return
     * @throws IOException
     */
    public static Properties get() throws IOException {
        File file = getAppCfgFile();
        Properties props = new Properties();
        // 使用InPutStream流读取properties文件
        props.load(new FileInputStream(file));
        return props;
    }

    /**
     * 获取host列表
     *
     * @param key
     * @return
     */
    public static String[] getHosts(String key) {
        String[] hosts;
        try {
            String value = get().getProperty(key);
            if (!StringUtil.isNullOrEmpty(value)) {
                String[] tmpArr = value.split(",", 5);
                hosts = new String[tmpArr.length];
                for (int i = 0; i < tmpArr.length; i++) {
                    hosts[i] = tmpArr[i].split(":")[0];
                }
            } else {
                hosts = new String[]{};
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return hosts;
    }

    /**
     * 获取端口
     *
     * @param key
     * @return
     */
    public static String getPort(String key, String ip) {
        if (ip == null) {
            return null;
        }
        String port = null;
        try {
            String value = get().getProperty(key);
            if (!StringUtil.isNullOrEmpty(value)) {
                String[] tmpArr = value.split(",", 5);
                for (String tmp : tmpArr) {
                    String[] ips = tmp.split(":");
                    if (ip.equals(ips[0])) {
                        port = ips[1];
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return port;
    }

    /**
     * 更新配置
     *
     * @param key
     * @param value
     */
    public static void update(String key, Object value) {
        try {
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            Properties props = get();
            OutputStream fos = new FileOutputStream(getAppCfgFile());
            props.setProperty(key, String.valueOf(value));
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            props.store(fos, "Update '" + key + "' value");
        } catch (IOException e) {
            System.err.println("属性文件更新错误");
        }
    }

    /**
     * 更新host 记录 最多记录5条记录
     *
     * @param host
     * @param port
     */
    public static void updateHost(String key, String host, Integer port) {
        try {
            String hosts = get().getProperty(key);
            String hostKey = String.format("%s:%s", host, port);
            if (hosts == null) {
                update(key, hostKey);
                return;
            }
            if (hosts.contains(hostKey)) {
                return;
            }
            String[] hostArr = hosts.split(",", 5);
            StringBuilder sb = new StringBuilder(hostKey);
            int len = hostArr.length >= 5 ? 4 : hostArr.length;
            for (int i = 0; i < len; i++) {
                String[] tmp = hostArr[i].split(":");
                sb.append(",");
                sb.append(String.format("%s:%s", tmp[0], tmp[1]));
            }
            update(key, sb.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取配置文件
     *
     * @return
     * @throws IOException
     */
    private static File getAppCfgFile() throws IOException {
        StringBuffer cfgPath = new StringBuffer(AppConstant.HOME_DIR);
        cfgPath.append(File.separator).append(AppConstant.CFG_DIR);
        File file = new File(cfgPath.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        cfgPath.append(File.separator).append(AppConstant.CFG_NAME);
        file = new File(cfgPath.toString());
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


}
