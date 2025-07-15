package com.tedu.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.ImageIcon;

import com.tedu.controller.GameThread;
import com.tedu.element.ElementObj;
import com.tedu.element.Enemy;
import com.tedu.element.MapObj;
import com.tedu.element.Play;

public class GameLoad {
    private static ElementManager em = ElementManager.getManager();
    public static Map<String, ImageIcon> imgMap = new HashMap<>();
    public static Map<String, List<ImageIcon>> imgMaps;
    private static Properties pro = new Properties();

    public static void MapLoad(int mapId) {
        ElementManager em = ElementManager.getManager();
        em.getGameElements().get(GameElement.MAPS).clear();
        String mapName = "com/tedu/text/" + mapId + ".map";
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
        String[] enemyStrs = {
            "100,100,left",
            "200,200,up",
            "300,300,right",
            "400,400,down"
        };
        for (String enemyStr : enemyStrs) {
            ElementObj enemy = new Enemy().createElement(enemyStr);
            em.addElement(enemy, GameElement.ENEMY);
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
//        System.out.println("未找到对象: " + str); // 调试日志
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
//                System.out.println("加载对象: " + o + " -> " + classUrl); // 调试日志
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}