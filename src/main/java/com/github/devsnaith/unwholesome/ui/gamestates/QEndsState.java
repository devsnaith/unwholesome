package com.github.devsnaith.unwholesome.ui.gamestates;

import com.github.devsnaith.unwholesome.Unwholesome;
import com.github.devsnaith.unwholesome.calculation.QLocation;
import com.github.devsnaith.unwholesome.core.canvas.GameState;
import com.github.devsnaith.unwholesome.core.canvas.QCanvas;
import com.github.devsnaith.unwholesome.io.QloadFont;
import com.github.devsnaith.unwholesome.io.QloadImage;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class QEndsState extends GameState {
	private Font defaultFont;
	public boolean isWiner = true;
	private boolean isDone = true;
	private BufferedImage Image;
	private Image loselaughImage;
	private QLocation imagePosition;
	private String winMsg = "I'm just fine, mann u win";

	private String loseMsg = "He he boyy, It's my turn";
	private String loseDescriptionMsg = "Please wait, we are preparing your computer";

	private long lastLoseCurrentTime;
	private String closeMsg = "bruh you know just press 'Enter' to ..... close the game and umm uh congrats";

	private Robot robot;

	public QEndsState(QCanvas Canvas) {
		super(Canvas);
	}

	public void onLoad() {
		this.defaultFont = QloadFont.getFont("/Fonts/VCR_OSD_MONO_1.001.ttf").deriveFont(0, 12.0F);
		this.loselaughImage = QloadImage.getAllImage("/end/loseLaughImage.png").getScaledInstance(16, 20, 4);
		getCanvas().getAudio().addAudio("endsSound", "/Audios/x6ds_damage/x6ds_Ends.wav");

		getCanvas().getAudio().playAudio("endsSound");
		getCanvas().getAudio().stopAudio("endsSound");
	}

	public void enable() {
		getCanvas().getAudio().stopAudio("endsSound");
		getCanvas().getAudio().playAudio("endsSound");

		if (this.isWiner) {
			this.Image = QloadImage.convertToBufferedImageObject(
					QloadImage.getAllImage("/end/sadEnds.jpg").getScaledInstance(180, 180, 4), 3);
			getCanvas().getKeyboard().initializeKey(10);
		} else {
			this.Image = QloadImage.convertToBufferedImageObject(
					QloadImage.getAllImage("/end/goodEnds.png").getScaledInstance(180, 180, 4), 3);
			this.closeMsg = "Hmmm Ok you can press 'Enter' to close the game";
			this.isDone = false;
		}

		this.imagePosition = new QLocation(this.Image, QLocation.Locations.CENTER, QLocation.Locations.CENTER);
		this.lastLoseCurrentTime = System.currentTimeMillis();

		if (this.robot == null) {
			try {
				this.robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}

		this.imagePosition.initialize(getCanvas().getDefaultWidth(), getCanvas().getDefaultHeight());
	}

	public void disable() {
		getCanvas().getKeyboard().removeKey(10);
	}

	public void update() {
		this.imagePosition.initialize(getCanvas().getDefaultWidth(), getCanvas().getDefaultHeight());
		if (getCanvas().getKeyboard().isPressed(10)) {
			getCanvas().closeRequest(1, 0L);
		}

		if (System.currentTimeMillis() - this.lastLoseCurrentTime >= 1000L) {
			this.lastLoseCurrentTime += 1000L;
			if (!this.loseDescriptionMsg.endsWith("...")) {
				this.loseDescriptionMsg = String.valueOf(this.loseDescriptionMsg) + ".";
			} else {
				this.loseDescriptionMsg = this.loseDescriptionMsg.substring(0, this.loseDescriptionMsg.length() - 3);
			}
		}
	}

	public void startV(final boolean isDesktopWallpaperEnabled, final boolean isDesktopCringeEnabled,
			final boolean isRestartEnabled, final boolean isCloseObsEnabled) {
		(new Thread(new Runnable() {
			public void run() {
				Unwholesome.gameEnds(isDesktopWallpaperEnabled, isDesktopCringeEnabled, isRestartEnabled, isCloseObsEnabled);
				QEndsState.this.getCanvas().getKeyboard().initializeKey(10);
				QEndsState.this.isDone = true;
			}
		})).start();
	}

	public void render(Graphics gl) {
		Graphics2D gl2 = (Graphics2D) gl;
		gl2.setFont(this.defaultFont);
		gl2.drawImage(this.Image, this.imagePosition.getX(), this.imagePosition.getY(), (ImageObserver) null);

		if (this.isWiner) {
			int defaultWidthSize = gl2.getFontMetrics().stringWidth(this.winMsg);
			gl2.setColor(Color.GREEN);
			gl2.drawString(this.winMsg, getCanvas().getWidth() / 2 - defaultWidthSize / 2,
					getCanvas().getHeight() / 2 - gl2.getFontMetrics().getHeight() / 2 - 90);
		} else {
			int defaultWidthSize = gl2.getFontMetrics().stringWidth(this.loseMsg);
			gl2.setColor(Color.RED);
			gl2.drawString(this.loseMsg, getCanvas().getWidth() / 2 - defaultWidthSize / 2,
					getCanvas().getHeight() / 2 - gl2.getFontMetrics().getHeight() / 2 - 90);

			if (!this.isDone) {
				gl2.drawString(this.loseDescriptionMsg,
						getCanvas().getWidth() / 2 - gl2.getFontMetrics().stringWidth(this.loseDescriptionMsg) / 2 - 8,
						getCanvas().getHeight() - 80);
				gl2.drawImage(this.loselaughImage,
						getCanvas().getWidth() / 2 + gl2.getFontMetrics().stringWidth(this.loseDescriptionMsg) / 2,
						getCanvas().getHeight() - 96, (ImageObserver) null);
			}
		}
		if (this.isDone) {
			gl2.drawString(this.closeMsg,
					getCanvas().getWidth() / 2 - gl2.getFontMetrics().stringWidth(this.closeMsg) / 2,
					getCanvas().getHeight() - 80);
		}
	}
}