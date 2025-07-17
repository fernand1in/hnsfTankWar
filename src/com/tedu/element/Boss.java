package com.tedu.element;

import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

public class Boss extends Enemy {
    private int health = 5; // Boss 的生命值，比普通敌人高
    
    public Boss() {
        this.speed = 3; // 将Boss的移动速度设为2，比普通敌人的1更快
    }
    @Override
    public void showElement(Graphics g) {
        // 绘制一个带有颜色和边框的圆角矩形作为 Boss
        Graphics2D g2d = (Graphics2D) g;

        // 设置 Boss 的颜色为深红色
        g2d.setColor(new Color(150, 0, 0));
        // 绘制圆角矩形
        RoundRectangle2D.Float bossShape = new RoundRectangle2D.Float(
                this.getX(), this.getY(), this.getW(), this.getH(), 10, 10);
        g2d.fill(bossShape);

        // 绘制边框
        g2d.setColor(Color.BLACK);
        g2d.draw(bossShape);

        // 绘制内部细节（例如眼睛）
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(this.getX() + 10, this.getY() + 10, 10, 10); // 左眼
        g2d.fillOval(this.getX() + this.getW() - 20, this.getY() + 10, 10, 10); // 右眼

        // 绘制嘴巴
        g2d.setColor(Color.WHITE);
        g2d.fillRect(this.getX() + 15, this.getY() + this.getH() - 15, this.getW() - 30, 5);
    }

    @Override
    public ElementObj createElement(String str) {
        String[] split = str.split(",");
        this.setX(Integer.parseInt(split[0]));
        this.setY(Integer.parseInt(split[1]));
        // 获取玩家的宽度和高度
        List<ElementObj> plays = ElementManager.getManager().getElementsByKey(GameElement.PLAY);
        if (!plays.isEmpty()) {
            Play player = (Play) plays.get(0);
            this.setW(player.getW()); // 设置宽度与玩家一致
            this.setH(player.getH()); // 设置高度与玩家一致
        } else {
            this.setW(50); // 默认宽度
            this.setH(50); // 默认高度
        }
        this.fx = split[2];
        this.frameCount = 0; // 初始化帧计数器

        // 检查生成位置是否与地图元素碰撞
        List<ElementObj> maps = ElementManager.getManager().getElementsByKey(GameElement.MAPS);
        boolean collisionDetected;
        int safeMargin = 50; // 安全边界
        do {
            collisionDetected = false;
            for (ElementObj map : maps) {
                if (this.getRectangle().intersects(map.getRectangle())) {
                    collisionDetected = true;
                    break;
                }
            }
            if (collisionDetected) {
                Random ran = new Random();
                this.setX(safeMargin + ran.nextInt(GameJFrame.GameX - this.getW() - 2 * safeMargin));
                this.setY(safeMargin + ran.nextInt(GameJFrame.GameY - this.getH() - 2 * safeMargin));
            }
        } while (collisionDetected);

        return this;
    }

    @Override
    public void die() {
        // Boss 死亡时增加更多得分
        ElementManager.getManager().addScore(500);
        this.setLive(false); // Boss 死亡
    }

    @Override
    public void add(long gameTime) {
        // 每帧调用一次，增加帧计数器
        frameCount++;
        
        // 当达到发射间隔且未达到最大发射次数时发射子弹
        if (frameCount >= fireInterval && shotCount < totalShots) {
            System.out.println("Boss fires at frame: " + frameCount);
            ElementObj bullet = GameLoad.getObj("bossBullet");
            if (bullet != null) {
                ElementObj element = bullet.createElement(this.toString());
                ElementManager.getManager().addElement(element, GameElement.ENEMYFILE);
                System.out.println("Boss发射子弹: " + element);
                frameCount = 0; // 重置帧计数器
                shotCount++;    // 增加发射次数计数
            }
        }
    }
    @Override
    public void handleCollision(ElementObj obj) {
        if (obj instanceof PlayFile) {
            // 玩家子弹击中 Boss
            this.health -= 1; // 减少生命值
            obj.setLive(false); // 子弹消失
            if (this.health <= 0) {
                this.die(); // 调用死亡方法
            }
        }
    }
}