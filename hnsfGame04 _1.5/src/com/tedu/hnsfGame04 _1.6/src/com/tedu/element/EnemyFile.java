package com.tedu.element;

import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.show.GameJFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public class EnemyFile extends ElementObj {
    private int moveNum = 3; // 可适当提高子弹速度，避免卡顿
    private String fx;
    private ElementManager em = ElementManager.getManager(); // 获取元素管理器

    @Override
    public ElementObj createElement(String str) {
        System.out.println("创建敌人子弹，参数: " + str);
        String[] split = str.split(",");
        for (String str1 : split) {
            String[] split2 = str1.split(":");
            switch (split2[0]) {
                case "x": this.setX(Integer.parseInt(split2[1])); break;
                case "y": this.setY(Integer.parseInt(split2[1])); break;
                case "f": this.fx = split2[1]; break;
            }
        }
        this.setW(10);
        this.setH(10);
        return this;
    }

    @Override
    public void showElement(Graphics g) {
        if (this.isLive()) { // 只绘制存活的子弹
            g.setColor(Color.YELLOW);
            int bulletSize = 10;
            int offsetX = (this.getW() - bulletSize) / 2;
            int offsetY = (this.getH() - bulletSize) / 2;
            g.fillOval(this.getX() + offsetX, this.getY() + offsetY, bulletSize, bulletSize);
            g.setColor(Color.BLACK);
            g.drawOval(this.getX() + offsetX, this.getY() + offsetY, bulletSize, bulletSize);
        }
    }

    @Override
    protected void move() {
        // 1. 先判断子弹是否已死亡，若死亡则停止移动
        if (!this.isLive()) {
            return;
        }

        // 2. 计算子弹新位置
        int newX = this.getX();
        int newY = this.getY();
        switch (fx) {
            case "up": newY -= moveNum; break;
            case "down": newY += moveNum; break;
            case "left": newX -= moveNum; break;
            case "right": newX += moveNum; break;
        }

        // 3. 边界检测：子弹超出游戏窗口则死亡
        // 游戏窗口边界从GameJFrame获取，确保与敌人移动边界一致
        int gameWidth = GameJFrame.GameX;
        int gameHeight = GameJFrame.GameY;
        if (newX < 0 || newX > gameWidth || newY < 0 || newY > gameHeight) {
            this.setLive(false); // 碰到边界，子弹死亡
            return;
        }

        // 4. 与地图元素（ElementObj map）碰撞检测
        List<ElementObj> maps = em.getElementsByKey(GameElement.MAPS); // 获取所有地图元素
        Rectangle bulletRect = new Rectangle(newX, newY, this.getW(), this.getH()); // 子弹的碰撞矩形
        for (ElementObj map : maps) {
            if (map.isLive() && bulletRect.intersects(map.getRectangle())) {
                // 子弹与地图元素碰撞，子弹死亡（地图元素不死亡）
                this.setLive(false);
                return;
            }
        }

        // 5. 若未碰撞，更新子弹位置
        this.setX(newX);
        this.setY(newY);
    }
}