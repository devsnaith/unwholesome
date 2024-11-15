package com.github.devsnaith.unwholesome.core;

import com.github.devsnaith.unwholesome.calculation.QDirections;
import com.github.devsnaith.unwholesome.calculation.QLocation;
import com.github.devsnaith.unwholesome.io.listeners.QKeyboardListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class QObject {
	private int ObjectX = 0;
	private int ObjectY = 0;

	private int ObjectWidth = 0;
	private int ObjectHeight = 0;

	private BufferedImage ObjectBackground;
	private QKeyboardListener Keyboard;

	private Image ObjectImage;
	private QLocation ObjectImageLocation;
	private int upKeyCode = 87;
	private int downKeyCode = 83;
	private int leftKeyCode = 65;
	private int rightKeyCode = 68;

	private sensor Touch;
	private QObject[] TouchObject;

	public int objectSpeed = 1;

	private boolean accelerationEnabled = false;
	private float accelerationValue = 1;
	private double accelerationX, accelerationY = 0;

	public QObject(int x, int y, int width, int height) {
		this.ObjectBackground = new BufferedImage(width, height, 2);
		setLocation(x, y);

		this.ObjectWidth = width;
		this.ObjectHeight = height;
		this.ObjectImageLocation = new QLocation(0, 0, QLocation.Locations.CENTER, QLocation.Locations.CENTER);
	}

	public void acceleration(boolean acceleration, float accelerationValue) {
		this.accelerationValue = accelerationValue;
		this.accelerationEnabled = acceleration;
	}

	public void setImage(Image image) {
		this.ObjectImage = image;
		this.ObjectImageLocation.setImageSize(this.ObjectWidth, this.ObjectHeight);
	}

	public void dimension(Dimension dimension) {
		this.ObjectWidth = dimension.width;
		this.ObjectHeight = dimension.height;
	}

	public void setKeyboard(QKeyboardListener keyboard) {
		this.Keyboard = keyboard;
		this.Keyboard.initializeKey(this.upKeyCode);
		this.Keyboard.initializeKey(this.downKeyCode);
		this.Keyboard.initializeKey(this.leftKeyCode);
		this.Keyboard.initializeKey(this.rightKeyCode);
	}

	public void setKeyboard(QKeyboardListener Keyboard, int upKeyCode, int downKeyCode, int leftKeyCode,
			int rightKeyCode) {
		this.upKeyCode = upKeyCode;
		this.downKeyCode = downKeyCode;
		this.leftKeyCode = leftKeyCode;
		this.rightKeyCode = rightKeyCode;
		setKeyboard(Keyboard);
	}

	public int getMustX() {
		return this.ObjectX;
	}

	public int getMustY() {
		return this.ObjectY;
	}

	public int getWidth() {
		return this.ObjectWidth;
	}

	public int getHeight() {
		return this.ObjectHeight;
	}

	public void setLocation(int x, int y) {
		this.ObjectX = x;
		this.ObjectY = y;
	}

	public void setSpeed(int speed) {
		this.objectSpeed = speed;
	}

	public int getSpeed() {
		return this.objectSpeed;
	}

	public void update() {
		if (this.ObjectImage != null) {
			this.ObjectImageLocation.setImageSize(this.ObjectImage.getWidth(null), this.ObjectImage.getHeight(null));
			this.ObjectImageLocation.initialize(this.ObjectBackground.getWidth(), this.ObjectBackground.getHeight());
		}

		if (this.Touch != null && this.TouchObject != null) {
			QObject[] arrayOfQObject;
			byte b;
			int i;
			for (i = (arrayOfQObject = this.TouchObject).length, b = 0; b < i;) {
				QObject TouchObject = arrayOfQObject[b];
				boolean touched = false;
				try {
					if (this.ObjectX + this.ObjectWidth >= TouchObject.getMustX()
							&& this.ObjectX < TouchObject.getMustX() + TouchObject.getWidth()
							&& this.ObjectY + this.ObjectHeight >= TouchObject.getMustY()
							&& this.ObjectY < TouchObject.getMustY() + TouchObject.getHeight()) {
						this.Touch.touched(this, TouchObject);
						touched = true;
					}

					if (!touched) {
						this.Touch.notTouched(this);
					}
				} catch (NullPointerException nullPointerException) {
				}
				b++;
			}
		}

		if (this.Keyboard == null) {
			return;
		}

		int moveX = 0, moveY = 0;
		if (this.Keyboard.isPressed(this.upKeyCode)) {
			moveY--;
		}

		if (this.Keyboard.isPressed(this.downKeyCode)) {
			moveY++;
		}

		if (this.Keyboard.isPressed(this.leftKeyCode)) {
			moveX--;
		}

		if (this.Keyboard.isPressed(this.rightKeyCode)) {
			moveX++;
		}

		if (this.Touch != null) {
			QDirections Directions = new QDirections();

			if (moveX > 0) {
				Directions.setDirection(new QDirections.Directions[] { QDirections.Directions.EAST });
			} else if (moveX < 0) {
				Directions.setDirection(new QDirections.Directions[] { QDirections.Directions.WEST });
			}

			if (moveY > 0) {
				Directions.setDirection(new QDirections.Directions[] { QDirections.Directions.SOUTH });
			} else if (moveY < 0) {
				Directions.setDirection(new QDirections.Directions[] { QDirections.Directions.NORTH });
			}

			Directions = this.Touch.moveing(Directions);

			if (Directions.isDirection(QDirections.Directions.EAST)) {
				moveX = 1;
			} else if (Directions.isDirection(QDirections.Directions.WEST)) {
				moveX = -1;
			} else {
				moveX = 0;
			}

			if (Directions.isDirection(QDirections.Directions.NORTH)) {
				moveY = -1;
			} else if (Directions.isDirection(QDirections.Directions.SOUTH)) {
				moveY = 1;
			} else {
				moveY = 0;
			}
		}

		moveX *= this.objectSpeed;
		moveY *= this.objectSpeed;

		if(accelerationEnabled) {
			accelerationX = moveX > 0 ? (double) accelerationX + accelerationValue : moveX < 0 ? (double) accelerationX - accelerationValue : 0;
			accelerationY = moveY > 0 ? (double) accelerationY + accelerationValue : moveY < 0 ? (double) accelerationY - accelerationValue : 0;

			accelerationX = Math.abs(accelerationX) > Math.abs(moveX) ? moveX : accelerationX;
			accelerationY = Math.abs(accelerationY) > Math.abs(moveY) ? moveY : accelerationY;

			this.ObjectX += moveX > 0 ? Math.min(accelerationX, moveX) : Math.max(accelerationX, moveX);
			this.ObjectY += moveY > 0 ? Math.min(accelerationY, moveY) : Math.max(accelerationY, moveY);
		}else {
			this.ObjectX += moveX;
			this.ObjectY += moveY;
		}
	}

	public void setSensor(sensor Touch, QObject... arrayOfQObject) {
		this.Touch = Touch;
		this.TouchObject = arrayOfQObject;
	}

	public BufferedImage getDraw(boolean debug) {
		Graphics gl = this.ObjectBackground.getGraphics();

		if (this.ObjectBackground != null) {
			int[] Pixels = ((DataBufferInt) this.ObjectBackground.getRaster().getDataBuffer()).getData();
			for (int Pixel = 0; Pixel < Pixels.length; Pixel++) {
				Pixels[Pixel] = (Pixels[Pixel] != 0) ? 0 : Pixels[Pixel];
			}
		}

		if (debug) {
			gl.setColor(Color.RED);
			gl.fillRect(0, 0, this.ObjectBackground.getWidth(), this.ObjectBackground.getHeight());
		}

		if (this.ObjectImage != null) {
			gl.drawImage(this.ObjectImage, this.ObjectImageLocation.getX(), this.ObjectImageLocation.getY(), null);
		}

		gl.dispose();
		return this.ObjectBackground;
	}

	public static interface sensor {
		QDirections moveing(QDirections param1QDirections);

		void touched(QObject param1QObject1, QObject param1QObject2);

		void notTouched(QObject param1QObject);
	}
}