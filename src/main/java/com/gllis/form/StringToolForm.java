package com.gllis.form;

import com.gllis.util.DateUtil;
import com.gllis.util.HexUtil;
import io.netty.util.internal.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具
 *
 * @author glli
 * @date 2023/8/25
 */
public class StringToolForm extends JPanel {

    /**
     * 原始数据
     */
    private JTextArea oriHexArea;

    /**
     * 转换后的数据
     */
    private JTextArea newHexArea;

    private JTextField tfTimestamp;
    private JTextField tfDate;

    private JButton btnNow;
    private JButton btnConvertTime;
    private JButton btnConvertDate;

    private JButton btnUcToZh;
    private JButton btnZhToUc;
    private JButton btnStrToBase64;
    private JButton btnBase64ToStr;
    private JButton btnToMd5;
    private JButton btnCopy;
    private JButton btnClean;


    public StringToolForm() {
        init();
        initActionEvent();
    }

    private void init() {
        this.setLayout(null);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lts = new JLabel("时间戳");
        tfTimestamp = new JTextField();
        tfTimestamp.setColumns(12);
        btnNow = new JButton("当前时间");
        btnConvertTime = new JButton("<< 转换时间戳");
        btnConvertDate = new JButton("转换日期 >>");
        JLabel lDate = new JLabel("  北京时间");
        tfDate = new JTextField();
        tfDate.setColumns(12);

        timePanel.add(lts);
        timePanel.add(tfTimestamp);
        timePanel.add(btnNow);
        timePanel.add(btnConvertTime);
        timePanel.add(btnConvertDate);
        timePanel.add(lDate);
        timePanel.add(tfDate);


        timePanel.setBounds(10, 5, 780, 40);
        this.add(timePanel);

        oriHexArea = new JTextArea();
        oriHexArea.setFont(new Font(null, 0, 16));
        oriHexArea.setLineWrap(true);
        oriHexArea.setWrapStyleWord(true);

        JScrollPane oriHexScrollPane = new JScrollPane(oriHexArea);
        oriHexScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        oriHexScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        oriHexScrollPane.setBounds(10, 45, 780, 185);
        this.add(oriHexScrollPane);


        newHexArea = new JTextArea();
        newHexArea.setFont(new Font(null, 0, 16));
        newHexArea.setLineWrap(true);
        newHexArea.setWrapStyleWord(true);
        newHexArea.setEditable(false);

        JScrollPane newHexScrollPane = new JScrollPane(newHexArea);
        newHexScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        newHexScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        newHexScrollPane.setBounds(10, 270, 780, 200);
        this.add(newHexScrollPane);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnUcToZh = new JButton("Unicode解码");
        btnZhToUc = new JButton("Unicode编码");
        btnBase64ToStr = new JButton("Base64解码");
        btnStrToBase64 = new JButton("Base64编码");
        btnToMd5 = new JButton("MD5");
        btnClean = new JButton("清空结果");
        btnCopy = new JButton("复制结果");
        btnPanel.add(btnUcToZh);
        btnPanel.add(btnZhToUc);
        btnPanel.add(btnBase64ToStr);
        btnPanel.add(btnStrToBase64);
        btnPanel.add(btnToMd5);
        btnPanel.add(btnClean);
        btnPanel.add(btnCopy);
        btnPanel.setBounds(10, 230, 780, 40);

        this.add(btnPanel);
    }

    private void initActionEvent() {
        btnNow.addActionListener( l -> tfTimestamp.setText(String.valueOf(System.currentTimeMillis())));
        btnConvertDate.addActionListener(l -> tfDate.setText(DateUtil.format(tfTimestamp.getText())));
        btnConvertTime.addActionListener(l -> tfTimestamp.setText(DateUtil.parse(tfDate.getText())));
        btnUcToZh.addActionListener(l -> {
            if (oriHexArea.getText() == null) {
                return;
            }
            String result = oriHexArea.getText();
            Pattern pattern = Pattern.compile("(\\\\u(\\w{4}))");
            Matcher matcher = pattern.matcher(result);
            char ch;
            while (matcher.find()) {
                ch = (char) Integer.parseInt(matcher.group(2), 16);
                result = result.replace(matcher.group(1), ch + "");
            }
            newHexArea.setText(result);
        });
        btnZhToUc.addActionListener(l -> {
            if (oriHexArea.getText() == null) {
                return;
            }
            char[] bytes = oriHexArea.getText().toCharArray();
            StringBuilder unicodeBytes = new StringBuilder();
            for (char utfByte : bytes) {
                String hexB = Integer.toHexString(utfByte);
                if (hexB.length() <= 2) {
                    hexB = "00" + hexB;
                }
                unicodeBytes.append("\\u").append(hexB);
            }
            newHexArea.setText(unicodeBytes.toString());
        });
        btnBase64ToStr.addActionListener(l -> {
            if (oriHexArea.getText() == null) {
                return;
            }
            newHexArea.setText(new String(Base64.getDecoder().decode(oriHexArea.getText())));
        });
        btnStrToBase64.addActionListener(l -> {
            if (oriHexArea.getText() == null) {
                return;
            }
            newHexArea.setText(new String(Base64.getEncoder().encode(oriHexArea.getText().getBytes())));
        });
        btnToMd5.addActionListener(l -> {
            if (oriHexArea.getText() == null) {
                return;
            }
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] digest = md5.digest(oriHexArea.getText().getBytes(StandardCharsets.UTF_8));
                newHexArea.setText(HexUtil.convertByteToHex(digest));
            } catch (NoSuchAlgorithmException e) {
              e.printStackTrace();
            }
        });
        btnClean.addActionListener(l -> newHexArea.setText(null));
        btnCopy.addActionListener(l -> {
            if (StringUtil.isNullOrEmpty(newHexArea.getText())) {
                return;
            }
            StringSelection stringSelection = new StringSelection(newHexArea.getText().trim());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
    }
}
