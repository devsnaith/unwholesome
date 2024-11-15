package com.github.devsnaith.unwholesome.ui.gamestates;

import com.github.devsnaith.unwholesome.core.canvas.GameState;
import com.github.devsnaith.unwholesome.core.canvas.QCanvas;
import com.github.devsnaith.unwholesome.io.QloadFont;
import com.github.devsnaith.unwholesome.io.QloadImage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

public class QTutorialState extends GameState {
	private Font titleFont;
	private Font description;
	private Font ItemTitleFont;
	private Font ItemDescriptionFont;
	private boolean keyUP = false;
	private boolean keyDOWN = false;
	private boolean keyLEFT = false;
	private boolean keyRIGHT = false;
	private boolean keyUpArrow = false;
	private boolean keyDownArrow = false;
	private Image keyUpImage;
	private Image keyDownImage;
	private Image keyLeftImage;
	private Image keyRightImage;
	private Image keyUpArrowImage;
	private Image keyDownArrowImage;
	private Image pointer;
	private Image wallpaper;
	private Image obsIcon;
	private Image slowdown;

//	private String theDate = "2021";
	private String theDate = "2024";

	private byte index = 0;

	private boolean obsRunning = false;
	private int Timer = 90;
	private long LastCurrentTime = -1L;

	public ActionListener Listener;
	private int smoothShowUp = 255;
	private long smoothShowUpCurrentTime = 0L;

	private Image temp;
	private boolean debug;

	public QTutorialState(QCanvas Canvas, boolean debug) {
		super(Canvas);
		this.debug = debug;
	}

	public void onLoad() {
		if (!debug) {
			File wallpaperFolder = new File(
					String.valueOf(System.getenv("AppData")) + "\\Microsoft\\Windows\\Themes\\CachedFiles");
			try {
				byte b; int i;
				File[] arrayOfFile;
				for (i = (arrayOfFile = wallpaperFolder.listFiles()).length, b = 0; b < i;) {
					File wallpaper = arrayOfFile[b];
					try {
						this.temp = ImageIO.read(wallpaper.getAbsoluteFile()).getScaledInstance(17, 11, 4);
						break;
					} catch (Exception exception) {
							this.temp = null;
					}
					b++;
				}
			} catch (NullPointerException error) {
				this.temp = null;
			}
		}

		this.titleFont = QloadFont.getFont("/Fonts/ValentineTime.otf").deriveFont(0, 40.0F);
		this.description = QloadFont.getFont("/Fonts/Roboto-Regular.ttf").deriveFont(0, 14.0F);
		this.ItemDescriptionFont = QloadFont.getLastFont().deriveFont(0, 12.0F);
		this.ItemTitleFont = QloadFont.getLastFont().deriveFont(0, 18.0F);

		this.keyUpImage = QloadImage.getAllImage("/UI/Keys/KeyW.png");
		this.keyDownImage = QloadImage.getAllImage("/UI/Keys/KeyS.png");
		this.keyLeftImage = QloadImage.getAllImage("/UI/Keys/KeyA.png");
		this.keyRightImage = QloadImage.getAllImage("/UI/Keys/KeyD.png");
		this.keyUpArrowImage = QloadImage.getAllImage("/UI/Keys/KeyUP.png");
		this.keyDownArrowImage = QloadImage.getAllImage("/UI/Keys/KeyDOWN.png");

		this.pointer = QloadImage.getAllImage("/UI/Pointer.png");
		this.wallpaper = QloadImage.getAllImage("/UI/wallpaperIcon.png");
		this.slowdown = QloadImage.getAllImage("/UI/slowdown.png");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		this.theDate = dateFormat.format(new Date());

		int y = -1;

		try {
			y = Integer.parseInt(this.theDate);
		} catch (NumberFormatException numberFormatException) {
		}

		y = (y < 2016) ? -1 : y;

		if (y != -1) {
			this.theDate = "I mean who read tutorials in " + y
					+ "\nanyways to interact with item just press [Enter] key.";
		} else {
			this.theDate = "and to interact with item just press [Enter] key.";
		}

		this.obsRunning = !(((Integer) getCanvas().getParameter(1)).intValue() == -1);

		if (this.obsRunning) {
			this.obsIcon = QloadImage.getAllImage("/UI/obsKill.png");
		}
	}

	public void enable() {
		getCanvas().getKeyboard().initializeKey(87);
		getCanvas().getKeyboard().initializeKey(83);
		getCanvas().getKeyboard().initializeKey(65);
		getCanvas().getKeyboard().initializeKey(68);
		getCanvas().getKeyboard().initializeKey(67);
		getCanvas().getKeyboard().initializeKey(38);
		getCanvas().getKeyboard().initializeKey(40);

		this.LastCurrentTime = System.currentTimeMillis();
		this.smoothShowUpCurrentTime = System.currentTimeMillis();
	}

