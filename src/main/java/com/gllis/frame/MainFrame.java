package com.gllis.frame;

import com.gllis.conf.UiConstant;
import com.gllis.dialog.AboutDialog;
import com.gllis.form.NetClientForm;
import com.gllis.net.TcpClient;
import com.gllis.net.UdpClient;
import com.gllis.util.SystemUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 主界面
 *
 * @author gllis
 * @date 2023/7/27
 */
public class MainFrame extends JFrame {


    private JTabbedPane tabbedPane;

    public void init() {
        this.setName(UiConstant.APP_NAME);
        this.setTitle(UiConstant.APP_NAME);

        if (SystemUtil.isMacOs()) {
            this.getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
            this.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
            this.getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
        }

        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        NetClientForm tcpClientForm = new NetClientForm(new TcpClient().create());
        NetClientForm udpClientForm = new NetClientForm(new UdpClient());
        this.tabbedPane.add("TCP客户端", tcpClientForm);
        this.tabbedPane.add("UDP客户端", udpClientForm);

        JPanel contentPanel = new JPanel(new BorderLayout());
        if (SystemUtil.isMacOs()) {
            contentPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        }
        contentPanel.add(this.tabbedPane);
        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
    }
}
