package com.tedu.show;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.tedu.element.ElementObj;
import com.tedu.element.EnemyFile;
import com.tedu.element.Play;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;

/**
 * @说明 游戏的主要面板
 * @author renjj
 * @功能说明 主要进行元素的显示，同时进行界面的刷新(多线程)
 * 
 * @题外话 java开发实现思考的应该是：做继承或者是接口实现
 * 
 * @多线程刷新 1.本类实现线程接口
 *             2.本类中定义一个内部类来实现
 */
public class GameMainJPanel extends JPanel implements Runnable {
    // 联动管理器
    private ElementManager em;

    public GameMainJPanel() {
        init();
    }

    public void init() {
        em = ElementManager.getManager(); // 得到元素管理器对象
    }

    /**
     * paint方法是进行绘画元素。
     * 绘画时是有固定的顺序，先绘画的图片会在底层，后绘画的图片会覆盖先绘画的
     * 约定：本方法只执行一次,想实时刷新需要使用 多线程
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g); // 调用父类的paint方法
        Map<GameElement, List<ElementObj>> all = em.getGameElements();
        for (GameElement ge : GameElement.values()) {
            List<ElementObj> list = all.get(ge);
            for (int i = 0; i < list.size(); i++) {
                ElementObj obj = list.get(i); // 读取为基类
                if (obj.isLive()) { // 确保只绘制存活的对象
                    obj.showElement(g); // 调用每个类的自己的show方法完成自己的显示
                    if (obj instanceof EnemyFile) {
                        System.out.println("绘制敌人子弹: " + obj);
                    }
                }
            }
        }

        // 绘制玩家生命值
        List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY);
        if (!plays.isEmpty()) {
            Play player = (Play) plays.get(0);
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("LIVE: " + player.getLives(), 20, 30);
        }
    }

    @Override
    public void run() { // 接口实现
        while (true) {
            this.repaint(); // 重新绘制面板
            try {
                Thread.sleep(10); // 休眠10毫秒，1秒刷新100次
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}