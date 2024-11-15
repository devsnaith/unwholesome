package com.github.devsnaith.unwholesome.ui.gamestates;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.github.devsnaith.unwholesome.calculation.QDirections;
import com.github.devsnaith.unwholesome.calculation.QLocation;
import com.github.devsnaith.unwholesome.core.QObject;
import com.github.devsnaith.unwholesome.core.QObject.sensor;
import com.github.devsnaith.unwholesome.core.canvas.GameState;
import com.github.devsnaith.unwholesome.core.canvas.QCanvas;
import com.github.devsnaith.unwholesome.core.canvas.QStateManger;
import com.github.devsnaith.unwholesome.graphics.QTileset;
import com.github.devsnaith.unwholesome.graphics.drawers.QAnimation;
import com.github.devsnaith.unwholesome.io.QloadFont;
import com.github.devsnaith.unwholesome.io.QloadImage;

public class QGameState extends GameState implements MouseListener, MouseMotionListener {
	
	private int playerSpeed = 8;
	private QObject playerObject;
	private Image[] playerImage;
	private QDirections lastDirection;
	
	private boolean isPlayerNextToKurzex = false;
	private boolean haveUmbrella = false;
	
	private QObject kurzexObject;
	private QAnimation kurzexAnimation;
	private QLocation kurzexLocation;

	private Image umbrellaImage;
	private QObject umbrellaObject;
	private int umbrellaSmooth = 0;
	public int umbrellaCurrentTime = 0;
	private long umbrellaCurrentTimerMS = -1L;

	private Image heartIcon;
	private int shootCoolDown = 10;
	private int maxShootSpeed = 1000;
	private int minShootSpeed = 80;
	private long shootAfter = 0;
	private long lastShootTimer = 0;

	private boolean mousePressed = false;
	private Point mousePoint = new Point(0, 0);
	
	private Image moneyIcon;
	public int heart = 10;

	private int moneyAddedWhenWholesomeKilled = 600;
	public int money = 5240;

	public boolean isRestartEnabled = true;
	public boolean isDesktopWallpaperEnabled = true;
	public boolean isDesktopCringeEnabled = true;
	public boolean isCloseObsEnabled = false;

	private QAnimation trapAnimation;
	private boolean trapRunning = false;
	private boolean wasOnTrap = false;
	private QObject trapObject;
	private int trapTimer = 3000;
	private int trapAround = 10000;
	private int lastPlayerX = 0, lastPlayerY = 0;

	private List<QObject> wholesomeImagesToRemove = new ArrayList<QObject>();
	private QAnimation wholesomeImagesAnimation;
	private ArrayList<QObject> wholesomeImages;
	
	public int wholesomeTargetedSpeed = 17;
	private float wholesomeSpeedToAdd = 0.1f;
	private float wholesomeSpeed = 3f;

	private int wholesomeAmount = 10;
	private int currentwholesomes = -1;
	private Image[] airEffect;

	private ArrayList<AirEffectObject> airEffectObjects;
	private long lastCurrentTimer = -1L;
	private Font timerFont;
	private Font ItemsFont;
	private byte startTimer = 3;

	private Font infoFont;
	private ArrayList<Bullet> bulletObjects;

	private class Bullet extends QObject {
		private int dest_X = 0;
		private int dest_Y = 0;
		private double doubleXPos = 0;
		private double doubleYPos = 0;

		public Bullet(int x, int y, int width, int height) {
			super(x, y, width, height);
			this.dest_X = mousePoint.x;
			this.dest_Y = 0;
			this.doubleXPos = x + (playerObject.getWidth() / 2) - 1;
			this.doubleYPos = y;
		}

		public void update() {
			double angle = Math.atan2(dest_Y - this.getMustY(), dest_X - this.getMustX());
			doubleXPos += Math.cos(angle) * 4;
			doubleYPos += Math.sin(angle) * 4;
			this.setLocation((int) doubleXPos, (int) doubleYPos);
		}

		public void render(Graphics gl) {
			gl.setColor(Color.WHITE);
			gl.fillRect(this.getMustX(), this.getMustY(), this.getWidth(), this.getHeight());
		}
	}

	private class AirEffectObject {
		private int x = 0;
		private int y = 0;

		private boolean effectEnd = false;
		private int index = 0;
		private Image[] effectAnimation;

		public AirEffectObject(Image[] effectAnimation, int x, int y) {
			this.effectAnimation = effectAnimation;
			this.x = x;
			this.y = y;
		}

		public void update() {
			if (this.index < this.effectAnimation.length - 1) {
				this.index++;
			} else {
				this.effectEnd = true;
			}
		}

		public void render(Graphics gl) {
			if (this.effectEnd) {
				return;
			}
			gl.drawImage(this.effectAnimation[this.index], this.x, this.y, null);
		}

		public boolean effectEnds() {
			return this.effectEnd;
		}
	}

	String[] damageSounds = new String[] { "damage0", "damage1", "damage2", "damage3" };
	private long smoothShowUpCurrentTime = 0L;
	private int smoothShowUp = 255;

	private boolean showInfo = false;
	private boolean wholesome_boolean = true;
	private boolean kurzex_boolean = true;

	private long moneyBagTimeMills = 0;
	private int minMoneyBagAmount = 100;
	private int maxMoneyBagAmount = 10000;
	private int moneyBagAmount;
	private QObject moneyBag;

