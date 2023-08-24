package com.gllis.net;

import com.gllis.conf.AppConstant;
import com.gllis.util.AppConfUtils;
import io.netty.util.internal.StringUtil;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


/**
 * mqtt客户端
 *
 * @author glli
 * @date 2023/8/24
 */
public class MQTTClient implements Client {

    private MqttClient client;
    private ClientDispatcher clientDispatcher;

    @Override
    public Client setListener(ClientDispatcher clientDispatcher) {
        this.clientDispatcher = clientDispatcher;
        return this;
    }

    @Override
    public void connect(String host, Integer port, String clientId) {
        if (StringUtil.isNullOrEmpty(clientId)) {
            clientDispatcher.alertMsg("请输入Client ID");
            return;
        }
        try {
            String broker = String.format("tcp://%s:%s", host, port);
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
            client = new MqttClient(broker, clientId.trim(), new MemoryPersistence());
            // Paho MQTT连接参数。
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(180);
            client.connect(connOpts);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    clientDispatcher.disConnect();
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    clientDispatcher.receive(topic, mqttMessage.getPayload());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            clientDispatcher.connected();
            AppConfUtils.updateHost(AppConstant.MQTT_HOST, host, port);
            AppConfUtils.update(AppConstant.MQTT_CLIENT_ID, clientId);
            clientDispatcher.updateIpArray(AppConfUtils.getHosts(AppConstant.MQTT_HOST));

        } catch (Exception e) {
            e.printStackTrace();
            clientDispatcher.alertMsg("请求连接服务器失败！");
        }

    }

    @Override
    public void disConnect() {
        try {
            client.disconnect();
            clientDispatcher.disConnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMsg(String topic, String content) {
        try {
            if (client == null || !client.isConnected()) {
                clientDispatcher.alertMsg("请先连接服务器！");
                return;
            }
            if (StringUtil.isNullOrEmpty(topic)) {
                clientDispatcher.alertMsg("topic不能为空！");
                return;
            }
            if (StringUtil.isNullOrEmpty(content)) {
                clientDispatcher.alertMsg("发送内容不能为空！");
                return;
            }
            byte[] sendMsg = content.trim().getBytes();
            client.publish(topic, new MqttMessage(sendMsg));
            AppConfUtils.update(AppConstant.MQTT_TOPIC, topic);
            AppConfUtils.update(AppConstant.MQTT_LAST_SEND, content.trim());
            clientDispatcher.receive(topic, sendMsg);
        } catch (MqttException e) {
            e.printStackTrace();
            clientDispatcher.alertMsg("发送失败！");
        }
    }

}
