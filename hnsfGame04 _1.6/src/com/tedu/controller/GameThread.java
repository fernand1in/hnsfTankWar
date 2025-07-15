package com.tedu.controller;

import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import com.tedu.element.ElementObj;
import com.tedu.element.Enemy;
import com.tedu.element.Play;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameMainJPanel;

public class GameThread extends Thread {
    private ElementManager em;
    private static int level = 1;
    private GameMainJPanel panel;
    private static boolean GameContinue = false;//是否继续游戏的标志
    private boolean paused = false; // 暂停标志
    private boolean isGameStarted = false; // 游戏启动标志
    
    

    public GameThread(GameMainJPanel panel) {
        em = ElementManager.getManager();
        this.panel = panel;
    }

    public static void setLevel(int level) {
        GameThread.level = level;
    }

    // 添加设置游戏启动的方法
    public void startGame() {
        this.isGameStarted = true;
    }

    @Override
    public void run() {
        while (true) {
            if (isGameStarted) {
                gameLoad();
                gameRun();
                gameOver();
            }
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void gameLoad() {
        GameLoad.loadImg();
        GameLoad.MapLoad(level); 
        GameLoad.loadPlay();
        GameLoad.loadEnemies();

        List<ElementObj> enemys = em.getElementsByKey(GameElement.ENEMY);
        for (ElementObj enemy : enemys) {
            if (enemy instanceof Enemy) {
                ((Enemy) enemy).add(System.currentTimeMillis()); 
            }
        }
    }

    private void gameRun() {
        long gameTime = 0L;
        while (true) {
            Map<GameElement, List<ElementObj>> all = em.getGameElements();
            List<ElementObj> enemys = em.getElementsByKey(GameElement.ENEMY);
            List<ElementObj> files = em.getElementsByKey(GameElement.PLAYFILE);
            List<ElementObj> maps = em.getElementsByKey(GameElement.MAPS);
            List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY);
            List<ElementObj> enemyFiles = em.getElementsByKey(GameElement.ENEMYFILE);

            moveAndUpdate(all, gameTime);
            ElementPK(enemys, files);
            ElementPK(files, maps);

            for (ElementObj enemy : enemys) {
                enemy.model(gameTime); 
            }

            for (ElementObj enemy : enemys) {
                for (ElementObj map : maps) {
                    enemy.pk(map);
                }
            }

            for (ElementObj play : plays) {
                for (ElementObj map : maps) {
                    play.pk(map);
                }
            }

            for (ElementObj play : plays) {
                for (ElementObj enemyFile : enemyFiles) {
                    play.pk(enemyFile);
                }
            }

            // 检查游戏状态
            checkGameStatus(plays, enemys);

            gameTime++;
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void ElementPK(List<ElementObj> listA, List<ElementObj> listB) {
        for (int i = 0; i < listA.size(); i++) {
            ElementObj enemy = listA.get(i);
            for (int j = 0; j < listB.size(); j++) {
                ElementObj file = listB.get(j);
                if (enemy.pk(file)) {
                    enemy.setLive(false);
                    file.setLive(false);
                    break;
                }
            }
        }
    }

    public void moveAndUpdate(Map<GameElement, List<ElementObj>> all, long gameTime) {
        for (GameElement ge : GameElement.values()) {
            List<ElementObj> list = all.get(ge);
            for (int i = list.size() - 1; i >= 0; i--) {
                ElementObj obj = list.get(i);
                if (!obj.isLive()) {
                    obj.die();
                    list.remove(i);
                    continue;
                }
                obj.model(gameTime);
            }
        }
    }

    private void gameOver() {
    }

    private void checkGameStatus(List<ElementObj> plays, List<ElementObj> enemys) {
        if (!plays.isEmpty()) {
            Play player = (Play) plays.get(0);
            if (player.getLives() <= 0) {
                panel.setGameOver(true);
            }
        }

        if (enemys.isEmpty()) {
            panel.setGameWin(true);
        }
    }
    public static void setContinue_status(boolean GameContinue) {
        GameThread.GameContinue = GameContinue;
    }
    public static int getLevel() {
        return level;
    }
    public synchronized void setPaused(boolean paused) {
        this.paused = paused;
        if (!paused) {
            notify();
        }
    }
}