package com.gllis.form;

import com.gllis.conf.UiConstant;
import com.gllis.net.Client;
import com.gllis.net.ClientDispatcher;
import com.gllis.net.UdpClient;
import com.gllis.util.HexUtil;
import io.netty.util.internal.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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

    private boolean isHexReceive;

    /**
     * 客户端
     */
    private Client client;

    public NetClientForm(Client client) {
        super();
        this.client = client;
        this.client.setListener(this);
        init();
    }

    /**
     * 初始化UI
     */
    private void init() {

        this.setLayout(null);
        this.setBorder(new EmptyBorder(5,5,5,5));
        JLabel lIp = new JLabel("远程主机：");
        lIp.setBounds(10, 10, 80, UiConstant.COMPONENT_HEIGHT);
        this.add(lIp);
        JTextField tIp = new JTextField();
        tIp.setBounds(80, 10, 200, UiConstant.COMPONENT_HEIGHT);
        this.add(tIp);
        tIp.setColumns(10);
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
            if (StringUtil.isNullOrEmpty(tIp.getText()) || StringUtil.isNullOrEmpty(tPort.getText())) {
                showMessageDialog(null, "请输入Ip或端口");
                return;
            }
            client.connect(tIp.getText().trim(), Integer.parseInt(tPort.getText()));
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
        taReceive.setBounds(10, 75, 600, 220);
        taReceive.setFont(new Font(null, 0, 22));
        this.add(taReceive);

        JCheckBox hexSend = new JCheckBox();
        hexSend.setSelected(true);
        hexSend.addChangeListener( l -> client.setIsHexSend(hexSend.isSelected()));
        hexSend.setBounds(10, 300, 30, UiConstant.COMPONENT_HEIGHT);
        this.add(hexSend);
        JLabel labHexSend = new JLabel("16进制");
        labHexSend.setBounds(30, 300, 60, UiConstant.COMPONENT_HEIGHT);
        this.add(labHexSend);

        JTextArea taSend = new JTextArea();
        taSend.setBounds(10, 340, 600, 120);
        taSend.setFont(new Font(null, 0, 22));
        this.add(taSend);

        JButton btnSend = new JButton("发送");
        btnSend.setBounds(620, 430, 80, UiConstant.COMPONENT_HEIGHT);
        this.add(btnSend);
        btnSend.addActionListener(e -> {
            if (client instanceof UdpClient) {
                if (StringUtil.isNullOrEmpty(tIp.getText())
                        || StringUtil.isNullOrEmpty(tPort.getText())) {
                    showMessageDialog(null, "请输入Ip或端口");
                    return;
                }
                client.connect(tIp.getText(), Integer.parseInt(tPort.getText()));
            }
            client.sendMsg(taSend.getText());
        });
    }

    @Override
    public void receive(byte[] data) {
        String result = isHexReceive ? HexUtil.convertByteToHex(data) : new String(data);
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
