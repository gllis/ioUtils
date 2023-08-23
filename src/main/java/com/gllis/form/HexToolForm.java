package com.gllis.form;

import com.gllis.util.HexUtil;
import io.netty.util.internal.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * 16 进制转换工具
 *
 * @author GL
 * @date 2023/8/9
 */
public class HexToolForm extends JPanel {

    public HexToolForm() {
        // 初始化
        init();
        // 添加事件
        addAction();
    }

    /**
     * 原始数据
     */
    private JTextArea oriHexArea;

    /**
     * 转换后的数据
     */
    private JTextArea newHexArea;

    /**
     * 转换为字符串按钮
     */
    private JButton btnToStr;
    /**
     * 转换成16进制
     */
    private JButton btnToHex;
    /**
     * 复制
     */
    private JButton btnCopy;
    /**
     * 清空
     */
    private JButton btnClean;
    /**
     * 格式化
     */
    private JButton btnFormatter;

    /**
     * 全大写
     */
    private JButton btnToUpCase;

    /**
     * 全小写
     */
    private JButton btnToLowCase;

    /**
     * 初始化
     */
    private void init() {
        this.setLayout(null);

        oriHexArea = new JTextArea();
        oriHexArea.setFont(new Font(null, 0, 16));
        oriHexArea.setLineWrap(true);
        oriHexArea.setWrapStyleWord(true);

        JScrollPane oriHexScrollPane = new JScrollPane(oriHexArea);
        oriHexScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        oriHexScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        oriHexScrollPane.setBounds(10, 10, 780, 220);
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
        btnToStr = new JButton("16进制转字符串");
        btnToHex = new JButton("字符串转16进制");
        btnClean = new JButton("清空结果");
        btnCopy = new JButton("复制结果");
        btnFormatter = new JButton("格式化");
        btnToUpCase = new JButton("全大写");
        btnToLowCase = new JButton("全小写");
        btnPanel.add(btnToStr);
        btnPanel.add(btnToHex);
        btnPanel.add(btnClean);
        btnPanel.add(btnCopy);
        btnPanel.add(btnFormatter);
        btnPanel.add(btnToUpCase);
        btnPanel.add(btnToLowCase);
        btnPanel.setBounds(10, 230, 780, 40);

        this.add(btnPanel);

    }

    private void addAction() {
        btnToStr.addActionListener(l -> {
            String content = oriHexArea.getText();
            if (StringUtil.isNullOrEmpty(content)) {
                showMessageDialog(null, "内容不能为空！");
                return;
            }
            String ascii = new String(HexUtil.convertHexToByte(content.toUpperCase()));
            newHexArea.setText(ascii);
        });
        btnToHex.addActionListener(l -> {
            String content = oriHexArea.getText();
            if (StringUtil.isNullOrEmpty(content)) {
                showMessageDialog(null, "内容不能为空！");
                return;
            }
            String ascii = HexUtil.convertByteToHex(content.trim().getBytes());
            newHexArea.setText(ascii);
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
        btnFormatter.addActionListener(l -> {
            if (StringUtil.isNullOrEmpty(oriHexArea.getText())) {
                showMessageDialog(null, "内容不能为空！");
                return;
            }
            String content = oriHexArea.getText().trim();
            int i = 0;
            StringBuilder rs = new StringBuilder();
            for (char c : content.toCharArray()) {
                i++;
                rs.append(c);
                if (i % 2 == 0) {
                    rs.append(" ");
                }
            }
            newHexArea.setText(rs.toString());
        });

        btnToUpCase.addActionListener(l -> {
            if (StringUtil.isNullOrEmpty(oriHexArea.getText())) {
                showMessageDialog(null, "内容不能为空！");
                return;
            }
            newHexArea.setText(oriHexArea.getText().trim().toUpperCase());
        });

        btnToLowCase.addActionListener(l -> {
            if (StringUtil.isNullOrEmpty(oriHexArea.getText())) {
                showMessageDialog(null, "内容不能为空！");
                return;
            }
            newHexArea.setText(oriHexArea.getText().trim().toLowerCase());
        });
    }
}
