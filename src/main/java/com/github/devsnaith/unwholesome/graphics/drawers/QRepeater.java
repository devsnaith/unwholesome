package com.github.devsnaith.unwholesome.graphics.drawers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

public class QRepeater {
	private RepeaterStyle Style;

	public enum RepeaterStyle {
		REPEATED, CLOUDE;
	}

	private int cloudeXPos = 0;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private BufferedImage RepeatedImage;
	private BufferedImage outputImage;
	private boolean RepeatedHeight = false;

	private Point[] points;
	private int count = 0;
	private int speed = 0;

	public QRepeater(BufferedImage RepeatedImage, RepeaterStyle Style, int count, int speed, boolean RepeatedHeight) {
		this.Style = Style;
		if (Style == null) {
			Style = RepeaterStyle.REPEATED;
		}
		this.count = count;
		this.speed = speed;

		this.RepeatedHeight = RepeatedHeight;
		this.RepeatedImage = RepeatedImage;
	}

	public void update(int screenWidth, int screenHeight) {
		if (this.screenWidth != screenWidth) {
			this.screenWidth = screenWidth;

			if (this.RepeatedHeight) {
				this.screenHeight = screenHeight;
			} else {
				this.screenHeight = this.RepeatedImage.getHeight();
			}

			this.outputImage = new BufferedImage(this.screenWidth, this.screenHeight, 2);
			if (this.Style == RepeaterStyle.CLOUDE) {
				this.points = new Point[this.count];
				for (int i = 0; i < this.count; i++) {
					Random random = new Random();
					int Y = random.nextInt(screenHeight);
					int X = random.nextInt(screenWidth);
					if (this.RepeatedImage.getHeight() + Y > screenHeight) {
						Y = screenHeight - this.RepeatedImage.getHeight();
					}

					if (this.RepeatedImage.getWidth() + X > screenWidth) {
						X = screenWidth - this.RepeatedImage.getWidth();
					}
					this.points[i] = new Point(X, Y);
				}
			}
		}

		Graphics gl = this.outputImage.getGraphics();
		int[] Pixels = ((DataBufferInt) this.outputImage.getRaster().getDataBuffer()).getData();
		for (int Pixel = 0; Pixel < Pixels.length; Pixel++) {
			Pixels[Pixel] = (Pixels[Pixel] != 0) ? 0 : Pixels[Pixel];
		}

		switch (this.Style) {
		case REPEATED:
			updateRepeated(gl);
			break;
		default:
			updateCloud(gl);
			break;
		}
		gl.dispose();
	}

	private void updateRepeated(Graphics gl) {
		for (int X = 0; X < this.screenWidth; X += this.RepeatedImage.getWidth())
			gl.drawImage(this.RepeatedImage, X, 0, null);
	}

	private void updateCloud(Graphics gl) {
		byte b;
		int i;
		Point[] arrayOfPoint;
		for (i = (arrayOfPoint = this.points).length, b = 0; b < i;) {
			Point point = arrayOfPoint[b];
			gl.drawImage(this.RepeatedImage, point.x + this.cloudeXPos, point.y, null);
			gl.drawImage(this.RepeatedImage, point.x + this.cloudeXPos - this.outputImage.getWidth(), point.y, null);
			b++;
		}

		if (this.cloudeXPos > this.outputImage.getWidth()) {
			this.cloudeXPos = 0;
		}

		this.cloudeXPos += this.speed;
	}

	public BufferedImage getDraw() {
		return this.outputImage;
	}
}