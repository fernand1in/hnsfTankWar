package com.tedu.element;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;

public class BossBullet extends EnemyFile {
    private int moveNum = 7; // Boss 子弹的速度
    private String fx;

    @Override
    public ElementObj createElement(String str) {
        System.out.println("创建 Boss 子弹，参数: " + str);
        String[] split = str.split(",");
        for (String str1 : split) {
            String[] split2 = str1.split(":");
            switch (split2[0]) {
                case "x": this.setX(Integer.parseInt(split2[1])); break;
                case "y": this.setY(Integer.parseInt(split2[1])); break;
                case "f": this.fx = split2[1]; break;
            }
        }
        this.setW(15); // Boss 子弹的宽度
        this.setH(15); // Boss 子弹的高度
        return this;
    }

    @Override
    public void showElement(Graphics g) {
        if (this.isLive()) { // 只绘制存活的子弹
            g.setColor(Color.RED);
            int bulletSize = 15;
            int offsetX = (this.getW() - bulletSize) / 2;
            int offsetY = (this.getH() - bulletSize) / 2;
            g.fillOval(this.getX() + offsetX, this.getY() + offsetY, bulletSize, bulletSize);
            g.setColor(Color.BLACK);
            g.drawOval(this.getX() + offsetX, this.getY() + offsetY, bulletSize, bulletSize);
        }
    }

    @Override
    protected void move() {
        if (!this.isLive()) {
            return;
        }

        int newX = this.getX();
        int newY = this.getY();
        switch (fx) {
            case "up": newY -= moveNum; break;
            case "down": newY += moveNum; break;
            case "left": newX -= moveNum; break;
            case "right": newX += moveNum; break;
        }

        int gameWidth = 900; // 游戏窗口宽度
        int gameHeight = 600; // 游戏窗口高度
        if (newX < 0 || newX > gameWidth || newY < 0 || newY > gameHeight) {
            this.setLive(false);
            return;
        }

        List<ElementObj> maps = ElementManager.getManager().getElementsByKey(GameElement.MAPS);
        Rectangle bulletRect = new Rectangle(newX, newY, this.getW(), this.getH());
        for (ElementObj map : maps) {
            if (map.isLive() && bulletRect.intersects(map.getRectangle())) {
                this.setLive(false);
                return;
            }
        }

        this.setX(newX);
        this.setY(newY);
    }
}