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
    private int speed = 1; // 敌方坦克移动速度
    private ElementManager em = ElementManager.getManager(); // 元素管理器

    private String fx = "up"; // 方向 (只需要保留这个方向变量)
    private long lastFireTime = 0; // 记录上次发射子弹的时间
    private int fireInterval = 2000; // 发射间隔(毫秒)
    
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
        int safeMargin = 50; // 安全边距，确保不靠近边界
        do {
            collisionDetected = false;
            for (ElementObj map : maps) {
                if (this.getRectangle().intersects(map.getRectangle())) {
                    collisionDetected = true;
                    break;
                }
            }
            if (collisionDetected) {
                // 如果发生碰撞，重新生成位置（避开边界）
                Random ran = new Random();
                // 确保生成的位置离边界有safeMargin的距离
                this.setX(safeMargin + ran.nextInt(GameJFrame.GameX - this.getW() - 2 * safeMargin));
                this.setY(safeMargin + ran.nextInt(GameJFrame.GameY - this.getH() - 2 * safeMargin));
            }
        } while (collisionDetected);

        // 设置初始方向与图像一致
        this.fx = split[2]; // 使用传入的方向参数
        this.setIcon(new ImageIcon("image/tank/bot/bot_" + this.fx + ".png"));

        return this;
    }

    @Override
    protected void move() {
        List<ElementObj> maps = em.getElementsByKey(GameElement.MAPS);
        boolean canMove = true;
        
        // 预先计算下一步的位置
        int newX = this.getX();
        int newY = this.getY();
        
        switch (this.fx) {
            case "up":
                newY -= this.speed;
                break;
            case "down":
                newY += this.speed;
                break;
            case "left":
                newX -= this.speed;
                break;
            case "right":
                newX += this.speed;
                break;
        }

        // 创建新的矩形用于碰撞检测
        Rectangle futureRect = new Rectangle(newX, newY, this.getW(), this.getH());

        // 边界检测（确保坦克完全在画面内）
        // 使用实际地图边界（800×600）
        int mapRightBound = 760 - this.getW();  // 右边界
        int mapBottomBound = 550 - this.getH(); // 下边界
        
        if (newX <= 0 || 
            newY <= 0 || 
            newX >= mapRightBound || 
            newY >= mapBottomBound) {
            canMove = false;
        }

        // 墙体碰撞检测
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
            // 碰撞后随机选择新方向
            Random random = new Random();
            String[] directions = {"up", "down", "left", "right"};
            this.fx = directions[random.nextInt(4)];
            this.setIcon(new ImageIcon("image/tank/bot/bot_" + this.fx + ".png"));
        }
    }
    @Override
    protected void add(long gameTime) {
        // 敌人自动发射子弹
        if (gameTime - lastFireTime > fireInterval) {
            lastFireTime = gameTime;
            ElementObj bullet = GameLoad.getObj("enemyFile");
            if (bullet != null) {
                ElementObj element = bullet.createElement(this.toString());
                em.addElement(element, GameElement.ENEMYFILE);
                System.out.println("敌人发射子弹: " + element);
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
}