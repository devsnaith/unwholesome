package com.github.devsnaith.unwholesome.ui.gamestates;

import com.github.devsnaith.unwholesome.core.canvas.GameState;
import com.github.devsnaith.unwholesome.core.canvas.QCanvas;
import com.github.devsnaith.unwholesome.core.canvas.QStateManger;
import com.github.devsnaith.unwholesome.io.QloadFont;
import com.github.devsnaith.unwholesome.io.QloadImage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.ImageObserver;
import java.io.File;
import javax.imageio.ImageIO;

public class QShopState extends GameState {
	private Font titleFont;
	private Font itemsFont;
	private Font itemsDesFont;
	private Color backgroundColor;
	private Image pointer;
	private Image umbrellaImage;
	private Image heart;
	private Image restart;
	private Image obsIcon;
	private Image slowdown;
	private Image Files;
	private Image wallpaper;
	private Image currentPcWallpaper;
	private int index = 0;
	private boolean obsRunning = false;
	private QGameState GameState;
	private boolean debug;

	public QShopState(QCanvas Canvas, boolean debug) {
		super(Canvas);
		this.debug = debug;
	}

	public void onLoad() {
		if (!debug) {
			File wallpaperFolder = new File(
					String.valueOf(System.getenv("AppData")) + "\\Microsoft\\Windows\\Themes\\CachedFiles");
			try {
				byte b;
				int i;
				File[] arrayOfFile;
				for (i = (arrayOfFile = wallpaperFolder.listFiles()).length, b = 0; b < i;) {
					File wallpaper = arrayOfFile[b];
					try {
						this.currentPcWallpaper = ImageIO.read(wallpaper.getAbsoluteFile()).getScaledInstance(17, 11,
								4);
						break;
					} catch (Exception exception) {
						this.currentPcWallpaper = null;
					}
					b++;
				}
			} catch (NullPointerException error) {
				this.currentPcWallpaper = null;
			}
		}

		this.backgroundColor = new Color(0.0F, 0.0F, 0.0F, 0.5F);
		this.GameState = (QGameState) ((QStateManger) getCanvas().getParameter(0)).getStateObject("players&objects");
		this.obsRunning = (((Integer) getCanvas().getParameter(1)).intValue() != -1);

		this.titleFont = QloadFont.getFont("/Fonts/ValentineTime.otf").deriveFont(0, 90.0F);
		this.itemsFont = QloadFont.getFont("/Fonts/Roboto-Regular.ttf").deriveFont(0, 18.0F);
		this.itemsDesFont = QloadFont.getLastFont().deriveFont(0, 12.0F);

		if (this.obsRunning) {
			this.obsIcon = QloadImage.getAllImage("/UI/obsKill.png");
		} else {
			this.GameState.isCloseObsEnabled = false;
		}

		this.pointer = QloadImage.getAllImage("/UI/Pointer.png");
		this.heart = QloadImage.getAllImage("/UI/Heart.png");
		this.umbrellaImage = QloadImage.getAllImage("/UI/umbrella.png");
		this.slowdown = QloadImage.getAllImage("/UI/slowdown.png");
		this.wallpaper = QloadImage.getAllImage("/UI/wallpaperIcon.png");
		this.Files = QloadImage.getAllImage("/UI/FilesIcon.png");
		this.restart = QloadImage.getAllImage("/UI/Restart.png");
	}

	public void enable() {
		getCanvas().getKeyboard().initializeKey(38);
		getCanvas().getKeyboard().initializeKey(40);
		getCanvas().getKeyboard().initializeKey(10);
	}

	public void disable() {
		getCanvas().getKeyboard().removeKey(38);
		getCanvas().getKeyboard().removeKey(40);
		getCanvas().getKeyboard().removeKey(10);
	}

	public void update() {
		if (getCanvas().getKeyboard().isPressed(10)) {
			getCanvas().getKeyboard().resetKey(10);
			if (this.index == 0 && this.GameState.money > 12000) {
				this.GameState.heart++;
				this.GameState.money -= 12000;
			} else if (this.index == 1 && this.GameState.money > 50000 && this.GameState.umbrellaCurrentTime <= 0) {
				this.GameState.showUmbrella();
				this.GameState.money -= 50000;
			} else if (this.index == 2 && this.GameState.money > 54200 && this.GameState.wholesomeTargetedSpeed > 5) {
				this.GameState.wholesomeTargetedSpeed--;
				this.GameState.money -= 54200;
			} else if (this.index == 3 && this.GameState.money > 75000 && this.GameState.isDesktopWallpaperEnabled) {
				this.GameState.isDesktopWallpaperEnabled = false;
				this.GameState.money -= 75000;
			} else if (this.index == 4 && this.GameState.money > 90000 && this.GameState.isDesktopCringeEnabled) {
				this.GameState.isDesktopCringeEnabled = false;
				this.GameState.money -= 90000;
			} else if (this.index == 5 && this.GameState.money > 95000 && this.GameState.isCloseObsEnabled) {
				this.GameState.isCloseObsEnabled = false;
				this.GameState.money -= 95000;
			} else if (this.index == 6 && this.GameState.money > 100000 && this.GameState.isRestartEnabled) {
				this.GameState.isRestartEnabled = false;
				this.GameState.money -= 100000;
			}
		}

		if (getCanvas().getKeyboard().isPressed(38)) {
			getCanvas().getKeyboard().resetKey(38);
			this.index = (this.index > 0) ? (this.index - 1) : this.index;

			if (this.index == 5 && !this.obsRunning) {
				this.index = 4;
			}
		} else if (getCanvas().getKeyboard().isPressed(40)) {
			getCanvas().getKeyboard().resetKey(40);
			this.index = (this.index < 6) ? (this.index + 1) : this.index;
			if (this.index == 5 && !this.obsRunning) {
				this.index = 6;
			}
		}
	}

