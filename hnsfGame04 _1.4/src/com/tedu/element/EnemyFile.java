package com.tedu.element;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.ImageIcon;

public class EnemyFile extends ElementObj {
    private int moveNum = 3;
    private String fx;

    @Override
    public ElementObj createElement(String str) {
        String[] split = str.split(",");
        for(String str1 : split) {
            String[] split2 = str1.split(":");
            switch(split2[0]) {
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
        g.setColor(Color.yellow); // 敌人子弹用黄色区分
        g.fillOval(this.getX(), this.getY(), this.getW(), this.getH());
    }

    @Override
    protected void move() {
        if(this.getX()<0 || this.getX()>900 || this.getY()<0 || this.getY()>600) {
            this.setLive(false);
            return;
        }
        switch(this.fx) {
            case "up": this.setY(this.getY()-this.moveNum); break;
            case "left": this.setX(this.getX()-this.moveNum); break;
            case "right": this.setX(this.getX()+this.moveNum); break;
            case "down": this.setY(this.getY()+this.moveNum); break;
        }
    }
}