package com.tedu.element;

import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;

import javax.swing.*;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

public class Enemy extends ElementObj {
    private int speed = 1; // 敌方坦克移动速度
    private ElementManager em = ElementManager.getManager(); // 元素管理器

    private boolean left = false;  // 左
    private boolean up = false;    // 上
    private boolean right = false; // 右
    private boolean down = false;  // 下
    private String fx = "up"; // 方向

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

        // 检查初始位置是否与墙体或其他障碍物发生碰撞
        List<ElementObj> maps = em.getElementsByKey(GameElement.MAPS);
        boolean collisionDetected;
        do {
            collisionDetected = false;
            for (ElementObj map : maps) {
                if (this.getRectangle().intersects(map.getRectangle())) {
                    collisionDetected = true;
                    break;
                }
            }
            if (collisionDetected) {
                // 如果发生碰撞，重新生成位置
                Random ran = new Random();
                this.setX(ran.nextInt(780 - this.getW()));
                this.setY(ran.nextInt(555 - this.getH()));
            }
        } while (collisionDetected);

        // 随机设置初始方向
        Random random = new Random();
        int direction = random.nextInt(4);
        switch (direction) {
            case 0:
                this.up = true;
                this.fx = "up";
                break;
            case 1:
                this.down = true;
                this.fx = "down";
                break;
            case 2:
                this.left = true;
                this.fx = "left";
                break;
            case 3:
                this.right = true;
                this.fx = "right";
                break;
        }

        return this;
    }

    @Override
    protected void move() {
        // 检查移动方向是否与墙体或其他障碍物发生碰撞
        List<ElementObj> maps = em.getElementsByKey(GameElement.MAPS);
        boolean canMove = true;
        Rectangle futureRect = new Rectangle(this.getX(), this.getY(), this.getW(), this.getH());
        switch (this.fx) {
            case "up":
                futureRect.y -= this.speed;
                break;
            case "down":
                futureRect.y += this.speed;
                break;
            case "left":
                futureRect.x -= this.speed;
                break;
            case "right":
                futureRect.x += this.speed;
                break;
        }

        for (ElementObj map : maps) {
            if (futureRect.intersects(map.getRectangle())) {
                canMove = false;
                break;
            }
        }

        // 如果不会发生碰撞，才进行移动
        if (canMove) {
            switch (this.fx) {
                case "up":
                    this.setY(this.getY() - this.speed);
                    break;
                case "down":
                    this.setY(this.getY() + this.speed);
                    break;
                case "left":
                    this.setX(this.getX() - this.speed);
                    break;
                case "right":
                    this.setX(this.getX() + this.speed);
                    break;
            }
        } else {
            // 如果发生碰撞，选择一个不会导致进一步碰撞的方向
            switch (this.fx) {
                case "up":
                    this.up = false;
                    if (this.getY() + this.getH() < 555) {
                        this.down = true;
                        this.fx = "down";
                    } else {
                        this.left = true;
                        this.fx = "left";
                    }
                    break;
                case "down":
                    this.down = false;
                    if (this.getY() > 0) {
                        this.up = true;
                        this.fx = "up";
                    } else {
                        this.left = true;
                        this.fx = "left";
                    }
                    break;
                case "left":
                    this.left = false;
                    if (this.getX() + this.getW() < 780) {
                        this.right = true;
                        this.fx = "right";
                    } else {
                        this.up = true;
                        this.fx = "up";
                    }
                    break;
                case "right":
                    this.right = false;
                    if (this.getX() > 0) {
                        this.left = true;
                        this.fx = "left";
                    } else {
                        this.up = true;
                        this.fx = "up";
                    }
                    break;
            }
        }
    }
}