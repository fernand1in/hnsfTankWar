package com.tedu.show;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.tedu.controller.GameListener;
import com.tedu.controller.GameThread;

/**
 * @说明 游戏窗体 主要实现的功能：关闭，显示，最大最小化
 * @author renjj
 * @功能说明   需要嵌入面板,启动主线程等等
 * @窗体说明  swing awt  窗体大小（记录用户上次使用软件的窗体样式）
 * 
 * @分析 1.面板绑定到窗体
 *       2.监听绑定
 *       3.游戏主线程启动
 *       4.显示窗体
 */
public class GameJFrame extends JFrame{
	public static int GameX = 800;//GAMEX 
	public static int GameY = 600;
	private JPanel jPanel =null; //正在现实的面板
	private KeyListener  keyListener=null;//键盘监听
	private MouseMotionListener mouseMotionListener=null; //鼠标监听
	private MouseListener mouseListener=null;
	private GameThread thead=null;  //游戏主线程
	private boolean PausedYes = false; // 游戏是否暂停
	private JDialog pauseDialog; // 暂停对话框

	
	public GameJFrame() {
		init();
	}
	public void init() {
		this.setSize(GameX, GameY); //设置窗体大小
		this.setTitle("坦克大战");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置退出并且关闭
		this.setLocationRelativeTo(null);//屏幕居中显示
		 this.setResizable(false); // 设置窗体不可调整大小
//		。。。。
	}
	/*窗体布局: 可以讲 存档，读档。button   给大家扩展的*/
	public void addButton() {
//		this.setLayout(manager);//布局格式，可以添加控件
	}	
	/**
	 * 启动方法
	 */
	public void start() {
		if(jPanel!=null) {
			this.add(jPanel);
		}
		if(keyListener !=null) {
			this.addKeyListener(keyListener);
		}
		if(thead !=null) {
			thead.start();//启动线程
		}
//		this.show();
		this.setVisible(true);//显示界面
//		如果jp 是 runnable的 子类实体对象 
//		如果这个判定无法进入就是 instanceof判定为 false 那么 jpanel没有实现runnable接口
		if(this.jPanel instanceof Runnable) {
//			已经做类型判定，强制类型转换不会出错
//			new Thread((Runnable)this.jPanel).start();
			Runnable run=(Runnable)this.jPanel;
			Thread th=new Thread(run);
			th.start();// 
			System.out.println("是否启动");
		}
		
	}
	
	
	/*set注入：等大家学习ssm 通过set方法注入配置文件中读取的数据;讲配置文件
	 * 中的数据赋值为类的属性
	 * 构造注入：需要配合构造方法
	 * spring 中ioc 进行对象的自动生成，管理。
	 * */
	public void setjPanel(JPanel jPanel) {
		this.jPanel = jPanel;
	}
	public void setKeyListener(GameListener listener) {
	    this.keyListener = listener;
	    this.addKeyListener(listener); // 添加监听器到JFrame
	    this.setFocusable(true);       // 确保JFrame可以获得焦点
	    this.requestFocusInWindow();   // 请求焦点
	}
	public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
		this.mouseMotionListener = mouseMotionListener;
	}
	public void setMouseListener(MouseListener mouseListener) {
		this.mouseListener = mouseListener;
	}
	public void setThead(Thread thead) {
		this.thead = (GameThread) thead;
	}
	 public void startGame(int level) {
	        if (jPanel == null) {
	            createGamePanel();
	        }
	        if (jPanel != null) {
	            this.setContentPane(jPanel);
	            this.revalidate();  // 重新验证窗口，以确保新内容正确布局
	        }

	        GameThread.setLevel(level);
	        GameThread.setContinue_status(false); // 让游戏不暂停，重新开始
	        thead.setPaused(false); // 确保游戏未暂停
	        resumeGame();

	        System.out.println("重新绘画");

	        // 启动游戏线程
            thead.startGame();

	        // 启动新的游戏线程
	        if (this.jPanel instanceof Runnable) {
	            Runnable run = (Runnable) this.jPanel;
	            Thread newThread = new Thread(run);
	            newThread.start();
	        }
	    }
	 
	public void createGamePanel() {
        GameMainJPanel gamePanel = new GameMainJPanel();
        setjPanel(gamePanel);
    }
    public void showStartPanel() {
        // 切换回开始面板
        this.setContentPane(new StartPanel(this));
        this.revalidate();
    }
    public void showLevelPanel(GameJFrame parentFrame) {
        // 切换到关卡选择面板
        this.setContentPane(new LevelPanel(this));
        this.revalidate();
    }
    public void pauseGame() {
        if (thead != null) {
            PausedYes = true;
            thead.setPaused(true); // 设置暂停标志
        }
        showPauseDialog();
    }
    public void resumeGame() {
        if (thead != null) {
        	PausedYes = false;
            thead.setPaused(false); // 取消暂停标志
        }
        if (pauseDialog != null) {
            pauseDialog.dispose();
        }
    }
    private void showPauseDialog() {
        pauseDialog = new JDialog(this, "暂停游戏", true);
        pauseDialog.setSize(350, 350);
        pauseDialog.setLocationRelativeTo(this);
        pauseDialog.setLayout(new GridBagLayout()); // 使用 GridBagLayout

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // 设置列索引为0
        gbc.gridy = GridBagConstraints.RELATIVE; // 设置行索引相对
        gbc.insets = new Insets(10, 0, 10, 0); // 设置按钮之间的垂直间距
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        gbc.anchor = GridBagConstraints.CENTER; // 居中显示

        JButton resumeButton = new JButton("继续游戏");
        resumeButton.setPreferredSize(new Dimension(200, 50));
        resumeButton.setFocusPainted(false);  // 移除焦点框
        resumeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // 设置鼠标悬浮手形
        resumeButton.setFont(new Font("Serif", Font.BOLD, 20));  // 设置字体

        JButton nextLevelButton = new JButton("下一关");
        nextLevelButton.setPreferredSize(new Dimension(200, 50));
        nextLevelButton.setFocusPainted(false);
        nextLevelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextLevelButton.setFont(new Font("Serif", Font.BOLD, 20));

        JButton prevLevelButton = new JButton("上一关");
        prevLevelButton.setPreferredSize(new Dimension(200, 50));
        prevLevelButton.setFocusPainted(false);
        prevLevelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        prevLevelButton.setFont(new Font("Serif", Font.BOLD, 20));

        JButton saveAndExitButton = new JButton("保存并返回主菜单");
        saveAndExitButton.setPreferredSize(new Dimension(200, 50));
        saveAndExitButton.setFocusPainted(false);
        saveAndExitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveAndExitButton.setFont(new Font("Serif", Font.BOLD, 20));

        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resumeGame();
            }
        });

        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                levelUp();
                resumeGame();
            }
        });

        prevLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                levelDown();
                resumeGame();
            }
        });

        saveAndExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGame();
                returnToMainMenu();
            }
        });

        pauseDialog.add(resumeButton, gbc);
        pauseDialog.add(nextLevelButton, gbc);
        pauseDialog.add(prevLevelButton, gbc);
        pauseDialog.add(saveAndExitButton, gbc);

        pauseDialog.setVisible(true);
    }
    private void levelUp() {
        // 跳转到下一关
        GameThread.setLevel(GameThread.getLevel() + 1);
        startGame(GameThread.getLevel());
    }
    private void levelDown() {
        // 跳转到上一关
        GameThread.setLevel(GameThread.getLevel() - 1);
        startGame(GameThread.getLevel());
    }
    private void saveGame() {
        // 实现保存游戏逻辑
        GameThread.setContinue_status(true);
        System.out.println("游戏已保存");
    }
    private void returnToMainMenu() {
        // 返回主菜单
        resumeGame();
        showStartPanel();
    }



}





