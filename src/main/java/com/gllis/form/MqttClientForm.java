package com.gllis.form;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.gllis.conf.AppConstant;
import com.gllis.conf.UiConstant;
import com.gllis.net.Client;
import com.gllis.net.ClientDispatcher;
import com.gllis.net.MQTTClient;
import com.gllis.util.AppConfUtils;
import com.gllis.util.DateUtil;
import io.netty.util.internal.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import java.awt.*;
import java.io.IOException;
import java.text.MessageFormat;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * mqtt 客户端
 *
 * @author glli
 * @date 2023/8/24
 */
public class MqttClientForm extends JPanel implements ClientDispatcher {

    /**
     * 接收区
     */
    private JTextPane taReceive;

    /**
     * 连接按钮
     */
    private JButton btnConnect;
    /**
     * ip选择器
     */
    private JComboBox<String> cIp;

    private JButton btnSend;
    private JTextField tPort;
    private JTextField tClientId;
    private JTextArea taSend;
    private JTextField tTopic;

    /**
     * 客户端
     */
    private final Client client;

    public MqttClientForm() {
        this.client = new MQTTClient();
        this.client.setListener(this);
        try {
            init();
            initActionEvent();
            initClientLastRecord();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        this.setLayout(null);
        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lIp = new JLabel("远程主机：");
        cIp = new JComboBox<>();
        cIp.setPreferredSize(new Dimension(160, UiConstant.COMPONENT_HEIGHT));
        cIp.setEditable(true);

        JLabel lPort = new JLabel(" 端口：");

        tPort = new JTextField();
        tPort.setColumns(4);

        JLabel lClientId = new JLabel(" ClientId：");
        tClientId = new JTextField();
        tClientId.setColumns(12);

        btnConnect = new JButton("连接");
        btnConnect.setPreferredSize(new Dimension(80, UiConstant.COMPONENT_HEIGHT));
        toolPanel.add(lIp);
        toolPanel.add(cIp);
        toolPanel.add(lPort);
        toolPanel.add(tPort);
        toolPanel.add(lClientId);
        toolPanel.add(tClientId);
        toolPanel.add(btnConnect);

        toolPanel.setBounds(10, 10, 780, 40);
        this.add(toolPanel);

        taReceive = new JTextPane();
        taReceive.setFont(new Font(null, 0, 16));
        taReceive.setEditable(false);

        TabStop[] tabs = new TabStop[4];
        tabs[0] = new TabStop(20, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE);
        tabs[1] = new TabStop(60, TabStop.ALIGN_CENTER, TabStop.LEAD_NONE);
        tabs[2] = new TabStop(20, TabStop.ALIGN_RIGHT, TabStop.LEAD_NONE);
        tabs[3] = new TabStop(60, TabStop.ALIGN_DECIMAL, TabStop.LEAD_NONE);
        TabSet tabset = new TabSet(tabs);

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.TabSet, tabset);
        taReceive.setParagraphAttributes(aset, false);

        JScrollPane receivePanel = new JScrollPane(taReceive);
        receivePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        receivePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        receivePanel.setBounds(10, 55, 680, 240);
        this.add(receivePanel);

        JButton btnClear = new JButton("清空");
        btnClear.setBounds(700, 265, 80, UiConstant.COMPONENT_HEIGHT);
        this.add(btnClear);
        btnClear.addActionListener(l -> taReceive.setText(null));


        JPanel topicPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lQos = new JLabel("Qos：");
        JComboBox cQos = new JComboBox(new Integer[]{1, 2, 3});
        JLabel lTopic = new JLabel(" Topic：");
        tTopic = new JTextField();
        tTopic.setPreferredSize(new Dimension(497, UiConstant.COMPONENT_HEIGHT));

        topicPanel.add(lQos);
        topicPanel.add(cQos);
        topicPanel.add(lTopic);
        topicPanel.add(tTopic);
        topicPanel.setBounds(10, 298, 680, 40);
        this.add(topicPanel);

        taSend = new JTextArea();
        taSend.setFont(new Font(null, 0, 16));
        taSend.setLineWrap(true);
        taSend.setWrapStyleWord(true);
        taSend.setTabSize(2);
        JScrollPane sendPane = new JScrollPane(taSend);
        sendPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sendPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sendPane.setBounds(10, 340, 680, 120);
        this.add(sendPane);

        btnSend = new JButton("发送");
        btnSend.setBounds(700, 430, 80, UiConstant.COMPONENT_HEIGHT);
        this.add(btnSend);

    }

    private void initActionEvent() {
        cIp.addActionListener(l -> tPort.setText(AppConfUtils.getPort(AppConstant.MQTT_HOST, cIp.getSelectedIndex())));

        btnConnect.addActionListener(e -> {
            if ("断开".equals(btnConnect.getText())) {
                client.disConnect();
                return;
            }
            String ip = (String) cIp.getSelectedItem();
            if (StringUtil.isNullOrEmpty(ip) || StringUtil.isNullOrEmpty(tPort.getText())) {
                showMessageDialog(null, "请输入Ip或端口");
                return;
            }
            client.connect(ip.trim(), Integer.parseInt(tPort.getText()), tClientId.getText());
        });

        btnSend.addActionListener(e -> {
            String ip = (String) cIp.getSelectedItem();
            if (StringUtil.isNullOrEmpty(ip)
                    || StringUtil.isNullOrEmpty(tPort.getText())) {
                showMessageDialog(null, "请输入Ip或端口");
                return;
            }
            client.sendMsg(tTopic.getText(), taSend.getText());
        });


    }

    /**
     * 初始化上次发送记录
     */
    private void initClientLastRecord() {
        try {
            cIp.setModel(new DefaultComboBoxModel<>(AppConfUtils.getHosts(AppConstant.MQTT_HOST)));
            tPort.setText(AppConfUtils.getPort(AppConstant.MQTT_HOST, cIp.getSelectedIndex()));
            taSend.setText(AppConfUtils.get().getProperty(AppConstant.MQTT_LAST_SEND));
            tClientId.setText(AppConfUtils.get().getProperty(AppConstant.MQTT_CLIENT_ID));
            tTopic.setText(AppConfUtils.get().getProperty(AppConstant.MQTT_TOPIC));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receive(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrSet, 16);
        if (topic != null && topic.equals(tTopic.getText())) {
            StyleConstants.setBold(attrSet, true);
            StyleConstants.setForeground(attrSet, Color.BLUE);//设置颜色
        } else {
            StyleConstants.setForeground(attrSet, Color.WHITE);//设置颜色
        }
        Document doc = taReceive.getDocument();
        try {
            JSONObject object = JSONObject.parseObject(new String(data));
            doc.insertString(doc.getLength(), MessageFormat.format("[{0}] {1}\n", DateUtil.now(), topic), attrSet);
            doc.insertString(doc.getLength(), JSON.toJSONString(object,
                    JSONWriter.Feature.PrettyFormat) + "\n\n", attrSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connected() {
        btnConnect.setText("断开");
    }

    @Override
    public void disConnect() {
        btnConnect.setText("连接");
    }

    @Override
    public void alertMsg(String msg) {
        showMessageDialog(null, msg);
    }

    @Override
    public void updateIpArray(String[] ipArray) {
        if (ipArray.length == cIp.getItemCount()) {
            return;
        }
        cIp.setModel(new DefaultComboBoxModel<>(ipArray));
    }
}
