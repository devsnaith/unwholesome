package com.github.devsnaith.unwholesome.io.listeners;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class QKeyboardListener extends KeyAdapter {
	private HashMap<Integer, Boolean> Keys = new HashMap<>();

	public void resetKey(int KeyCode) {
		this.Keys.replace(Integer.valueOf(KeyCode), Boolean.valueOf(false));
	}

	public void initializeKey(int KeyCode) {
		this.Keys.put(Integer.valueOf(KeyCode), Boolean.valueOf(false));
	}

	public void removeKey(int KeyCode) {
		this.Keys.remove(Integer.valueOf(KeyCode));
	}

	public boolean isPressed(int KeyCode) {
		try {
			return ((Boolean) this.Keys.get(Integer.valueOf(KeyCode))).booleanValue();
		} catch (NullPointerException error) {
			return false;
		}
	}

	public void keyPressed(KeyEvent e) {
		this.Keys.replace(Integer.valueOf(e.getKeyCode()), Boolean.valueOf(true));
	}

	public void keyReleased(KeyEvent e) {
		resetKey(e.getKeyCode());
	}
}