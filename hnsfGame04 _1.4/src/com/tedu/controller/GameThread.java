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

public class GameThread extends Thread {
    private ElementManager em;
    private int level = 1;

    public GameThread() {
        em = ElementManager.getManager();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void run() {
        while (true) {
            gameLoad();
            gameRun();
            gameOver();
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void gameLoad() {
        GameLoad.loadImg();
        GameLoad.MapLoad(level); // 使用设置的关卡加载地图
        GameLoad.loadPlay();
        GameLoad.loadEnemies();
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
                for (ElementObj map : maps) {
                    enemy.pk(map);
                }
            }

            for (ElementObj play : plays) {
                for (ElementObj map : maps) {
                    play.pk(map);
                }
            }

            for (ElementObj enemy : enemys) {
                enemy.model(gameTime);
            }
            
            // 检测玩家与敌人子弹的碰撞
            for (ElementObj play : plays) {
                for (ElementObj enemyFile : enemyFiles) {
                    play.pk(enemyFile);
                }
            }

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
}