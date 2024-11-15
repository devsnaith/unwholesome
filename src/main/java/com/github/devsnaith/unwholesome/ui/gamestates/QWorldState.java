package com.github.devsnaith.unwholesome.ui.gamestates;

import com.github.devsnaith.unwholesome.calculation.QLocation;
import com.github.devsnaith.unwholesome.core.canvas.GameState;
import com.github.devsnaith.unwholesome.core.canvas.QCanvas;
import com.github.devsnaith.unwholesome.graphics.drawers.QRepeater;
import com.github.devsnaith.unwholesome.io.QloadImage;

import java.awt.Color;
import java.awt.Graphics;

public class QWorldState extends GameState {
	private Color backgroundColor;
	private QRepeater grassRepeater;
	private QLocation grassLocation;
	private QRepeater[] Clouds;

	public QWorldState(QCanvas Canvas) {
		super(Canvas);
	}

	public void onLoad() {
		this.backgroundColor = new Color(3735865);

		this.grassRepeater = new QRepeater(QloadImage.getAllImage("/kurzex_draw/KurzexGrass0.png"),
				QRepeater.RepeaterStyle.REPEATED, 0, 0, false);
		this.grassLocation = new QLocation(QloadImage.getLastImage(), QLocation.Locations.LEFT,
				QLocation.Locations.BOTTOM);

		this.Clouds = new QRepeater[] {
				new QRepeater(QloadImage.getAllImage("/kurzex_draw/KurzexCloud0.png"), QRepeater.RepeaterStyle.CLOUDE, 4, 1, true),
				new QRepeater(QloadImage.getAllImage("/kurzex_draw/KurzexCloud0.png"), QRepeater.RepeaterStyle.CLOUDE, 2, 2,
						true) };
	}

	public void enable() {
	}

	public void disable() {
	}

	public void initialzieLocations(int screenWidth, int screenHeight) {
		this.grassLocation.initialize(screenWidth, screenHeight);
		byte b;
		int i;
		QRepeater[] arrayOfQRepeater;
		for (i = (arrayOfQRepeater = this.Clouds).length, b = 0; b < i;) {
			QRepeater Cloud = arrayOfQRepeater[b];
			Cloud.update(screenWidth, screenHeight / 3);
			b++;
		}
	}

	public void update() {
		initialzieLocations(getCanvas().getWidth(), getCanvas().getHeight());
		this.grassRepeater.update(getCanvas().getWidth(), getCanvas().getHeight());
	}

	public void render(Graphics gl) {
		gl.setColor(this.backgroundColor);
		gl.fillRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight());
		gl.drawImage(this.grassRepeater.getDraw(), this.grassLocation.getX(), this.grassLocation.getY(), null);
		byte b;
		int i;
		QRepeater[] arrayOfQRepeater;
		for (i = (arrayOfQRepeater = this.Clouds).length, b = 0; b < i;) {
			QRepeater Cloud = arrayOfQRepeater[b];
			gl.drawImage(Cloud.getDraw(), 0, 0, null);
			b++;
		}
	}
}