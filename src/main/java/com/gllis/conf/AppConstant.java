package com.gllis.conf;

/**
 * app 常量
 *
 * @author gllis
 * @date 2023/7/31
 */
public interface AppConstant {
    String HOME_DIR = System.getProperty("user.home");
    String CFG_DIR = ".io";
    String CFG_NAME = "app.cfg";

    String TCP_IP = "tcp.ip";
    String TCP_PORT = "tcp.port";
    String TCP_LAST_SEND = "tcp.last.send";

    String UDP_IP = "udp.ip";
    String UDP_PORT = "udp.port";
    String UDP_LAST_SEND = "udp.last.send";

}
