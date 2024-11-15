package com.github.devsnaith.unwholesome.calculation;

import java.awt.image.BufferedImage;

import com.github.devsnaith.unwholesome.core.QObject;

public class QLocation {
	public enum Locations {
		TOP, BOTTOM, LEFT, RIGHT, CENTER;
	}

	public enum Vector2d {
		X, Y;
	}

	private int xPos = 0;
	private int yPos = 0;

	private int ImageWidth = 0;
	private int ImageHeight = 0;

	private Locations x;
	private Locations y;

	public QLocation() {
	}

	public QLocation(int width, int height, Locations x, Locations y) {
		setImageSize(width, height, x, y);
	}

	public QLocation(BufferedImage Image, Locations x, Locations y) {
		if (Image == null) {
			return;
		}
		setImageSize(Image.getWidth(), Image.getHeight(), x, y);
	}

	public void setImageSize(int width, int height, Locations x, Locations y) {
		this.x = x;
		this.y = y;
		setImageSize(width, height);
	}

	public void setImageSize(int width, int height) {
		this.ImageWidth = width;
		this.ImageHeight = height;
	}

	public int getObjectSensitiveLocation(Vector2d vector, QObject qObject, int... addNumbers) {
		int position = 0;
		if (vector == null || qObject == null) {
			return position;
		}

		switch (vector) {
		case X:
			position = this.ImageWidth / 2 - qObject.getWidth() / 2;
			break;
		case Y:
			position = this.ImageHeight / 2 + qObject.getHeight() / 2;
			break;
		}

		int i, arrayOfInt[];
		int numbers = 0;
		byte b;

		for (i = (arrayOfInt = addNumbers).length, b = 0; b < i;) {
			int number = arrayOfInt[b];
			numbers += number;
			b++;
		}

		return numbers + position;
	}

	public void initialize(int screenWidth, int screenHeight) {
		if (this.x == null || this.y == null) {
			return;
		}

		switch (this.x) {
		case CENTER:
			this.xPos = screenWidth / 2 - this.ImageWidth / 2;
			break;
		case RIGHT:
			this.xPos = screenWidth - this.ImageWidth;
			break;
		default:
			this.xPos = 0;
			break;
		}

		switch (this.y) {
		case CENTER:
			this.yPos = screenHeight / 2 - this.ImageHeight / 2;
			return;
		case BOTTOM:
			this.yPos = screenHeight - this.ImageHeight;
			return;
		default:
			this.yPos = 0;
			break;
		}
	}

	public int getX() {
		return this.xPos;
	}

	public int getY() {
		return this.yPos;
	}

	public int getWidth() {
		return this.ImageWidth;
	}

	public int getHeight() {
		return this.ImageHeight;
	}
}
