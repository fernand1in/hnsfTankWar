package com.tedu.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tedu.element.ElementObj;

public class ElementManager {
    private Map<GameElement, List<ElementObj>> gameElements;	
    private int score = 0;

    public ElementManager() {
        init();
    }

    public void init() {
        gameElements = new HashMap<>();
        for (GameElement ge : GameElement.values()) {
            gameElements.put(ge, new ArrayList<ElementObj>());
        }
    }

    public Map<GameElement, List<ElementObj>> getGameElements() {
        return gameElements;
    }

    public void addElement(ElementObj obj, GameElement ge) {
        gameElements.get(ge).add(obj);
    }

    public List<ElementObj> getElementsByKey(GameElement ge) {
        return gameElements.get(ge);
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getScore() {
        return this.score;
    }

    // 单例模式
    private static ElementManager EM = null;

    public static synchronized ElementManager getManager() {
        if (EM == null) {
            EM = new ElementManager();
        }
        return EM;
    }
}