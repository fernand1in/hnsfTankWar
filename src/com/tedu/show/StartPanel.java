package com.tedu.show;

import com.tedu.controller.GameThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class StartPanel extends JPanel{
    private GameJFrame parentFrame;
    private Image backgroundImage;

    public StartPanel(GameJFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.setLayout(null);

        JLabel titleLabel = new JLabel("游 戏 主 菜 单");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titleLabel.setBounds(90+(100+50)*1+30, 300-75, 300, 50);
        this.add(titleLabel);

        // 加载游戏图标
        try {
            backgroundImage = ImageIO.read(new File("image/icon.png"));
            System.out.println("加载背景图片");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 创建并设置开始游戏按钮
        JButton startButton = new JButton("开始游戏");
        startButton.setBounds(320, 300, 150, 50);
        startButton.setFocusPainted(false);  // 移除焦点框
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // 设置鼠标悬浮手形
        startButton.setFont(new Font("Serif", Font.BOLD, 20));  // 设置字体

        System.out.println("创建按钮");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showLevelPanel(parentFrame); // 切换到关卡选择面板
            }
        });
        this.add(startButton);

        // 创建并设置继续游戏按钮
        JButton continueButton = new JButton("继续游戏");
        continueButton.setBounds(320, 370, 150, 50);
        continueButton.setFocusPainted(false);  // 移除焦点框
        continueButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // 设置鼠标悬浮手形
        continueButton.setFont(new Font("Serif", Font.BOLD, 20));  // 设置字体

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.startGame(GameThread.getLevel()); // 暂时设为启动关卡1
            }
        });
        this.add(continueButton);

        // 创建并设置退出游戏按钮
        JButton exitButton = new JButton("退出游戏");
        exitButton.setBounds(320, 440, 150, 50);
        exitButton.setFocusPainted(false);  // 移除焦点框
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // 设置鼠标悬浮手形
        exitButton.setFont(new Font("Serif", Font.BOLD, 20));  // 设置字体

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // 关闭整个窗口
            }
        });
        this.add(exitButton);

        System.out.println("按钮创建完了");
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

