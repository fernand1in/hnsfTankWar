package com.tedu.element;

import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

public class Enemy extends ElementObj {
    protected int speed = 1;
    private ElementManager em = ElementManager.getManager();
    protected String fx = "up";
    protected int fireInterval = 180; // 每隔180帧发射一次子弹（假设60FPS，即3秒）
    protected int frameCount = 0;     // 游戏帧数计数器
    protected int totalShots = 100;   // 总共发射100次
    protected int shotCount = 0;      // 已经发射的子弹次数

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(), this.getX(), this.getY(), this.getW(), this.getH(), null);
    }

    @Override
    public ElementObj createElement(String str) {
        String[] split = str.split(",");
        this.setX(Integer.parseInt(split[0]));
        this.setY(Integer.parseInt(split[1]));
        this.setW(35);
        this.setH(35);
        this.setIcon(new ImageIcon("image/tank/bot/bot_" + split[2] + ".png"));

        List<ElementObj> maps = em.getElementsByKey(GameElement.MAPS);
        boolean collisionDetected;
        int safeMargin = 50;
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

        this.fx = split[2];
        this.setIcon(new ImageIcon("image/tank/bot/bot_" + this.fx + ".png"));
        this.frameCount = 0; // 初始化帧计数器
        return this;
    }

    @Override
    protected void move() {
        List<ElementObj> maps = em.getElementsByKey(GameElement.MAPS);
        boolean canMove = true;
        int newX = this.getX();
        int newY = this.getY();
        
        switch (this.fx) {
            case "up": newY -= this.speed; break;
            case "down": newY += this.speed; break;
            case "left": newX -= this.speed; break;
            case "right": newX += this.speed; break;
        }

        Rectangle futureRect = new Rectangle(newX, newY, this.getW(), this.getH());
        int mapRightBound = 760 - this.getW();
        int mapBottomBound = 550 - this.getH();
        
        if (newX <= 0 || newY <= 0 || newX >= mapRightBound || newY >= mapBottomBound) {
            canMove = false;
        }

        for (ElementObj map : maps) {
            if (futureRect.intersects(map.getRectangle())) {
                canMove = false;
                break;
            }
        }

        if (canMove) {
            this.setX(newX);
            this.setY(newY);
        } else {
            Random random = new Random();
            String[] directions = {"up", "down", "left", "right"};
            this.fx = directions[random.nextInt(4)];
            this.setIcon(new ImageIcon("image/tank/bot/bot_" + this.fx + ".png"));
        }
    }

    @Override
    public void add(long gameTime) { // 保持与父类相同的参数
        // 每帧调用一次，增加帧计数器
        frameCount++;
        
        // 当达到发射间隔且未达到最大发射次数时发射子弹
        if (frameCount >= fireInterval && shotCount < totalShots) {
            System.out.println("Enemy fires at frame: " + frameCount);
            ElementObj bullet = GameLoad.getObj("enemyFile");
            if (bullet != null) {
                ElementObj element = bullet.createElement(this.toString());
                em.addElement(element, GameElement.ENEMYFILE);
                System.out.println("敌人发射子弹: " + element);
                frameCount = 0; // 重置帧计数器
                shotCount++;    // 增加发射次数计数
                System.out.println("Current shotCount: " + shotCount);
            } else {
                System.out.println("敌人子弹对象为空");
            }
        }
    }
    @Override
    public String toString() {
        int x = this.getX();
        int y = this.getY();
        switch(this.fx) {
            case "up": x += this.getW()/2 - 5; break;
            case "left": y += this.getH()/2 - 5; break;
            case "right": x += this.getW(); y += this.getH()/2 - 5; break;
            case "down": x += this.getW()/2 - 5; y += this.getH(); break;
        }
        return "x:"+x+",y:"+y+",f:"+this.fx;
    }
    
    @Override
    public void die() {
        // 敌人死亡时增加得分
        ElementManager.getManager().addScore(100);
    }
    
}