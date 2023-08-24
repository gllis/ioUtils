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

    String TCP_HOST = "tcp.host";
    String TCP_LAST_SEND = "tcp.last.send";

    String UDP_HOST = "udp.host";
    String UDP_LAST_SEND = "udp.last.send";

    String MQTT_HOST = "mqtt.host";
    String MQTT_LAST_SEND = "mqtt.last.send";
    String MQTT_CLIENT_ID = "mqtt.clientId";
    String MQTT_TOPIC = "mqtt.topic";
}
