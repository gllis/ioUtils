package com.gllis.util;

import com.gllis.conf.AppConstant;

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