	private boolean moneyBagIsRunning = true;
	private int moneyBagTimer = 10000;

	public QGameState(QCanvas canvas, boolean showInfo) {
		super(canvas);
		
		if (showInfo || ((Integer) canvas.getParameter(1)).intValue() != -1) {
			this.isCloseObsEnabled = true;
		}

		if (showInfo) {
			JFrame debugTools = new JFrame();

			debugTools.setDefaultCloseOperation(0);
			debugTools.setIconImage(canvas.getIconImage());

			debugTools.setSize(new Dimension(250, 300));
			debugTools.setLocationRelativeTo((Component) null);

			debugTools.setLocation(canvas.getMonitorWidth() - 220, debugTools.getY());
			debugTools.setUndecorated(true);

			JPanel contentPanel = new JPanel();
			JScrollPane scrollBar = new JScrollPane(contentPanel); 

			contentPanel.setBackground(Color.GRAY);
			contentPanel.setLayout(new GridLayout(-1, 1));
			contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 8, false));

			JLabel Label = new JLabel("GAME_TOOLS");
			Label.setForeground(Color.white);
			contentPanel.add(Label);

			JButton exit = new JButton();
			exit.setText("Send, exit request");
			exit.setBackground(Color.BLACK);
			exit.setForeground(Color.WHITE);
			exit.setFocusable(false);
			exit.addActionListener(e -> System.exit(0));
			contentPanel.add(exit);

			JButton resources = new JButton();
			resources.setText("Set player resources");
			resources.setBackground(Color.BLACK);
			resources.setForeground(Color.WHITE);
			resources.setFocusable(false);
			resources.addActionListener(e -> {
				String returnedMsg = null;
				String Selection = (String) JOptionPane.showInputDialog(debugTools, "", "set player resources", 3,
						canvas.getIcon(), (Object[]) new String[] { "HEARTS", "MONEY" }, Integer.valueOf(0));

				if (Selection == null) {
					return;
				}

				try {
					int amount = Integer.parseInt((String) JOptionPane.showInputDialog(debugTools,
							String.valueOf(Selection) + "_Amount", "set Amount", 3, canvas.getIcon(), null, null));
					if (amount == -1) {
						return;
					}

					if (Selection.equals("HEARTS")) {
						this.heart = amount;
					} else if (Selection.equals("MONEY")) {
						this.money = amount;
					}

				} catch (NumberFormatException error) {
					returnedMsg = "Enter a Number,\r\nMax Amount is 2147483647";
				}

				if (returnedMsg != null) {
					JOptionPane.showMessageDialog(debugTools, returnedMsg, "Returned_Msg", 0, canvas.getIcon());
				}
			});

			contentPanel.add(resources);

			JButton stopTheWholesome = new JButton();
			stopTheWholesome.setText("wholesome_" + wholesome_boolean);
			stopTheWholesome.setBackground(Color.BLACK);
			stopTheWholesome.setForeground(Color.WHITE);
			stopTheWholesome.setFocusable(false);

			stopTheWholesome.addActionListener(e -> {
				this.wholesome_boolean = !this.wholesome_boolean;
				stopTheWholesome.setText("Wholesomes_" + this.wholesome_boolean);
			});

			contentPanel.add(stopTheWholesome);

			JButton stopKurzex = new JButton();
			stopKurzex.setText("Kurzex_" + kurzex_boolean);
			stopKurzex.setBackground(Color.BLACK);
			stopKurzex.setForeground(Color.WHITE);
			stopKurzex.setFocusable(false);

			stopKurzex.addActionListener(e -> {
				this.kurzex_boolean = !this.kurzex_boolean;
				stopKurzex.setText("Kurzex_" + this.kurzex_boolean);
			});

			contentPanel.add(stopKurzex);

			JButton trap = new JButton();
			trap.setText("Request Trap");
			trap.setBackground(Color.BLACK);
			trap.setForeground(Color.WHITE);
			trap.setFocusable(false);

			trap.addActionListener(e -> {
				this.wasOnTrap = false;
				this.trapRunning = false;
				this.trapTimer = 500;
				spwanTrap();
			});

			contentPanel.add(trap);

			JButton trapRunning = new JButton();
			trapRunning.setText("Trap_" + this.moneyBagIsRunning);
			trapRunning.setBackground(Color.BLACK);
			trapRunning.setForeground(Color.WHITE);
			trapRunning.setFocusable(false);

			trapRunning.addActionListener(e -> {
				this.trapRunning = !this.trapRunning;
				trapRunning.setText("Trap_" + this.trapRunning);
			});

			contentPanel.add(trapRunning);

			JButton money = new JButton();
			money.setText("Request MoneyBag");
			money.setBackground(Color.BLACK);
			money.setForeground(Color.WHITE);
			money.setFocusable(false);

			money.addActionListener(e -> {
				spwanMoneyBag();
			});

			contentPanel.add(money);

			JButton moneyRunning = new JButton();
			moneyRunning.setText("MoneyBag_" + this.moneyBagIsRunning);
			moneyRunning.setBackground(Color.BLACK);
			moneyRunning.setForeground(Color.WHITE);
			moneyRunning.setFocusable(false);

			moneyRunning.addActionListener(e -> {
				this.moneyBagIsRunning = !this.moneyBagIsRunning;
				moneyRunning.setText("MoneyBag_" + this.moneyBagIsRunning);
			});

