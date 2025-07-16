package com.tedu.element;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.ImageIcon;

import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;

public class Play extends ElementObj {
    private boolean left = false; // 左
    private boolean up = false;   // 上
    private boolean right = false;// 右
    private boolean down = false; // 下
    private int lives = 3; // 初始生命值
    private String fx = "up"; // 当前面向方向
    private boolean pkType = false; // 攻击状态

    private int moveSpeed = 2; // 每次移动2像素（默认速度）
    private long speedBoostEndTime = 0; // 速度提升结束时间

    public Play() {}
    public Play(int x, int y, int w, int h, ImageIcon icon) {
        super(x, y, w, h, icon);
    }

    public String getFx() {
        return this.fx;
    }

    @Override
    public ElementObj createElement(String str) {
        String[] split = str.split(",");
        this.setX(Integer.parseInt(split[0]));
        this.setY(Integer.parseInt(split[1]));
        ImageIcon icon2 = GameLoad.imgMap.get(split[2]);
        this.setW(icon2.getIconWidth());
        this.setH(icon2.getIconHeight());
        this.setIcon(icon2);
        return this;
    }

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(), 
                this.getX(), this.getY(), 
                this.getW(), this.getH(), null);
    }

    @Override
    public void keyClick(boolean bl, int key) {
        if (bl) { // 按下按键
            switch (key) {
            case 37: // 左
                this.down = false; this.up = false;
                this.right = false; this.left = true; this.fx = "left"; break;
            case 38: // 上
                this.right = false; this.left = false;
                this.down = false; this.up = true; this.fx = "up"; break;
            case 39: // 右
                this.down = false; this.up = false;
                this.left = false; this.right = true; this.fx = "right"; break;
            case 40: // 下
                this.right = false; this.left = false;
                this.up = false; this.down = true; this.fx = "down"; break;
            case 32: // 空格（攻击）
                this.pkType = true; break;
            }
        } else { // 松开按键
            switch (key) {
            case 37: this.left = false; break;
            case 38: this.up = false; break;
            case 39: this.right = false; break;
            case 40: this.down = false; break;
            case 32: this.pkType = false; break;
            }
        }
    }

    @Override
    public void move() {
        if (System.currentTimeMillis() > speedBoostEndTime) {
            moveSpeed = 2; // 恢复默认速度
        }

        boolean canMove = true; // 标志是否可以移动

        // 检查与地图元素的碰撞（碰撞则不能移动）
        List<ElementObj> maps = ElementManager.getManager().getElementsByKey(GameElement.MAPS);
        for (ElementObj map : maps) {
            Rectangle futureRect = new Rectangle(this.getX(), this.getY(), this.getW(), this.getH());
            // 计算移动后的碰撞矩形（根据速度预判）
            switch (this.fx) {
                case "up": futureRect.y -= moveSpeed; break;
                case "down": futureRect.y += moveSpeed; break;
                case "left": futureRect.x -= moveSpeed; break;
                case "right": futureRect.x += moveSpeed; break;
            }
            if (futureRect.intersects(map.getRectangle())) {
                canMove = false;
                break;
            }
        }

        // 若可移动，根据方向更新位置（使用moveSpeed控制步长）
        if (canMove) {
            if (this.left && this.getX() > 0) {
                this.setX(this.getX() - moveSpeed); // 左移：减少X坐标（步长=moveSpeed）
            }
            if (this.up && this.getY() > 0) {
                this.setY(this.getY() - moveSpeed); // 上移：减少Y坐标
            }
            if (this.right && this.getX() < 900 - this.getW()) {
                this.setX(this.getX() + moveSpeed); // 右移：增加X坐标
            }
            if (this.down && this.getY() < 600 - this.getH()) {
                this.setY(this.getY() + moveSpeed); // 下移：增加Y坐标
            }
        }
    }

    @Override
    protected void updateImage() {
        this.setIcon(GameLoad.imgMap.get(fx));
    }

    private long filetime = 0;
    @Override
    public void add(long gameTime) {
        if (!this.pkType) {
            return;
        }
        this.pkType = false;
        ElementObj obj = GameLoad.getObj("file");  		
        ElementObj element = obj.createElement(this.toString());
        ElementManager.getManager().addElement(element, GameElement.PLAYFILE);
    }

    @Override
    public String toString() {
        int x = this.getX();
        int y = this.getY();
        switch (this.fx) {
            case "up": x += 20; break;
            case "left": y += 20; break;
            case "right": x += 50; y += 20; break;
            case "down": y += 50; x += 20; break;
        }
        return "x:" + x + ",y:" + y + ",f:" + this.fx;
    }

    @Override
    public void handleCollision(ElementObj obj) {
        if (obj instanceof EnemyFile) {
            lives--;
            obj.setLive(false);
            if (lives <= 0) {
                this.setLive(false);
                System.out.println("游戏结束！");
            }
        } else if (obj instanceof Prop) {
            Prop prop = (Prop) obj;
            increaseSpeed(); // 调用加速方法
            prop.setLive(false); // 道具消失
        }
    }

    public void increaseSpeed() {
        moveSpeed = 5; // 提升速度
        speedBoostEndTime = System.currentTimeMillis() + 10000; // 10秒后恢复默认速度
    }

    public int getLives() {
        return lives;
    }
}