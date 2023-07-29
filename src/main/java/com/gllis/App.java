package com.gllis;

import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.gllis.conf.UiConstant;
import com.gllis.dialog.AboutDialog;
import com.gllis.frame.MainFrame;
import com.gllis.util.SystemUtil;

import javax.swing.*;

/**
 * 程序启动入口
 *
 * @author gllis
 * @date 2023/7/29
 */
public class App {

    public static MainFrame mainFrame;
    public static void main(String[] args) {

        if (SystemUtil.isMacOs()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", UiConstant.APP_NAME);
            System.setProperty("apple.awt.application.appearance", "system");
        }
        mainFrame = new MainFrame();
        FlatDesktop.setAboutHandler(() -> {
            AboutDialog dialog = new AboutDialog();
            dialog.setVisible(true);
        });

        FlatDesktop.setPreferencesHandler(() -> {

        });
        FlatMacDarkLaf.setup();
        mainFrame.init();
        mainFrame.setBounds(300, 200, 800, 540);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