	public void disable() {
		getCanvas().getKeyboard().removeKey(87);
		getCanvas().getKeyboard().removeKey(83);
		getCanvas().getKeyboard().removeKey(65);
		getCanvas().getKeyboard().removeKey(68);
		getCanvas().getKeyboard().removeKey(67);
		getCanvas().getKeyboard().removeKey(38);
		getCanvas().getKeyboard().removeKey(40);
	}

	public void update() {
		if (this.Timer <= 0 || getCanvas().getKeyboard().isPressed(67)) {
			disable();
			this.Listener.actionPerformed(null);
		} else if (System.currentTimeMillis() - this.LastCurrentTime >= 1000L) {
			this.LastCurrentTime += 1000L;
			this.Timer--;
		}

		if (this.smoothShowUp > 0) {
			if (System.currentTimeMillis() - this.smoothShowUpCurrentTime >= 10L) {
				this.smoothShowUpCurrentTime += 10L;
				this.smoothShowUp -= 5;
			}
		}

		if (getCanvas().getKeyboard().isPressed(87))
			this.keyUP = true;
		if (getCanvas().getKeyboard().isPressed(83))
			this.keyDOWN = true;
		if (getCanvas().getKeyboard().isPressed(65))
			this.keyLEFT = true;
		if (getCanvas().getKeyboard().isPressed(68))
			this.keyRIGHT = true;
		if (getCanvas().getKeyboard().isPressed(38)) {
			this.index = (byte) Math.max(this.index - 1, 0);
			this.keyUpArrow = true;
		}
		if (getCanvas().getKeyboard().isPressed(40)) {
			this.index = (byte) Math.min(this.index + 1, 1);
			this.keyDownArrow = true;
		}
	}