			contentPanel.add(moneyRunning);

			JButton _showInfo = new JButton();
			_showInfo.setText("Toggle Info");
			_showInfo.setBackground(Color.BLACK);
			_showInfo.setForeground(Color.WHITE);
			_showInfo.setFocusable(false);

			_showInfo.addActionListener(e -> {
				this.showInfo = !this.showInfo;
			});

			contentPanel.add(_showInfo);

			debugTools.add(scrollBar);
			debugTools.setAlwaysOnTop(true);
			debugTools.setVisible(true);
		}
		this.showInfo = showInfo;
	}

	public void onLoad() {

		this.getCanvas().getCanvas().addMouseListener(this);
		this.getCanvas().getCanvas().addMouseMotionListener(this);

		this.ItemsFont = QloadFont.getFont("/Fonts/VCR_OSD_MONO_1.001.ttf").deriveFont(Font.PLAIN, 18.0F);
		this.infoFont = QloadFont.getFont("/Fonts/Roboto-Light.ttf").deriveFont(Font.PLAIN, 12.0F);
		this.timerFont = QloadFont.getFont("/Fonts/DS-DIGIT.TTF").deriveFont(Font.BOLD, 82.0F);

		this.moneyIcon = QloadImage.getAllImage("/UI/Money.png");
		this.heartIcon = QloadImage.getAllImage("/UI/Heart.png");
		this.umbrellaImage = QloadImage.getAllImage("/UI/umbrella.png");

		QAnimation airEffect = new QAnimation();
		this.airEffectObjects = new ArrayList<>();

		QTileset airImage = new QTileset(201, 201, QloadImage.getAllImage("/UI/Effects/air-blast.png"));
		for (int i = 0; i < airImage.getSize(); i++) {
			airEffect.loadImage(airImage.getDraw(i).getScaledInstance(40, 40, 4));
		}

		this.airEffect = airEffect.toArrayList();
		this.bulletObjects = new ArrayList<>();

		getCanvas().getAudio().addAudio("pop", "/Audios/pop.wav");
		getCanvas().getAudio().addAudio("shoot", "/Audios/shoot.wav");
		getCanvas().getAudio().addAudio("walk", "/Audios/walking.wav");

		getCanvas().getAudio().addAudio(this.damageSounds[0], "/Audios/x6ds_damage/damage0.wav");
		getCanvas().getAudio().addAudio(this.damageSounds[1], "/Audios/x6ds_damage/damage1.wav");
		getCanvas().getAudio().addAudio(this.damageSounds[2], "/Audios/x6ds_damage/damage2.wav");
		getCanvas().getAudio().addAudio(this.damageSounds[3], "/Audios/x6ds_damage/damage3.wav");

		this.playerImage = new Image[] { QloadImage.getAllImage("/player/left.png").getScaledInstance(32, 32, 4),
				QloadImage.getAllImage("/player/leftIdle.png").getScaledInstance(32, 32, 4),
				QloadImage.getAllImage("/player/right.png").getScaledInstance(32, 32, 4),
				QloadImage.getAllImage("/player/rightIdle.png").getScaledInstance(32, 32, 4) };

		QLocation playerLocation = new QLocation(32, 32, QLocation.Locations.CENTER, QLocation.Locations.BOTTOM);
		playerLocation.initialize(getCanvas().getWidth(), getCanvas().getHeight());
		this.playerObject = new QObject(playerLocation.getX(), playerLocation.getY(), 32, 32);
		this.playerObject.setKeyboard(getCanvas().getKeyboard());
		this.playerObject.setSpeed(playerSpeed);
		this.playerObject.acceleration(true, 1f);

		this.playerObject.setSensor(new QObject.sensor() {
			public QDirections moveing(QDirections direction) {
				if (direction.isDirection(QDirections.Directions.WEST)) {
					QGameState.this.playerObject.setImage(QGameState.this.playerImage[0]);
				} else if (direction.isDirection(QDirections.Directions.EAST)) {
					QGameState.this.playerObject.setImage(QGameState.this.playerImage[2]);
				} else if (QGameState.this.lastDirection == null
						|| QGameState.this.lastDirection.isDirection(QDirections.Directions.WEST)) {
					QGameState.this.lastDirection = (QGameState.this.lastDirection != null)
							? QGameState.this.lastDirection
							: new QDirections();
					QGameState.this.playerObject.setImage(QGameState.this.playerImage[1]);
				} else if (QGameState.this.lastDirection.isDirection(QDirections.Directions.EAST)) {
					QGameState.this.playerObject.setImage(QGameState.this.playerImage[3]);
				}
				QGameState.this.lastDirection.copy(direction);
				if (QGameState.this.playerObject.getMustX() <= 0) {
					direction.removeDirection(QDirections.Directions.WEST);
				} else if (QGameState.this.playerObject.getMustX() >= QGameState.this.getCanvas().getWidth()
						- QGameState.this.playerObject.getWidth()) {
					direction.removeDirection(QDirections.Directions.EAST);
				}
				if (QGameState.this.playerObject.getMustY() >= QGameState.this.getCanvas().getHeight()
						- QGameState.this.playerObject.getHeight()) {
					direction.removeDirection(QDirections.Directions.SOUTH);
				} else if (QGameState.this.playerObject.getMustY() <= QGameState.this.getCanvas().getHeight()
						- 120) {
					direction.removeDirection(QDirections.Directions.NORTH);
				}
				return direction;
			}

			public void touched(QObject Object0, QObject Object1) {
			}

			public void notTouched(QObject Object0) {
			}
		}, new QObject[0]);

		this.trapAnimation = new QAnimation();
		this.trapAnimation.loadImage(QloadImage.getAllImage("/UI/trap/trap0.png"));
		this.trapAnimation.loadImage(QloadImage.getAllImage("/UI/trap/trap1.png"));
		this.trapAnimation.loadImage(QloadImage.getAllImage("/UI/trap/trap2.png"));
		this.trapAnimation.loadImage(QloadImage.getAllImage("/UI/trap/trap3.png"));
		this.trapAnimation.loop(false);
		this.trapAnimation.sleep(100L);

		this.trapObject = new QObject(0, 0, 32, 32);
		this.trapObject.setSensor(new QObject.sensor() {
			public void touched(QObject selfObject, QObject playerObject) {
				if (QGameState.this.trapRunning && !QGameState.this.wasOnTrap) {
					QGameState.this.wasOnTrap = true;
					QGameState.this.heart--;
					playOuch();
				}
			}

			public void notTouched(QObject arg0) {
			}

			public QDirections moveing(QDirections arg0) {
				return null;
			}
		}, new QObject[] { this.playerObject });

		this.kurzexAnimation = new QAnimation();
		int j;
		for (j = 0; j <= 20; j++) {
			this.kurzexAnimation.loadImage(QloadImage
					.getAllImage(String.format("/kurzex_draw/vendor/Kurzex%s.png", new Object[] { Integer.valueOf(j) })));
		}

		this.kurzexAnimation.sleep(80L);
		this.kurzexObject = new QObject(0, 0, this.kurzexAnimation.getMaxWidth(), this.kurzexAnimation.getMaxHeight());
		this.kurzexLocation = new QLocation(this.kurzexAnimation.getMaxWidth(), this.kurzexAnimation.getMaxHeight(),
				QLocation.Locations.RIGHT, QLocation.Locations.BOTTOM);

		this.kurzexObject.setSensor(new QObject.sensor() {
			public void touched(QObject Object0, QObject Object1) {
				QGameState.this.isPlayerNextToKurzex = true;
			}

			public void notTouched(QObject Object0) {
				QGameState.this.isPlayerNextToKurzex = false;
			}

			public QDirections moveing(QDirections direction) {
				return null;
			}
		}, new QObject[] { this.playerObject });

		this.wholesomeImagesAnimation = new QAnimation();
		for (j = 0; j <= 12; j++) {
			this.wholesomeImagesAnimation.loadImage(
					QloadImage.getAllImage(String.format("/images/b_%d.jpg", new Object[] { Integer.valueOf(j) })));
		}
		this.wholesomeImages = new ArrayList<>();
	}

	public void enable() {
		this.smoothShowUpCurrentTime = System.currentTimeMillis();
		getCanvas().getKeyboard().initializeKey(87);
		getCanvas().getKeyboard().initializeKey(83);
		getCanvas().getKeyboard().initializeKey(65);
		getCanvas().getKeyboard().initializeKey(68);
	}

	public void disable() {
		getCanvas().getKeyboard().removeKey(87);
		getCanvas().getKeyboard().removeKey(83);
		getCanvas().getKeyboard().removeKey(65);
		getCanvas().getKeyboard().removeKey(68);
	}

	public void spwanMoneyBag() {
		this.moneyBagAmount = ThreadLocalRandom.current().nextInt(
			this.minMoneyBagAmount, this.maxMoneyBagAmount	
		);

		double bagSize = Math.max(((double) moneyBagAmount / (double) maxMoneyBagAmount) * 64, 24);
		moneyBag = new QObject(
			ThreadLocalRandom.current().nextInt(0, QGameState.this.getCanvas().getWidth() / 3),
			ThreadLocalRandom.current().nextInt(QGameState.this.getCanvas().getHeight() - 120, 
			QGameState.this.getCanvas().getHeight() - (int) bagSize), 
			(int) bagSize,(int) bagSize);

		moneyBag.setImage(this.moneyIcon.getScaledInstance(moneyBag.getWidth(), 
			moneyBag.getHeight(), BufferedImage.SCALE_SMOOTH));
		moneyBag.setSensor(new sensor() {

			
			@Override
			public void touched(QObject param1qObject1, QObject param1qObject2) {		
				money += moneyBagAmount;
				moneyBag = null;
			}
			
			@Override
			public QDirections moveing(QDirections param1qDirections) { return null; }
			
			@Override
			public void notTouched(QObject param1qObject) {
			}
			
		}, playerObject);
	}

	public void spwanTrap() {

		if (this.trapRunning) {
			return;
		}

		this.trapRunning = true;
		(new Timer()).schedule(new TimerTask() {
			public void run() {

				long LastCurrTime = System.currentTimeMillis();
				QGameState.this.trapObject.setLocation(
						ThreadLocalRandom.current().nextInt(0, QGameState.this.getCanvas().getWidth() - 32),
						QGameState.this.getCanvas().getHeight() - 32);
				QGameState.this.wasOnTrap = false;

				while (QGameState.this.trapRunning) {
					if (System.currentTimeMillis() - LastCurrTime < 5000L && !QGameState.this.wasOnTrap) {
						QGameState.this.trapAnimation.next();
					} else if (QGameState.this.trapAnimation.getCurrentIndex() > 0) {
						QGameState.this.trapAnimation.before();
					} else {
						QGameState.this.trapRunning = false;
						break;
					}
					QGameState.this.trapObject.update();
					QGameState.this.trapObject.setImage(QGameState.this.trapAnimation.getDraw());
				}

			}
		}, this.trapTimer);
	}

	public void showUmbrella() {

		if (this.haveUmbrella) {
			return;
		}

		this.umbrellaObject = new QObject(this.playerObject.getMustX(), this.playerObject.getMustY() - 24, 48, 48);
		this.umbrellaObject.setImage(this.umbrellaImage.getScaledInstance(48, 48, 4));
		this.haveUmbrella = true;

		this.umbrellaCurrentTimerMS = System.currentTimeMillis();
		this.umbrellaCurrentTime = 31;

	}

	public void playOuch() {
		String sound = QGameState.this.damageSounds[ThreadLocalRandom.current().nextInt(0, 4)];
		QGameState.this.getCanvas().getAudio().playAudio(sound);
	}

	public void setTochSensor(QObject object) {
		List<QObject> touchable = new ArrayList<QObject>();
		touchable.add(this.umbrellaObject);
		touchable.addAll(bulletObjects);
		touchable.add(this.playerObject);

		object.setSensor(new QObject.sensor() {
			public void touched(QObject Object0, QObject Object1) {
				if (Object1.getWidth() == 2 && Object1.getHeight() == 2) {
					bulletObjects.remove(Object1);
					wholesomeImagesToRemove.add(Object0);
				} else if (Object1.equals(QGameState.this.umbrellaObject)) {
					QGameState.this.airEffectObjects.add(new QGameState.AirEffectObject(QGameState.this.airEffect,
							Object0.getMustX() + Object0.getWidth() / 2 - 20,
							Object0.getMustY() + Object0.getHeight() / 2 - 20));
					QGameState.this.getCanvas().getAudio().playAudioWhenReady("pop");
					Object0.setSpeed(-2);
				} else {
					Object0.setSpeed(-1);
					playOuch();
				}
			}

			public void notTouched(QObject Object1) {
			}

			public QDirections moveing(QDirections Directions) {
				return null;
			}
		}, touchable.toArray(new QObject[0]));

	}

	public void updateShoot() {
		ListIterator<Bullet> bullets = this.bulletObjects.listIterator();
		while (bullets.hasNext()) {
			Bullet single = bullets.next();
			if (Math.abs(single.getMustX() - single.dest_X) < 3 || Math.abs(single.getMustY() - single.dest_Y) < 3) {
				bullets.remove();
			} else {
				single.update();
			}
		}

		if (mousePressed) {
			this.shootCoolDown = Math.min(this.shootCoolDown + 3, this.maxShootSpeed);
			if (System.currentTimeMillis() - this.lastShootTimer >= (this.shootAfter + this.shootCoolDown)) {
				this.lastShootTimer = System.currentTimeMillis();
				Bullet singleBullet = new Bullet(this.playerObject.getMustX(), this.playerObject.getMustY(), 2, 2);
				singleBullet.update();
				this.bulletObjects.add(singleBullet);
				QGameState.this.getCanvas().getAudio().playAudio("shoot");
			}
			return;
		}else {
			this.shootCoolDown = Math.max(this.shootCoolDown - 1, this.minShootSpeed);
		}
	}

	public void updateWholesome() {
		wholesomeImages.removeAll(wholesomeImagesToRemove);
		for (QObject aWholesome : this.wholesomeImages) {
			setTochSensor(aWholesome);
		}

		if (this.lastCurrentTimer != -1L && this.startTimer >= -2) {
			if (System.currentTimeMillis() - this.lastCurrentTimer >= 1000L) {
				this.lastCurrentTimer += 1000L;
				this.startTimer = (byte) (this.startTimer - 1);
			}
			return;
		} else if(System.currentTimeMillis() - this.lastCurrentTimer > 100) {
			this.wholesomeSpeed = Math.min(this.wholesomeTargetedSpeed, this.wholesomeSpeed + this.wholesomeSpeedToAdd);
			this.lastCurrentTimer = System.currentTimeMillis();
		}

		ListIterator<QObject> wholesomesList = this.wholesomeImages.listIterator();
		int wholesomeWidth = Math.round((Math.abs(getCanvas().getWidth() - 504) / 2 / this.wholesomeAmount));
		int wholesomeHeight = Math.round((Math.abs(getCanvas().getHeight() - 504) / 2 / this.wholesomeAmount));

		if (this.wholesomeImages.size() <= (this.currentwholesomes = wholesomeWidth + wholesomeHeight)) {
			int xPos = ThreadLocalRandom.current().nextInt(-64, getCanvas().getWidth()) + 1;
			int speed = ThreadLocalRandom.current().nextInt((int) this.wholesomeSpeed) + 1;
			QObject wholesomeObject = new QObject(xPos, -128, 64, 64);

			wholesomeObject.setImage(this.wholesomeImagesAnimation
					.getImage(ThreadLocalRandom.current().nextInt(this.wholesomeImagesAnimation.getSize()))
					.getScaledInstance(64, 64, 4));
			wholesomeObject.setSpeed(speed);
			setTochSensor(wholesomeObject);
			wholesomesList.add(wholesomeObject);
		}

		while (wholesomesList.hasNext()) {
			QObject wholesome = wholesomesList.next();
			if (wholesome.getMustY() > getCanvas().getHeight()) {
				wholesomesList.remove();
				this.money += this.moneyAddedWhenWholesomeKilled;
			} else if (wholesome.getSpeed() < 0) {
				wholesomesList.remove();
				if (wholesome.getSpeed() == -1) {
					this.heart--;
				}
			}

			wholesome.setLocation(wholesome.getMustX(), wholesome.getMustY() + wholesome.getSpeed());
			wholesome.update();
		}
	}

	public void updateKurzex() {
		this.kurzexObject.update();
		this.kurzexAnimation.next();
		this.kurzexObject.setImage(this.kurzexAnimation.getDraw());
		this.kurzexLocation.initialize(getCanvas().getWidth(), getCanvas().getHeight());
		this.kurzexObject.setLocation(this.kurzexLocation.getX() - 124, this.kurzexLocation.getY() - 120);
		if (this.isPlayerNextToKurzex) {
			if (!((QStateManger) getCanvas().getParameter(0)).isJoined("shop")) {
				((QStateManger) getCanvas().getParameter(0)).joinState("shop");
			}
		} else if (((QStateManger) getCanvas().getParameter(0)).isJoined("shop")) {
			((QStateManger) getCanvas().getParameter(0)).forgetState("shop");
		}
	}

	public void umbrellaUpdate() {
		if (this.umbrellaObject != null) {
			if (this.umbrellaCurrentTime <= 0) {
				this.haveUmbrella = false;
				this.umbrellaObject = null;
				return;
			}
			if (System.currentTimeMillis() - this.umbrellaCurrentTimerMS >= 1000L) {
				this.umbrellaCurrentTimerMS += 1000L;
				this.umbrellaCurrentTime--;
			}

			this.umbrellaSmooth = (this.umbrellaSmooth > 0) ? Math.max(this.umbrellaSmooth - 1, 0)
					: this.umbrellaSmooth;

			if (this.umbrellaObject.getMustX() < this.playerObject.getMustX() + this.playerObject.getWidth() / 2
					- this.umbrellaObject.getWidth() / 2) {
				this.umbrellaSmooth = (this.umbrellaSmooth <= 3) ? Math.min(
						(this.umbrellaSmooth + this.playerObject.getMustX() - this.umbrellaObject.getMustX() <= 20) ? 1
								: ((this.playerObject.getMustX() - this.umbrellaObject.getMustX() <= 30) ? 2 : 2),
						3) : this.umbrellaSmooth;
			} else if (this.umbrellaObject.getMustX() >= this.playerObject.getMustX()) {
				this.umbrellaSmooth = (this.umbrellaSmooth >= -3) ? Math.max(
						(this.umbrellaSmooth - this.playerObject.getMustX() - this.umbrellaObject.getMustX() <= 20) ? -1
								: ((this.playerObject.getMustX() - this.umbrellaObject.getMustX() <= 30) ? -2 : -3),
						-3) : this.umbrellaSmooth;
			}

			this.umbrellaObject.setLocation(this.umbrellaObject.getMustX() + this.umbrellaSmooth,
					this.playerObject.getMustY() - 24);
		}
	}

	public void endsGame(boolean win) {
		((QStateManger) getCanvas().getParameter(0)).disable();

		if (!win) {
			((QEndsState) ((QStateManger) getCanvas().getParameter(0)).getStateObject("ends")).isWiner = false;
			((QEndsState) ((QStateManger) getCanvas().getParameter(0)).getStateObject("ends")).startV(
					this.isDesktopWallpaperEnabled, this.isDesktopCringeEnabled, this.isRestartEnabled,
					this.isCloseObsEnabled);
		}

		((QStateManger) getCanvas().getParameter(0)).joinState("ends");
	}

	public void update() {

		if (!this.isDesktopWallpaperEnabled && !this.isDesktopCringeEnabled && !this.isRestartEnabled
				&& !this.isCloseObsEnabled) {
			endsGame(true);
			return;
		}

		if (this.heart <= 0) {
			endsGame(false);
			return;
		}

		if (this.smoothShowUp > 0) {
			if (System.currentTimeMillis() - this.smoothShowUpCurrentTime >= 10L) {
				this.smoothShowUpCurrentTime += 10L;
				this.smoothShowUp -= 5;
			}
		}

		umbrellaUpdate();
		if(moneyBagIsRunning) {
			if(moneyBag == null) {
				if (System.currentTimeMillis() - moneyBagTimeMills > moneyBagTimer) {
					moneyBagTimeMills = System.currentTimeMillis() + moneyBagTimer;
					spwanMoneyBag();
				}
			}else {
				moneyBagTimeMills = System.currentTimeMillis();
				moneyBag.update();
			}
		}

		if (!this.trapRunning) {
			this.trapTimer = ThreadLocalRandom.current().nextInt(trapAround);
			spwanTrap();
		}

		if (this.kurzex_boolean) {
			updateKurzex();
		}

		if (this.wholesome_boolean) {
			updateWholesome();
		}

		updateShoot();
		this.playerObject.update();
		if (this.playerObject.getMustX() != lastPlayerX || this.playerObject.getMustY() != lastPlayerY) {
			if(bulletObjects.isEmpty()) {
				this.shootCoolDown = Math.max(this.shootCoolDown - 10, this.minShootSpeed);
			}
			QGameState.this.getCanvas().getAudio().playAudioWhenReady("walk");
			this.lastPlayerX = this.playerObject.getMustX();
			this.lastPlayerY = this.playerObject.getMustY();
		}

		ListIterator<AirEffectObject> effects = this.airEffectObjects.listIterator();

		while (effects.hasNext()) {
			AirEffectObject effect = effects.next();

			if (!effect.effectEnds()) {
				effect.update();
				continue;
			}
			effects.remove();
		}
	}

	public void render(Graphics gl) {
		if (this.startTimer >= 0) {
			String timerAsString = (this.startTimer > 0) ? String.valueOf(this.startTimer) : "GO";
			Graphics2D gl2 = (Graphics2D) gl;

			gl2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			gl2.setColor(Color.WHITE);
			gl2.setFont(this.timerFont);
			gl2.drawString(String.valueOf(timerAsString),
					getCanvas().getWidth() / 2 - gl2.getFontMetrics().stringWidth(String.valueOf(timerAsString)) / 2,
					120);
			this.lastCurrentTimer = (this.lastCurrentTimer == -1L) ? System.currentTimeMillis() : this.lastCurrentTimer;
		}
		
		gl.drawImage(this.trapObject.getDraw(false), this.trapObject.getMustX(), this.trapObject.getMustY(), null);
		gl.drawImage(this.kurzexObject.getDraw(this.showInfo), this.kurzexObject.getMustX(),
				this.kurzexObject.getMustY() + 30, null);
		gl.drawImage(this.playerObject.getDraw(this.showInfo), this.playerObject.getMustX(),
				this.playerObject.getMustY(), null);

		if(moneyBag != null) {
			gl.drawImage(moneyBag.getDraw(this.showInfo), moneyBag.getMustX(), moneyBag.getMustY(), 
				moneyBag.getWidth(), moneyBag.getHeight(), null);

			gl.setColor(Color.GREEN);
			gl.setFont(this.ItemsFont.deriveFont(1, 16));
			gl.drawString("$"+this.moneyBagAmount, moneyBag.getMustX(), moneyBag.getMustY());

		}

		for (QObject wholesome : this.wholesomeImages) {
			gl.drawImage(wholesome.getDraw(this.showInfo), wholesome.getMustX(), wholesome.getMustY(), null);
		}

		for (Bullet bullets : this.bulletObjects) {
			bullets.render(gl);
		}

		for (AirEffectObject effect : this.airEffectObjects) {
			effect.render(gl);
		}

		gl.setFont(this.ItemsFont);
		gl.setColor(Color.WHITE);

		gl.drawImage(this.moneyIcon, 20, 64, null);
		gl.drawString(String.valueOf(this.heart), 52, 50);

		gl.drawImage(this.heartIcon, 20, 20, null);
		gl.drawString(String.valueOf(this.money), 52, 94);
		
		gl.setFont(this.ItemsFont.deriveFont(Font.BOLD, 10f));
		gl.drawString("Shooting Cooldown", (getCanvas().getWidth() - 120) - 20, 20);
		
		gl.setColor(Color.GRAY);
		gl.fillRect((getCanvas().getWidth() - 120) - 20, 23, 120, 20);
		
		gl.setColor(Color.BLACK);
		gl.fillRect((getCanvas().getWidth() - 120) - 18, 23, 118, 19);
		
		double shootCooldown = ((double) (this.maxShootSpeed - this.shootCoolDown) 
				/ (this.maxShootSpeed - this.minShootSpeed) * 110);

		if(bulletObjects.isEmpty() && this.minShootSpeed != this.shootCoolDown) {
			gl.setColor(new Color(0x82CFFA));
			gl.drawString("Move to speed", (getCanvas().getWidth() - 120) - 20, 55);
			gl.drawString("the cooldown!", (getCanvas().getWidth() - 120) - 20, 55 + gl.getFontMetrics().getHeight());	
		}else {
			gl.setColor(Color.WHITE);
		}

		gl.fillRect((getCanvas().getWidth() - 115) - 20, 28, (int) Math.round(shootCooldown), 10);
		
		gl.setColor(Color.WHITE);
		gl.setFont(this.ItemsFont);
		if (this.umbrellaObject != null) {
			gl.drawImage(this.umbrellaObject.getDraw(this.showInfo), this.umbrellaObject.getMustX(),
					this.umbrellaObject.getMustY(), null);
			gl.drawImage(this.umbrellaImage, getCanvas().getWidth() - 48, 16 + 35, 32, 32, null);
			gl.drawString(String.valueOf((this.umbrellaCurrentTime <= 0) ? 0 : (this.umbrellaCurrentTime - 1)),
					getCanvas().getWidth() - 60, 45 + 35);
		}

		gl.setColor(Color.WHITE);
		for (int aimLength = 0; aimLength <= 4; aimLength++) {
			double angle = Math.atan2(0 - this.playerObject.getMustY(),
					(mousePoint.x + (this.playerObject.getWidth() / 2)) - this.playerObject.getMustX());

			double aimX = (Math.cos(angle) * 10 * aimLength) + this.playerObject.getMustX();
			double aimY = (Math.sin(angle) * 10 * aimLength) + this.playerObject.getMustY();
			gl.fillRect((int) aimX + (this.playerObject.getWidth() / 2), (int) aimY, 2, 2);
		}

		gl.setColor(new Color(0, 0, 0, this.smoothShowUp));
		gl.fillRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight());

		if (this.showInfo) {
			showInfo(gl);
		}

	}

	public void showInfo(Graphics gl) {
		gl.setFont(this.infoFont.deriveFont(1, 12));
		Graphics2D gl2 = (Graphics2D) gl;
		gl2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		int xPos = 16;
		int yPos = getCanvas().getHeight() - 196;

		gl2.setColor(Color.BLUE);
		gl2.drawString("[Press esc to close the game]", xPos, yPos - (gl.getFontMetrics().getHeight() * 11));

		gl2.drawString("minMoneyBagNextShow: " + (moneyBagTimer - (System.currentTimeMillis() - moneyBagTimeMills)), xPos, yPos - (gl.getFontMetrics().getHeight() * 10));
		gl2.drawString("minMoneyBagAmount: " + this.minMoneyBagAmount, xPos, yPos - (gl.getFontMetrics().getHeight() * 9));
		gl2.drawString("maxMoneyBagAmount: " + this.maxMoneyBagAmount, xPos, yPos - (gl.getFontMetrics().getHeight() * 8));
		gl2.drawString("moneyBagIsRunning: " + this.moneyBagIsRunning, xPos, yPos - (gl.getFontMetrics().getHeight() * 7));

		gl2.drawString("wholesomeTargetedSpeed: " + this.wholesomeTargetedSpeed,
		xPos, yPos - (gl.getFontMetrics().getHeight() * 6));

		gl2.drawString("wholesomeSpeedToAdd: " + (this.wholesomeTargetedSpeed - this.wholesomeSpeed),
				xPos, yPos - (gl.getFontMetrics().getHeight() * 5));

		gl2.drawString("wholesomeCurrentSpeed: " + this.wholesomeSpeed,
				xPos, yPos - (gl.getFontMetrics().getHeight() * 4));

		gl2.drawString("moneyAddedWhenWholesomeKilled: " + this.moneyAddedWhenWholesomeKilled,
				xPos, yPos - (gl.getFontMetrics().getHeight() * 3));

		gl2.drawString("PID OF OBS IF FOUND: " + ((Integer) this.getCanvas().getParameter(1)).intValue(), xPos,
				yPos - (gl.getFontMetrics().getHeight() * 2));
		
		gl2.drawString("SHOOT MAX SPEED: " + this.maxShootSpeed, xPos,
				yPos - (gl.getFontMetrics().getHeight() * 1));
		
		gl2.drawString("SHOOT MIN SPEED: " + this.minShootSpeed, xPos, yPos);
		
		gl2.drawString("SHOOT COOLDOWN: " + this.shootCoolDown, xPos,
				yPos + (gl.getFontMetrics().getHeight()));

		gl2.drawString(
				String.format("Player Location : XY(%d, %d)",
						new Object[] { Integer.valueOf(this.playerObject.getMustX()),
								Integer.valueOf(this.playerObject.getMustY()) }),
				xPos, yPos + gl.getFontMetrics().getHeight() * 2);
		gl2.drawString(
				String.format("isPlayerNextToKurzex : %b", new Object[] { Boolean.valueOf(this.isPlayerNextToKurzex) }),
				xPos, yPos + gl.getFontMetrics().getHeight() * 3);
		gl2.drawString(String.format("PlayerHaveUmbrella : %b", new Object[] { Boolean.valueOf(this.haveUmbrella) }),
				xPos, yPos + gl.getFontMetrics().getHeight() * 4);
		gl2.drawString(String.format("trapTimer : %d", new Object[] { Integer.valueOf(this.trapTimer) }), xPos,
				yPos + gl.getFontMetrics().getHeight() * 5);
		gl2.drawString(String.format("trapRunning : %b", new Object[] { Boolean.valueOf(this.trapRunning) }), xPos,
				yPos + gl.getFontMetrics().getHeight() * 6);
		gl2.drawString(String.format("PlayerWasOnTrap : %b", new Object[] { Boolean.valueOf(this.wasOnTrap) }), xPos,
				yPos + gl.getFontMetrics().getHeight() * 7);
		gl2.drawString(
				String.format("isDesktopWallpaperEnabled : %b",
						new Object[] { Boolean.valueOf(this.isDesktopWallpaperEnabled) }),
				xPos, yPos + gl.getFontMetrics().getHeight() * 8);
		gl2.drawString(
				String.format("isDesktopCringeEnabled : %b",
						new Object[] { Boolean.valueOf(this.isDesktopCringeEnabled) }),
				xPos, yPos + gl.getFontMetrics().getHeight() * 9);
		gl2.drawString(
				String.format("isCloseObsEnabled : %b", new Object[] { Boolean.valueOf(this.isCloseObsEnabled) }), xPos,
				yPos + gl.getFontMetrics().getHeight() * 10);
		gl2.drawString(String.format("isRestartEnabled : %b", new Object[] { Boolean.valueOf(this.isRestartEnabled) }),
				xPos, yPos + gl.getFontMetrics().getHeight() * 11);
		gl2.drawString(String.format("currentwholesomes : %d", new Object[] { Integer.valueOf(this.currentwholesomes) }),
				xPos, yPos + gl.getFontMetrics().getHeight() * 12);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mousePressed = false;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mousePressed = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.mousePoint = e.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mousePoint = e.getPoint();
	}
}