package com.tedu.show;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LevelPanel extends JPanel {
    private GameJFrame parentFrame;
    private Image backgroundImage;

    // 定义常量
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_DX = 50;
    private static final int BUTTON_DY = 20;
    private static final int START_X = 120;
    private static final int START_Y = 300;
    private static final int LEVEL_COUNT = 10;

    public LevelPanel(GameJFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.setLayout(null);

        // 加载背景图片
        try {
            backgroundImage = ImageIO.read(new File("image/icon.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "无法加载背景图片", "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // 创建标题标签
        JLabel titleLabel = new JLabel("选 择 关 卡");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titleLabel.setBounds(START_X + (BUTTON_WIDTH + BUTTON_DX) * 1 + 30, START_Y - 75, 200, 50);
        this.add(titleLabel);

        // 创建关卡按钮
        for (int i = 1; i <= LEVEL_COUNT; i++) {
            int posX = START_X + (BUTTON_WIDTH + BUTTON_DX) * ((i - 1) % 4 + (i <= 8 ? 0 : 1));
            int posY = START_Y + (BUTTON_HEIGHT + BUTTON_DY) * ((i - 1) / 4);
            JButton levelButton = new JButton("关卡" + i);
            levelButton.setBounds(posX, posY, BUTTON_WIDTH, BUTTON_HEIGHT);
            levelButton.setFocusPainted(false);  // 移除焦点框
            levelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // 设置鼠标悬浮手形
            levelButton.setFont(new Font("Serif", Font.BOLD, 20));
            int level = i;
            levelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parentFrame.startGame(level); // 启动对应的关卡
                }
            });
            this.add(levelButton);
        }

        // 创建返回按钮
        JButton backButton = new JButton("返回");
        backButton.setBounds(START_X + (BUTTON_WIDTH + BUTTON_DX) * 1 + 30, START_Y + (BUTTON_HEIGHT + BUTTON_DY) * 3, 150, 50);
        backButton.setFont(new Font("Serif", Font.BOLD, 20));
        backButton.setFocusPainted(false);  // 移除焦点框
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // 设置鼠标悬浮手形
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showStartPanel(); // 返回到开始面板
            }
        });
        this.add(backButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 绘制背景图片，并适应窗口大小
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}