	public void render(Graphics gl) {
		Graphics2D gl2 = (Graphics2D) gl;

		int centerX = Math.max(getCanvas().getWidth() / 2, 242);
		int centerY = getCanvas().getHeight() / 2;

		gl2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		gl2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		gl2.setFont(this.titleFont);
		gl2.setColor(Color.WHITE);

		gl2.drawLine(centerX / 2 + 64, centerY / 6 + 32, centerX / 2 + 64, centerY / 6 + 82);
		gl2.drawLine(centerX / 2 - 60 + 260, centerY / 2 - 16 + 70, centerX / 2 - 60 + 260, centerY / 2 + 70 + 150);

		gl2.drawString("Tutorial", 32, 20 + gl2.getFontMetrics().getHeight());

		gl2.setFont(this.description);
		printLines(gl2, centerX / 2 + 78, centerY / 6 + 50,
				"To move you can use [w, s, a and d] keys like 99.99999% games in this world,\nwhat ? you don't like it ! ok use joystick from 1992.");

		printLines(gl2, centerX / 2 + 260 - 60 + 16, centerY / 2 - 16 + 70 + 16,
				"Press [                      ] to swap between items in the shop,\nthere is more items but this is just a tutorial idk if you will\nread something like that "
						+ this.theDate);

		gl2.setFont(this.titleFont);
		gl2.drawString("Shooting the Enemies", centerX / 2 + 260 - 60 + 16, centerY / 2 + 180);

		gl2.setFont(this.description);
		printLines(gl2, centerX / 2 + 260 - 60 + 16, centerY / 2 + 200,
				"In the game, Move your cursor to aim at\nthe emojis, kill them by pressing mouse left.");

		if (this.keyRIGHT) {
			gl2.setColor(Color.GREEN);
		} else {
			gl2.setColor(Color.RED);
		}
		gl2.fillRect(centerX / 3 + 41, centerY / 4 + 3, 16, 16);
		gl2.drawImage(this.keyRightImage, centerX / 3 + 32, centerY / 4 - 4, (ImageObserver) null);

		if (this.keyDOWN) {
			gl2.setColor(Color.GREEN);
		} else {
			gl2.setColor(Color.RED);
		}
		gl2.fillRect(centerX / 3 + 20, centerY / 4 + 16, 16, 16);
		gl2.drawImage(this.keyDownImage, centerX / 3 + 16, centerY / 4 + 12, (ImageObserver) null);

		if (this.keyUP) {
			gl2.setColor(Color.GREEN);
		} else {
			gl2.setColor(Color.RED);
		}
		gl2.fillRect(centerX / 3 + 10, centerY / 4 + 6, 16, 16);
		gl2.drawImage(this.keyUpImage, centerX / 3, centerY / 4, (ImageObserver) null);

		if (this.keyLEFT) {
			gl2.setColor(Color.GREEN);
		} else {
			gl2.setColor(Color.RED);
		}
		gl2.fillRect(centerX / 3 + 7, centerY / 4 + 38, 16, 16);
		gl2.drawImage(this.keyLeftImage, centerX / 3, centerY / 4 + 29, (ImageObserver) null);

		if (this.keyUpArrow) {
			gl2.setColor(Color.GREEN);
		} else {
			gl2.setColor(Color.RED);
		}
		gl2.fillRect(centerX / 2 + 328 - 60, centerY / 2 - 33 + 70 + 16, 14, 14);
		gl2.drawImage(this.keyUpArrowImage, centerX / 2 + 320 - 60, centerY / 2 - 40 + 70 + 16, 30, 30, null);

		if (this.keyDownArrow) {
			gl2.setColor(Color.GREEN);
		} else {
			gl2.setColor(Color.RED);
		}
		gl2.fillRect(centerX / 2 + 362 - 60, centerY / 2 - 33 + 70 + 16, 14, 14);
		gl2.drawImage(this.keyDownArrowImage, centerX / 2 + 354 - 60, centerY / 2 - 40 + 70 + 16, 30, 30, null);

		drawItem(gl2, this.wallpaper, "leave my wallpaper",
				"If you didn't buy this your wallpaper\nwill change to SOMETHING ;)", "$75000", centerX / 2 - 120,
				centerY / 2 + 70, false);
		gl2.drawImage(this.temp, centerX / 2 - 118, centerY / 2 + 70 + 4, (ImageObserver) null);

		if (this.obsRunning) {
			drawItem(gl2, this.obsIcon, "leave my obs", "Look man, if u didn't buy this\nYOUR obs will be killed !! ;)",
					"$95000", centerX / 2 - 118, centerY / 2 + 140, true);
		} else {
			drawItem(gl2, this.slowdown, "Slowness",
					"Buy this item to make the Emojis slower\nThe max speed right now is.... who knows? ;)", "$54200",
					centerX / 2 - 120, centerY / 2 + 140, true);
		}

		gl2.setColor(Color.WHITE);
		gl2.setFont(this.titleFont);
		gl2.drawString("Shooting Cooldown", 
				centerX / 2 - 118, centerY / 2 + 240);
		
		gl2.setFont(this.description);
		printLines(gl2, centerX / 2 - 118, centerY / 2 + 260,
				"When shooting, always keep an eye on the cooldown progress bar, the more you shoot, "
				+ "\nthe slower the speed becomes, stop shooting to restore your shooting speed.\n\n"
				+ "TIP: When no bullets appear on the screen, move your player to speed up the cooldown.");
		int pointerX = centerX / 2 - 40 - 120;
		int pointerY = centerY / 2 + 70 * (this.index + 1);

		gl2.setFont(this.ItemDescriptionFont);
		String nextButton = String.format("[%ds] Press 'C' to Start ->", new Object[] { Integer.valueOf(this.Timer) });
		int buttonWidth = gl2.getFontMetrics().stringWidth(nextButton);

		gl2.drawImage(this.pointer, pointerX, pointerY, (ImageObserver) null);
		gl.setColor(new Color(0, 0, 0, this.smoothShowUp));
		gl.fillRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight());

		gl2.setColor(Color.CYAN);
		gl2.drawString(nextButton, getCanvas().getWidth() - buttonWidth - 32,
				getCanvas().getDefaultHeight() - gl2.getFontMetrics().getHeight() - 10);
	}

	public void drawItem(Graphics2D gl2, Image ItemImage, String ItemName, String ItemDes, String money, int xPos,
			int yPos, boolean dark) {
		if (dark) {
			gl2.setComposite(AlphaComposite.SrcOver.derive(0.5F));
		}
		gl2.setColor(Color.WHITE);
		gl2.setFont(this.ItemTitleFont);
		gl2.drawImage(ItemImage, xPos, yPos, (ImageObserver) null);

		gl2.drawString(ItemName, xPos + 32 + 8, yPos + 16);
		gl2.drawString(money, xPos + 32 + 182, yPos + 16);

		gl2.setFont(this.ItemDescriptionFont);
		gl2.drawLine(xPos + 32 + 8, yPos + 20, xPos + 32 + 182, yPos + 20);
		printLines(gl2, xPos + 32 + 8, yPos + 34, ItemDes);
		gl2.setComposite(AlphaComposite.SrcOver.derive(1.0F));
	}

	public void actionListener(ActionListener Listener) {
		this.Listener = Listener;
	}

	public void printLines(Graphics2D gl, int x, int y, String lines) {
		int fontHeight = 0;
		byte b;
		int i;
		String[] arrayOfString;
		for (i = (arrayOfString = lines.split("\n")).length, b = 0; b < i;) {
			String line = arrayOfString[b];
			gl.drawString(line, x, y + fontHeight);
			fontHeight += gl.getFontMetrics().getHeight();
			b++;
		}
	}

}