package com.github.devsnaith.unwholesome.ui;

import com.github.devsnaith.unwholesome.calculation.QLocation;
import com.github.devsnaith.unwholesome.core.canvas.GameState;
import com.github.devsnaith.unwholesome.core.canvas.QCanvas;
import com.github.devsnaith.unwholesome.graphics.effects.QFadeEffect;
import com.github.devsnaith.unwholesome.io.QloadFont;
import com.github.devsnaith.unwholesome.io.QloadImage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class QAuthorsState extends GameState {
	private QLocation centerFade;
	private QLocation centerMsg;
	private QFadeEffect Fade;
	private QFadeEffect msgFade;
	private String msg = "are you sure you wanna to play this game ?";

	private Font msgFont;
	private Font ansFont;
	private String accapt = "yes, let me In";
	private String notAccapt = "No !, I'll pee in my self";

	private boolean waanaPlay = false;

	private boolean request = false;
	private ActionListener Listener;
	private boolean runningAsAdmin = false;
	private int toContinue = 18000000;
	private long LastCurrentTime = -1L;

	private boolean showInfo;

	public QAuthorsState(QCanvas Canvas, boolean showInfo) {
		super(Canvas);

		if (showInfo) {
			this.toContinue = 5000;
		}

		this.showInfo = showInfo;
	}

	public void actionListener(ActionListener Listener) {
		this.Listener = Listener;
	}

	public void onLoad() {
		ArrayList<BufferedImage> Images = new ArrayList<>();
		Images.add(QloadImage.getAllImage("/Intro/ThanksKurzex.png"));
		Images.add(QloadImage.getAllImage("/Intro/ThanksX6ds.png"));

		if (!showInfo) {
			try {
				Process process = Runtime.getRuntime().exec("reg query \"HKU\\S-1-5-19\"");
				if (process.waitFor() == 0) {
					this.runningAsAdmin = true;
				} else {
					Images.add(QloadImage.getAllImage("/Intro/exitCode1.png"));
				}
			} catch (InterruptedException | java.io.IOException e) {
				e.printStackTrace();
			}
		} else {
			this.runningAsAdmin = true;
		}

		BufferedImage[] authorsLogo = Images.<BufferedImage>toArray(new BufferedImage[0]);
		this.Fade = new QFadeEffect(authorsLogo, 1000L, 3000L, 0.05F);
		this.centerFade = new QLocation(this.Fade.getCurrentImage(), QLocation.Locations.CENTER,
				QLocation.Locations.CENTER);

		QloadFont.getFont("/Fonts/Daydream.ttf");
		this.msgFont = QloadFont.getLastFont().deriveFont(0, 12.0F);
		this.ansFont = QloadFont.getLastFont().deriveFont(0, 8.0F);

		@SuppressWarnings("deprecation")
		FontMetrics Metrics = Toolkit.getDefaultToolkit().getFontMetrics(this.msgFont);
		this.centerMsg = new QLocation(Metrics.stringWidth(this.msg), Metrics.getHeight(), QLocation.Locations.CENTER,
				QLocation.Locations.CENTER);

		BufferedImage msgImage = new BufferedImage(this.centerMsg.getWidth(), this.centerMsg.getHeight(), 1);
		Graphics msgImageGraphics = msgImage.getGraphics();

		msgImageGraphics.setFont(this.msgFont);
		msgImageGraphics.setColor(Color.WHITE);

		msgImageGraphics.drawString(this.msg, 0, 10);
		msgImageGraphics.dispose();

		this.msgFade = new QFadeEffect(new BufferedImage[] { msgImage }, 1000L, 2000L, 0.05F);

		getCanvas().getAudio().addAudio("requestYes", "/Audios/requestToPlay.wav");
		getCanvas().getAudio().addAudio("requestNo", "/Audios/requestToClose.wav");
	}

	public void enable() {
		getCanvas().getKeyboard().initializeKey(37);
		getCanvas().getKeyboard().initializeKey(39);
		getCanvas().getKeyboard().initializeKey(10);
		this.Fade.start();
	}

	public void disable() {
		getCanvas().getKeyboard().removeKey(37);
		getCanvas().getKeyboard().removeKey(39);
		getCanvas().getKeyboard().removeKey(10);
	}

	public void update() {
		if (!this.runningAsAdmin && this.Fade.getCurrentIndex() == 2 && this.Fade.getAlpha() == 1.0F) {
			if (this.toContinue >= 0) {
				this.LastCurrentTime = (this.LastCurrentTime == -1L) ? System.currentTimeMillis()
						: this.LastCurrentTime;
				if (System.currentTimeMillis() - this.LastCurrentTime >= 1000L) {
					this.LastCurrentTime += 1000L;
					this.toContinue -= 1000;
				}
				return;
			}
			this.runningAsAdmin = true;
		}

		this.centerFade.initialize(getCanvas().getWidth(), getCanvas().getHeight());
		this.centerMsg.initialize(getCanvas().getWidth(), getCanvas().getHeight());

		this.Fade.update();
		this.msgFade.update();

		if (!this.Fade.isRunning()) {
			if (this.msgFade.getCurrentTime() >= 1000L) {
				this.msgFade.stop();
			} else {
				this.msgFade.start();
			}
		}

		if (this.request) {
			this.msgFade.start();
			if (this.msgFade.getAlpha() <= 0.0F) {
				if (this.waanaPlay) {
					this.Listener.actionPerformed(null);
				} else {
					getCanvas().closeRequest(0, 1000L);
				}
			}
		}

		if (this.Fade.isRunning()) {
			return;
		}

		if (getCanvas().getKeyboard().isPressed(39)) {
			if (!this.waanaPlay) {
				getCanvas().getAudio().stopAudio("requestNo");
				getCanvas().getAudio().stopAudio("requestYes");
				getCanvas().getAudio().playAudio("requestYes");
			}
			this.waanaPlay = true;
		} else if (getCanvas().getKeyboard().isPressed(37)) {
			if (this.waanaPlay) {
				getCanvas().getAudio().stopAudio("requestYes");
				getCanvas().getAudio().stopAudio("requestNo");
				getCanvas().getAudio().playAudio("requestNo");
			}
			this.waanaPlay = false;
		} else if (getCanvas().getKeyboard().isPressed(10)) {
			this.request = true;
		}

		if (this.request) {
			getCanvas().getAudio().stopAudio("requestYes");
			getCanvas().getAudio().stopAudio("requestNo");
		}
	}

	public void render(Graphics gl) {
		gl.setColor(Color.WHITE);

		if (this.showInfo) {
			gl.drawString("showInfo = true, [toContinue = " + (this.toContinue / 1000) + "]", 150, 150);
		}

		gl.drawImage(this.Fade.getDraw(), this.centerFade.getX(), this.centerFade.getY(), null);
		gl.drawImage(this.msgFade.getDraw(), this.centerMsg.getX(), this.centerMsg.getY() - 20, null);

		if (!this.runningAsAdmin && this.Fade.getCurrentIndex() == 2) {

			Graphics2D gl2 = (Graphics2D) gl;
			gl2.setComposite(AlphaComposite.SrcOver.derive(this.Fade.getAlpha()));
			gl2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			String Timer = String.format("%ds", new Object[] { Integer.valueOf(this.toContinue / 1000) });
			gl.drawString(Timer, getCanvas().getWidth() / 2 - gl2.getFontMetrics().stringWidth(Timer) / 2,
					getCanvas().getHeight() - 150);
			return;

		}

		if (this.Fade.isRunning() || this.request) {
			return;
		}

		gl.setFont(this.ansFont);

		if (this.waanaPlay) {
			gl.setColor(Color.GREEN);
		} else {
			gl.setColor(Color.WHITE);
		}

		gl.drawString(this.accapt, getCanvas().getWidth() / 2 - gl.getFontMetrics().stringWidth(this.accapt) / 2 + 100,
				getCanvas().getHeight() / 2 - gl.getFontMetrics().getHeight() / 2 + 20);

		if (!this.waanaPlay) {
			gl.setColor(Color.RED);
		} else {
			gl.setColor(Color.WHITE);
		}

		gl.drawString(this.notAccapt,
				getCanvas().getWidth() / 2 - gl.getFontMetrics().stringWidth(this.notAccapt) / 2 - 100,
				getCanvas().getHeight() / 2 - gl.getFontMetrics().getHeight() / 2 + 20);
	}
}