package com.tedu.element;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;

public class Prop extends ElementObj {
    private long startTime; // 道具生效的开始时间
    private int duration = 10000; // 道具效果持续时间（10秒）

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(), this.getX(), this.getY(), this.getW(), this.getH(), null);
    }

    @Override
    public ElementObj createElement(String str) {
        String[] split = str.split(",");
        this.setX(Integer.parseInt(split[0]));
        this.setY(Integer.parseInt(split[1]));
        this.setW(30);
        this.setH(30);
        this.setIcon(new ImageIcon("image/tool/03.png"));
        return this;
    }

    @Override
    public void handleCollision(ElementObj obj) {
        // 道具只与玩家发生碰撞
        if (obj instanceof Play) {
            Play play = (Play) obj;
            play.increaseSpeed(); // 调用玩家的加速方法
            this.setLive(false); // 道具消失
        }
    }
}