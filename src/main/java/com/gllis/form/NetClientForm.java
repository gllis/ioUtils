package com.gllis.form;

import com.gllis.conf.AppConstant;
import com.gllis.conf.UiConstant;
import com.gllis.net.Client;
import com.gllis.net.ClientDispatcher;
import com.gllis.net.TcpClient;
import com.gllis.net.UdpClient;
import com.gllis.util.AppConfUtils;
import com.gllis.util.DateUtil;
import com.gllis.util.HexUtil;
import io.netty.util.internal.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.text.MessageFormat;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * 网络工具面板 （TCP&UDP）
 *
 * @author gllis
 * @date 2023/7/27
 */
public class NetClientForm extends JPanel implements ClientDispatcher {


    /**
     * 接收区
     */
    private JTextArea taReceive;

    /**
     * 连接按钮
     */
    private JButton btnConnect;
    /**
     * 是否以16进制接收
     */
    private boolean isHexReceive = true;

    /**
     * 客户端
     */
    private Client client;

    public NetClientForm(Client client) {
        super();
        this.client = client;
        this.client.setListener(this);
        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化UI
     */
    private void init() throws IOException {

        this.setLayout(null);
        this.setBorder(new EmptyBorder(5,5,5,5));
        JLabel lIp = new JLabel("远程主机：");
        lIp.setBounds(10, 10, 80, UiConstant.COMPONENT_HEIGHT);
        this.add(lIp);
        JComboBox<String> cIp = new JComboBox<>();
        cIp.setEditable(true);
        cIp.setBounds(80, 10, 200, UiConstant.COMPONENT_HEIGHT);
        this.add(cIp);

        JLabel lPort = new JLabel("端口：");
        lPort.setBounds(290, 10, 90, UiConstant.COMPONENT_HEIGHT);
        this.add(lPort);
        JTextField tPort = new JTextField();
        tPort.setBounds(330, 10, 80, UiConstant.COMPONENT_HEIGHT);
        this.add(tPort);
        tPort.setColumns(10);

        btnConnect = new JButton("连接");
        btnConnect.setBounds(420, 10, 80, UiConstant.COMPONENT_HEIGHT);
        this.add(btnConnect);
        btnConnect.addActionListener( e -> {
            if ("断开".equals(btnConnect.getText())) {
                client.disConnect();
                return;
            }
            String ip = (String) cIp.getSelectedItem();
            if (StringUtil.isNullOrEmpty(ip) || StringUtil.isNullOrEmpty(tPort.getText())) {
                showMessageDialog(null, "请输入Ip或端口");
                return;
            }
            client.connect(ip.trim(), Integer.parseInt(tPort.getText()));
        });
        if (client instanceof UdpClient) {
            btnConnect.setVisible(false);
        }

        JCheckBox hexReceive = new JCheckBox();
        hexReceive.setSelected(true);
        hexReceive.setBounds(10, 40, 30, UiConstant.COMPONENT_HEIGHT);
        hexReceive.addChangeListener( l -> this.isHexReceive = hexReceive.isSelected());
        this.add(hexReceive);
        JLabel labHexReceive = new JLabel("16进制");
        labHexReceive.setBounds(30, 40, 60, UiConstant.COMPONENT_HEIGHT);
        this.add(labHexReceive);

        taReceive = new JTextArea();
        taReceive.setFont(new Font(null, 0, 22));
        taReceive.setLineWrap(true);
        taReceive.setWrapStyleWord(true);
        taReceive.setEditable(false);

        JScrollPane receivePanel = new JScrollPane(taReceive);
        receivePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        receivePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        receivePanel.setBounds(10, 75, 680, 220);
        this.add(receivePanel);

        JButton btnClear = new JButton("清空");
        btnClear.setBounds(700, 265, 80, UiConstant.COMPONENT_HEIGHT);
        this.add(btnClear);
        btnClear.addActionListener( l -> taReceive.setText(null));

        JCheckBox hexSend = new JCheckBox();
        hexSend.setSelected(true);
        hexSend.addChangeListener( l -> client.setIsHexSend(hexSend.isSelected()));
        hexSend.setBounds(10, 300, 30, UiConstant.COMPONENT_HEIGHT);
        this.add(hexSend);
        JLabel labHexSend = new JLabel("16进制");
        labHexSend.setBounds(30, 300, 60, UiConstant.COMPONENT_HEIGHT);
        this.add(labHexSend);

        JTextArea taSend = new JTextArea();
        taSend.setFont(new Font(null, 0, 22));
        taSend.setLineWrap(true);
        taSend.setWrapStyleWord(true);
        JScrollPane sendPane = new JScrollPane(taSend);
        sendPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sendPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sendPane.setBounds(10, 340, 680, 120);
        this.add(sendPane);

        JButton btnSend = new JButton("发送");
        btnSend.setBounds(700, 430, 80, UiConstant.COMPONENT_HEIGHT);
        this.add(btnSend);
        btnSend.addActionListener(e -> {
            if (client instanceof UdpClient) {
                String ip = (String) cIp.getSelectedItem();
                if (StringUtil.isNullOrEmpty(ip)
                        || StringUtil.isNullOrEmpty(tPort.getText())) {
                    showMessageDialog(null, "请输入Ip或端口");
                    return;
                }
                client.connect(ip.trim(), Integer.parseInt(tPort.getText()));
            }
            client.sendMsg(taSend.getText());
        });

        initClientLastRecord(cIp, tPort, taSend);
    }

    /**
     * 初始化上次发送记录
     *
     * @param cIp
     * @param tPort
     * @param taSend
     */
    private void initClientLastRecord(JComboBox<String> cIp, JTextField tPort, JTextArea taSend) {
        try {
            if (client instanceof TcpClient) {
                cIp.setModel(new DefaultComboBoxModel<>(AppConfUtils.getHosts(AppConstant.TCP_HOST)));
                tPort.setText(AppConfUtils.getPort(AppConstant.TCP_HOST));
                taSend.setText(AppConfUtils.get().getProperty(AppConstant.TCP_LAST_SEND));
            } else {
                cIp.setModel(new DefaultComboBoxModel<>(AppConfUtils.getHosts(AppConstant.UDP_HOST)));
                tPort.setText(AppConfUtils.getPort(AppConstant.UDP_HOST));
                taSend.setText(AppConfUtils.get().getProperty(AppConstant.UDP_LAST_SEND));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receive(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        String result = isHexReceive ? HexUtil.convertByteToHex(data) : new String(data);
        taReceive.append(MessageFormat.format("{0} [{1}]", client.getHostInfo(), DateUtil.now()));
        taReceive.append("\n");
        taReceive.append(result);
        taReceive.append("\n");
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
}
