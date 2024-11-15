package com.github.devsnaith.unwholesome.graphics.effects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class QFadeEffect {
	private float alpha = 0.0F;
	private long LastCurrTime = -1L;
	private BufferedImage[] Images;
	private BufferedImage output;
	private Color FadeColor = Color.BLACK;
	private long sleepBetweenImages;
	private long pauseTime;
	private float showSpeed;
	private boolean loop = false;
	private boolean Running = false;
	private int index = 0;

	public QFadeEffect(BufferedImage[] Images, long sleepBetweenImages, long pauseTime, float showSpeed) {
		this.Images = Images;
		this.sleepBetweenImages = sleepBetweenImages;
		this.pauseTime = pauseTime;
		this.showSpeed = showSpeed;
	}

	public void start() {
		this.Running = true;
	}

	public void stop() {
		this.Running = false;
	}


	private void initializeOutput(BufferedImage Image) {
		if (this.output == null || this.output.getWidth() != Image.getWidth()
				|| this.output.getHeight() != Image.getHeight()) {
			this.output = new BufferedImage(Image.getWidth(), Image.getHeight(), 2);
		}
	}

	public void update() {
		if (!this.Running)
			return;
		initializeOutput(this.Images[Math.min(this.index, this.Images.length - 1)]);
		this.LastCurrTime = (this.LastCurrTime == -1L) ? System.currentTimeMillis() : this.LastCurrTime;

		if (System.currentTimeMillis() - this.LastCurrTime < this.sleepBetweenImages + this.pauseTime) {
			this.alpha = (this.alpha < 1.0F) ? Math.min(this.alpha + this.showSpeed, 1.0F) : this.alpha;
		} else if (this.alpha <= 0.0F) {

			this.index = (this.index < this.Images.length - 1) ? (this.index + 1) : (this.loop ? 0 : (this.index + 1));
			if (this.index >= this.Images.length) {
				stop();
				return;
			}
			this.LastCurrTime = -1L;
		} else {
			this.alpha = (this.alpha > 0.0F) ? Math.max(this.alpha - this.showSpeed, 0.0F) : this.alpha;
		}
	}

	public boolean isRunning() {
		return this.Running;
	}

	public BufferedImage getCurrentImage() {
		return this.Images[Math.min(this.index, this.Images.length - 1)];
	}

	public int getCurrentIndex() {
		return this.index;
	}

	public long getCurrentTime() {
		return (this.LastCurrTime == -1L) ? 0L : (System.currentTimeMillis() - this.LastCurrTime);
	}

	public float getAlpha() {
		return this.alpha;
	}

	public BufferedImage getDraw() {
		if (this.output == null) {
			return null;
		}
		Graphics2D gl2 = this.output.createGraphics();

		gl2.setColor(this.FadeColor);
		gl2.fillRect(0, 0, this.output.getWidth(), this.output.getHeight());

		gl2.setComposite(AlphaComposite.SrcOver.derive(this.alpha));
		gl2.drawImage(this.Images[Math.min(this.index, this.Images.length - 1)], 0, 0, (ImageObserver) null);
		gl2.dispose();

		return this.output;
	}
}