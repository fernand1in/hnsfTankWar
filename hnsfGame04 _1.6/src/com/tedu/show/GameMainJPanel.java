package com.tedu.show;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.tedu.element.ElementObj;
import com.tedu.element.EnemyFile;
import com.tedu.element.Play;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;

public class GameMainJPanel extends JPanel implements Runnable {
    private ElementManager em;
    private boolean gameOver = false;
    private boolean gameWin = false;

    public GameMainJPanel() {
        init();
    }

    public void init() {
        em = ElementManager.getManager();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Map<GameElement, List<ElementObj>> all = em.getGameElements();
        for (GameElement ge : GameElement.values()) {
            List<ElementObj> list = all.get(ge);
            for (int i = 0; i < list.size(); i++) {
                ElementObj obj = list.get(i);
                if (obj.isLive()) { 
                    obj.showElement(g); 
                    if (obj instanceof EnemyFile) {
                        System.out.println("绘制敌人子弹: " + obj);
                    }
                }
            }
        }

        // 绘制玩家生命值和得分
        List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY);
        if (!plays.isEmpty()) {
            Play player = (Play) plays.get(0);
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("LIVES: " + player.getLives(), 20, 30);
            g.drawString("SCORE: " + em.getScore(), 20, 60);
        }

        // 显示游戏失败或胜利信息
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Over!", 300, 300);
        } else if (gameWin) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Win!", 300, 300);
        }
    }

    @Override
    public void run() {
        while (true) {
            this.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if (!gameOver) { // 当重置失败状态时
            this.repaint(); // 添加界面刷新
        }
    }

    public void setGameWin(boolean gameWin) {
        this.gameWin = gameWin;
    }
    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWin() {
        return gameWin;
    }

}