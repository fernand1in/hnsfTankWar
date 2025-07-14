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
import com.tedu.element.MapObj;
import com.tedu.element.Play;

/**
 * @说明  加载器(工具：用户读取配置文件的工具)工具类,大多提供的是 static方法
 * @author renjj
 *
 */
public class GameLoad {
	
//	得到资源管理器
	private static ElementManager em=ElementManager.getManager();
	
//	图片集合  使用map来进行存储     枚举类型配合移动(扩展)
	public static Map<String,ImageIcon> imgMap = new HashMap<>();
	
	public static Map<String,List<ImageIcon>> imgMaps;

//	用户读取文件的类
	private static Properties pro =new Properties();	
	/**
	 * @说明 传入地图id有加载方法依据文件规则自动产生地图文件名称，加载文件
	 * @param mapId  文件编号 文件id
	 */
	public static void MapLoad(int mapId) {
		 // 清空现有地图元素
	    ElementManager em = ElementManager.getManager();
	    em.getGameElements().get(GameElement.MAPS).clear();
	    // 生成地图文件的名称
	    String mapName = "com/tedu/text/" + mapId + ".map";
	    System.out.println("地图文件路径: " + mapName);
	    // 获取类加载器
	    ClassLoader classLoader = GameLoad.class.getClassLoader();
	    // 使用类加载器获取地图文件的输入流
	    InputStream maps = classLoader.getResourceAsStream(mapName);
	    if (maps == null) {
	        System.out.println("配置文件读取异常,请重新安装");
	        return;
	    }
	    try {
	        // 清空 Properties 对象
	        pro.clear();
	        // 加载地图文件
	        pro.load(maps);
	        // 获取所有的键
	        Enumeration<?> names = pro.propertyNames();
	        while (names.hasMoreElements()) {
	            // 获取键
	            String key = names.nextElement().toString();
	            // 获取键对应的值，并按分号分割
	            String[] arrs = pro.getProperty(key).split(";");
	            for (int i = 0; i < arrs.length; i++) {
	                // 创建地图元素对象
	                ElementObj element = new MapObj().createElement(key + "," + arrs[i]);
	                // 将地图元素对象添加到元素管理器中
	                em.addElement(element, GameElement.MAPS);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	/**
	 *@说明 加载图片代码
	 *加载图片 代码和图片之间差 一个 路径问题 
	 */
	public static void loadImg() {//可以带参数，因为不同的关也可能需要不一样的图片资源
		String texturl="com/tedu/text/GameData.pro";//文件的命名可以更加有规律
		ClassLoader classLoader = GameLoad.class.getClassLoader();
		InputStream texts = classLoader.getResourceAsStream(texturl);
//		imgMap用于存放数据
		pro.clear();
		try {
//			System.out.println(texts);
			pro.load(texts);
			Set<Object> set = pro.keySet();//是一个set集合
			for(Object o:set) {
				String url=pro.getProperty(o.toString());
				imgMap.put(o.toString(), new ImageIcon(url));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 加载玩家
	 */
	public static void loadPlay() {
		loadObj();
		String playStr="500,500,up";
		ElementObj obj=getObj("play");  
		ElementObj play = obj.createElement(playStr);
//		ElementObj play = new Play().createElement(playStr);
//		解耦,降低代码和代码之间的耦合度 可以直接通过 接口或者是抽象父类就可以获取到实体对象
		em.addElement(play, GameElement.PLAY);
	}
	public static void loadEnemies() {
	    // 加载敌人对象
	    String enemyStr = "100,100,bot_up"; // 示例敌人位置和类型
	    ElementObj obj = getObj("enemy");
	    ElementObj enemy = obj.createElement(enemyStr);
	    em.addElement(enemy, GameElement.ENEMY);
	}

	public static ElementObj getObj(String str) {
		try {
			Class<?> class1 = objMap.get(str);
			Object newInstance = class1.newInstance();
			if(newInstance instanceof ElementObj) {
				return (ElementObj)newInstance;   //这个对象就和 new Play()等价
//				新建立啦一个叫  GamePlay的类
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 扩展： 使用配置文件，来实例化对象 通过固定的key(字符串来实例化)
	 * @param args
	 */
	private static Map<String,Class<?>> objMap=new HashMap<>();
	
	public static void loadObj() {
		String texturl="com/tedu/text/obj.pro";//文件的命名可以更加有规律
		ClassLoader classLoader = GameLoad.class.getClassLoader();
		InputStream texts = classLoader.getResourceAsStream(texturl);
		pro.clear();
		try {
			pro.load(texts);
			Set<Object> set = pro.keySet();//是一个set集合
			for(Object o:set) {
				String classUrl=pro.getProperty(o.toString());
//				使用反射的方式直接将 类进行获取
				Class<?> forName = Class.forName(classUrl);
				objMap.put(o.toString(), forName);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
//	用于测试
	public static void main(String[] args) {
		GameLoad.MapLoad(2);
	}
}
	
	
	
	
	
	