	public void render(Graphics gl) {
		Graphics2D gl2 = (Graphics2D) gl;
		gl2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		gl2.setColor(this.backgroundColor);
		gl2.fillRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight());

		gl2.setFont(this.titleFont);
		String Title = "The Shop";

		gl2.setColor(Color.WHITE);
		gl2.drawString(Title, getCanvas().getWidth() / 2 - gl.getFontMetrics().stringWidth(Title) / 2, 120);
		gl2.drawLine(32, 128, getCanvas().getWidth() - 32, 128);

		int xPos = getCanvas().getWidth() / 2 - 280;
		int yPos = 152;

		int pointerX = getCanvas().getWidth() / 2;
		int pointerY = 80 * this.index + 152;

		if (this.index < 4) {
			pointerX -= 316;
		} else {
			pointerX += 14;
			pointerY -= 320;
		}

		drawItem(gl2, this.heart, "Heart", "I guess you need this item ;)", "$1200", xPos, yPos + 0, false);
		drawItem(gl2, this.umbrellaImage, "Umbrella",
				"Unfortunately this item will act as a shield\nExpire time: 30 seconds.", "$50000", xPos, yPos + 80,
				!(this.GameState.umbrellaCurrentTime <= 0));

		drawItem(gl2, this.slowdown, "Slowness",
				"Buy this item to make the Emojis slower\nThe max speed right now is "
						+ String.valueOf(this.GameState.wholesomeTargetedSpeed) + " ;)",
				"$54200", xPos, yPos + 160, !(this.GameState.wholesomeTargetedSpeed > 5));

		drawItem(gl2, this.wallpaper, "Leave my wallpaper",
				"Don't buy this if you want me to pick\na good wallpaper for you.", "$75000", xPos, yPos + 240,
				!this.GameState.isDesktopWallpaperEnabled);
		gl2.drawImage(this.currentPcWallpaper, xPos + 2, yPos + 240 + 4, (ImageObserver) null);

		xPos = getCanvas().getWidth() / 2 + 50;

		drawItem(gl2, this.Files, "Don't Make Cringe!",
				"I'll add some gifts in your desktop\nBuy this to disable the feature ;)", "$90000", xPos, yPos + 0,
				!this.GameState.isDesktopCringeEnabled);

		if (this.obsRunning) {
			drawItem(gl2, this.obsIcon, "leave my obs", "Look man, if you didn't buy this\nYOUR obs will get killed !! ;)",
					"$95000", xPos, yPos + 80, !this.GameState.isCloseObsEnabled);
		}

		if (this.index == 6 && !this.obsRunning) {
			pointerY -= 80;
		}

		drawItem(gl2, this.restart, "Don't Restart pc", "Buy me or your unsaved work\nwill Vanish HAHAHA!", "$100000",
				xPos, yPos + 80 * (this.obsRunning ? 2 : 1), !this.GameState.isRestartEnabled);

		gl2.drawImage(this.pointer, pointerX, pointerY, (ImageObserver) null);
	}

	public void drawItem(Graphics2D gl2, Image ItemImage, String ItemName, String ItemDes, String money, int xPos,
			int yPos, boolean dark) {
		if (dark) {
			gl2.setComposite(AlphaComposite.SrcOver.derive(0.5F));
		}
		gl2.setColor(Color.WHITE);
		gl2.setFont(this.itemsFont);
		gl2.drawImage(ItemImage, xPos, yPos, (ImageObserver) null);

		gl2.drawString(ItemName, xPos + 32 + 8, yPos + 16);
		gl2.drawString(money, xPos + 32 + 182, yPos + 16);

		gl2.setFont(this.itemsDesFont);
		gl2.drawLine(xPos + 32 + 8, yPos + 20, xPos + 32 + 182, yPos + 20);
		printLines(gl2, xPos + 32 + 8, yPos + 34, ItemDes);
		gl2.setComposite(AlphaComposite.SrcOver.derive(1.0F));
	}

	public int printLines(Graphics2D gl2, int x, int y, String text) {
		int newLineSize = 0;
		byte b;
		int i;
		String[] arrayOfString;
		for (i = (arrayOfString = text.split("\n")).length, b = 0; b < i;) {
			String line = arrayOfString[b];

			gl2.drawString(line, x, y + newLineSize);
			newLineSize += gl2.getFontMetrics().getHeight();

			b++;
		}

		return newLineSize + gl2.getFontMetrics().getHeight() + 9;
	}
}