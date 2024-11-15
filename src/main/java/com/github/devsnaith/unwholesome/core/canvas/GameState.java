package com.github.devsnaith.unwholesome.core.canvas;

import java.awt.Graphics;

import com.github.devsnaith.unwholesome.core.QConsole;

public abstract class GameState {

	private QCanvas Canvas;

	public GameState(QCanvas Canvas) {
		if (Canvas == null) {
			QConsole.print(QConsole.Status.SUPER,
					String.valueOf(getClass().getSimpleName()) + " parameter must not be null");
		}
		this.Canvas = Canvas;
	}

	public QCanvas getCanvas() {
		return this.Canvas;
	}

	public abstract void onLoad();

	public abstract void enable();

	public abstract void disable();

	public abstract void update();

	public abstract void render(Graphics paramGraphics);
}