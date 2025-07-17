package com.tedu.manager;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.swing.ImageIcon;

import com.tedu.controller.GameThread;
import com.tedu.element.Boss;
import com.tedu.element.BossBullet;
import com.tedu.element.ElementObj;
import com.tedu.element.Enemy;
import com.tedu.element.MapObj;
import com.tedu.element.Play;
import com.tedu.element.Prop;

public class GameLoad {
    private static ElementManager em = ElementManager.getManager();
    public static Map<String, ImageIcon> imgMap = new HashMap<>();
    public static Map<String, List<ImageIcon>> imgMaps;
    private static Properties pro = new Properties();

    public static void MapLoad(int mapId) {
        ElementManager em = ElementManager.getManager();
        em.getGameElements().get(GameElement.MAPS).clear();
        String mapName = "com/tedu/text/" + mapId + ".map"; // 动态生成地图文件路径
        System.out.println("地图文件路径: " + mapName);
        ClassLoader classLoader = GameLoad.class.getClassLoader();
        InputStream maps = classLoader.getResourceAsStream(mapName);
        if (maps == null) {
            System.out.println("配置文件读取异常,请重新安装");
            return;
        }
        try {
            pro.clear();
            pro.load(maps);
            Enumeration<?> names = pro.propertyNames();
            while (names.hasMoreElements()) {
                String key = names.nextElement().toString();
                String[] arrs = pro.getProperty(key).split(";");
                for (int i = 0; i < arrs.length; i++) {
                    ElementObj element = new MapObj().createElement(key + "," + arrs[i]);
                    em.addElement(element, GameElement.MAPS);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadImg() {
        String texturl = "com/tedu/text/GameData.pro";
        ClassLoader classLoader = GameLoad.class.getClassLoader();
        InputStream texts = classLoader.getResourceAsStream(texturl);
        pro.clear();
        try {
            pro.load(texts);
            Set<Object> set = pro.keySet();
            for (Object o : set) {
                String url = pro.getProperty(o.toString());
                imgMap.put(o.toString(), new ImageIcon(url));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadPlay() {
        loadObj();
        String playStr = "500,500,up";
        ElementObj obj = getObj("play");
        ElementObj play = obj.createElement(playStr);
        em.addElement(play, GameElement.PLAY);
    }

    public static void loadEnemies() {
        int level = GameThread.getLevel(); // 获取当前关卡号
        int enemyCount = 0; // 根据关卡号确定敌人数量
        if (level >= 1 && level <= 4) {
            enemyCount = 3;
        } else if (level >= 5 && level <= 7) {
            enemyCount = 4;
        } else if (level >= 8 && level <= 10) {
            enemyCount = 5;
        }

        // 生成敌人
        for (int i = 0; i < enemyCount; i++) {
            int x = 100 + i * 100; // 敌人的初始位置可以根据需求调整
            int y = 100 + i * 100;
            String direction = "up"; // 敌人的初始方向可以根据需求调整
            String enemyStr = x + "," + y + "," + direction;
            ElementObj enemy = new Enemy().createElement(enemyStr);
            em.addElement(enemy, GameElement.ENEMY);
        }
    }

    public static void loadProp(int propCount) {
        ElementManager em = ElementManager.getManager();
        List<ElementObj> maps = em.getElementsByKey(GameElement.MAPS);
        Random random = new Random();
        int gameWidth = 900; // 游戏窗口宽度
        int gameHeight = 600; // 游戏窗口高度
        int propWidth = 30; // 道具宽度
        int propHeight = 30; // 道具高度

        for (int i = 0; i < propCount; i++) {
            int x = 0; // 初始化 x
            int y = 0; // 初始化 y

            boolean validPosition = false;
            while (!validPosition) {
                x = random.nextInt(gameWidth - propWidth);
                y = random.nextInt(gameHeight - propHeight);
                Rectangle propRect = new Rectangle(x, y, propWidth, propHeight);
                boolean collisionDetected = false;
                for (ElementObj map : maps) {
                    if (propRect.intersects(map.getRectangle())) {
                        collisionDetected = true;
                        break;
                    }
                }
                if (!collisionDetected) {
                    validPosition = true;
                }
            }

            ElementObj prop = new Prop().createElement(x + "," + y);
            em.addElement(prop, GameElement.PROP); // 使用PROP作为道具集合的key
        }
    }
    
    public static void loadBoss(int level) {
        if (level == 4 || level == 7 || level == 10) {
            ElementManager em = ElementManager.getManager();
            List<ElementObj> maps = em.getElementsByKey(GameElement.MAPS);
            List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY); // 获取玩家对象列表

            int gameWidth = 900; // 游戏窗口宽度
            int gameHeight = 600; // 游戏窗口高度

            int bossWidth = 50; // 默认 Boss 宽度
            int bossHeight = 50; // 默认 Boss 高度

            // 如果玩家对象存在，获取玩家的高度和宽度
            if (!plays.isEmpty()) {
                Play player = (Play) plays.get(0);
                bossWidth = player.getW(); // 设置 Boss 宽度与玩家一致
                bossHeight = player.getH(); // 设置 Boss 高度与玩家一致
            }

            int bossCount = 1; // 默认 Boss 数量
            if (level == 7) {
                bossCount = 2; // 第七关有2个Boss
            } else if (level == 10) {
                bossCount = 3; // 第十关有3个Boss
            }

            for (int i = 0; i < bossCount; i++) {
                int x = 0; // 初始化 x
                int y = 0; // 初始化 y

                boolean validPosition = false;
                int boundaryMargin = 100; // 边界限制为100
                while (!validPosition) {
                    // 随机生成 Boss 的初始位置，确保在边界100个单位以内
                    x = boundaryMargin + (int) (Math.random() * (gameWidth - bossWidth - boundaryMargin));
                    y = boundaryMargin + (int) (Math.random() * (gameHeight - bossHeight - boundaryMargin));

                    // 检查是否与地图元素发生碰撞
                    Rectangle bossRect = new Rectangle(x, y, bossWidth, bossHeight);
                    boolean collisionDetected = false;
                    for (ElementObj map : maps) {
                        if (bossRect.intersects(map.getRectangle())) {
                            collisionDetected = true;
                            break;
                        }
                    }

                    // 检查是否在边界100个单位以内
                    if (x < boundaryMargin || x + bossWidth > gameWidth - boundaryMargin ||
                        y < boundaryMargin || y + bossHeight > gameHeight - boundaryMargin) {
                        collisionDetected = true;
                    }

                    // 如果没有碰撞，位置有效
                    if (!collisionDetected) {
                        validPosition = true;
                    }
                }

                // 创建并添加 Boss
                ElementObj boss = new Boss().createElement(x + "," + y + ",up");
                em.addElement(boss, GameElement.BOSS); // 使用BOSS作为Boss集合的key
            }
        }
    }
    public static ElementObj getObj(String str) {
        synchronized (GameLoad.class) { // 使用 synchronized 块保护对 objMap 的访问
            try {
                Class<?> class1 = objMap.get(str);
                if (class1 != null) {
                    Object newInstance = class1.newInstance();
                    if (newInstance instanceof ElementObj) {
                        return (ElementObj) newInstance;
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Map<String, Class<?>> objMap = new HashMap<>();

    public static void loadObj() {
        String texturl = "com/tedu/text/obj.pro";
        ClassLoader classLoader = GameLoad.class.getClassLoader();
        InputStream texts = classLoader.getResourceAsStream(texturl);
        pro.clear();
        try {
            pro.load(texts);
            Set<Object> set = pro.keySet();
            for (Object o : set) {
                String classUrl = pro.getProperty(o.toString());
                Class<?> forName = Class.forName(classUrl);
                objMap.put(o.toString(), forName);
            }
            // 手动添加BossBullet到objMap
            objMap.put("bossBullet", BossBullet.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}