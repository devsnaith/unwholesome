package com.github.devsnaith.unwholesome.core.canvas;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.JFrame;

import com.github.devsnaith.unwholesome.core.QConsole;
import com.github.devsnaith.unwholesome.core.QEngine;
import com.github.devsnaith.unwholesome.io.QAudioManger;
import com.github.devsnaith.unwholesome.io.listeners.QKeyboardListener;

public class QCanvas implements QEngine.useEngine {

	private static int defaultMonitorWidth = (Toolkit.getDefaultToolkit().getScreenSize()).width;
	private static int defaultMonitorHeight = (Toolkit.getDefaultToolkit().getScreenSize()).height;

	private static int defaultWidth = 900;
	private static int defaultHeight = defaultWidth / 16 * 9;
	private String defaultName = "Bruh, just put any name lol";

	private GameState _GameState;
	private Dimension Size;
	private Color color;

	boolean clearByGraphics = true;
	private QKeyboardListener Keyboard;

	private QAudioManger Audios;
	private JFrame Frame;

	private Canvas Canvas;
	private boolean isInitialized = false;
	private Object[] objects;

	private Icon icon;
	private boolean isCloseing = false;

	public enum WindowLocation {
		TopLeft, TopRight, Top, CenterLeft, CenterRight, Center, BottomLeft, BottomRight, Bottom;
	}

	public QCanvas(String title) {
		if (title != null) {
			this.defaultName = title;
		}

		this.Frame = new JFrame();
		this.Canvas = new Canvas();
		this.Frame.setDefaultCloseOperation(0);
		this.Audios = new QAudioManger();
	}

	public void onLoad() {
		QConsole.print(QConsole.Status.INFO, "Screen has been initialized");
		this._GameState.onLoad();
	}

	public void setParameters(Object... objects) {
		if (this.objects == null) {
			this.objects = objects;
			return;
		}
		QConsole.print(QConsole.Status.ERROR, "parameters can not override");
	}

	public Object getParameter(int index) {
		return this.objects[index];
	}

	public void setIcon(final Image icon) {
		if (this.Frame != null) {
			this.Frame.setIconImage(icon);
			this.icon = new Icon() {
				public void paintIcon(Component c, Graphics g, int x, int y) {
					g.drawImage(icon, 0, 0, null);
				}

				public int getIconWidth() {
					return icon.getWidth(null);
				}

				public int getIconHeight() {
					return icon.getHeight(null);
				}
			};
			return;
		}
		QConsole.print(QConsole.Status.SUPER, "you cannot use setIcon and QCanvas not initalized yet");
	}

	public Image getIconImage() {
		if (this.Frame != null) {
			return this.Frame.getIconImage();
		}

		QConsole.print(QConsole.Status.SUPER, "you cannot use getIcon and QCanvas not initalized yet");
		return null;
	}

	public Icon getIcon() {
		return this.icon;
	}

	public void setAlwaysOnTop(boolean Command) {
		if (this.Frame != null) {
			this.Frame.setAlwaysOnTop(Command);
			this.Frame.requestFocus();
			return;
		}
		QConsole.print(QConsole.Status.SUPER, "you cannot use setAlwaysOnTop and QCanvas not initalized yet");
	}

	public boolean isAlwaysOnTop() {
		if (this.Frame != null)
			return this.Frame.isAlwaysOnTop();
		QConsole.print(QConsole.Status.SUPER, "you cannot use isAlwaysOnTop and QCanvas not initalized yet");
		return false;
	}

	public void closeRequest(final int exitCode, long delay) {
		if (this.isCloseing) {
			return;
		}

		this.isCloseing = true;
		TimerTask close = new TimerTask() {
			public void run() {
				QConsole.print(QConsole.Status.INFO, "Disabling " + QCanvas.this._GameState.getClass().getSimpleName());
				QCanvas.this._GameState.disable();

				QConsole.print(QConsole.Status.INFO, "Closeing window, exitCode : " + exitCode);
				System.exit(exitCode);
			}
		};
		(new Timer()).schedule(close, delay);

		if (delay > 0L) {
			QConsole.print(QConsole.Status.INFO,
					String.format("Window will close after %sms", new Object[] { Long.valueOf(delay) }));
		}
	}

	public void setLocation(WindowLocation Location) {
		switch (Location) {
		case TopLeft:
			setLocation(0, 0);
			break;

		case TopRight:
			setLocation(defaultMonitorWidth - getWidth(), 0);
			break;

		case Top:
			setLocation(defaultMonitorWidth / 2 - getWidth() / 2, 0);
			break;

		case CenterLeft:
			setLocation(0, defaultMonitorHeight / 2 - getHeight() / 2);
			break;

		case CenterRight:
			setLocation(defaultMonitorWidth - getWidth(), defaultMonitorHeight / 2 - getHeight() / 2);
			break;

		case Center:
			this.Frame.setLocationRelativeTo((Component) null);
			break;

		case BottomLeft:
			setLocation(0, defaultMonitorHeight - getHeight());
			break;

		case BottomRight:
			setLocation(defaultMonitorWidth - getWidth(), defaultMonitorHeight - getHeight());
			break;

		default:
			setLocation(defaultMonitorWidth / 2 - getWidth() / 2, defaultMonitorHeight - getHeight());
			break;
		}

		QConsole.print(QConsole.Status.INFO,
				"\033[1m\033[93m" + String.format("Window location has been set to [%s, [%dx%d]]", new Object[] {
						Location.toString(), Integer.valueOf(this.Frame.getX()), Integer.valueOf(this.Frame.getY()) }));
	}

	public void setLocation(int x, int y) {
		this.Frame.setLocation(x, y);
	}

	public void clearByGraphics(boolean commend) {
		this.clearByGraphics = commend;
	}

	public void setVisible(boolean Commend) {
		this.Frame.setVisible(Commend);
		QConsole.print(QConsole.Status.INFO, "screen visiblete has been set to " + Commend);
	}

	public void setTitle(String title) {
		this.Frame.setTitle(title);
	}

	public void initialize(GameState _GameState, int numBuffer, int width, int height, Color color,
			boolean undecorated) {
		if (_GameState == null) {
			QConsole.print(QConsole.Status.SUPER, "GameState parameter must not be equals to null");
		} else {
			QConsole.print(QConsole.Status.INFO, String.format("linked between 'Canvas <=> %s'",
					new Object[] { _GameState.getClass().getSimpleName() }));
			QConsole.print(QConsole.Status.INFO,
					"\033[33m"
							+ String.format("window size : width = %d, height = %d",
									new Object[] { Integer.valueOf(defaultWidth), Integer.valueOf(defaultHeight) })
							+ "\033[0m");
		}

		if (!undecorated) {
			QConsole.print(QConsole.Status.INFO,
					"\033[0;31m\033[43m sometimes when undecorated equals false that may make some problems. ");
		}

		if (numBuffer < 1) {
			QConsole.print(QConsole.Status.SUPER, "Number of buffers must be at least 1");
		}

		defaultWidth = (width >= 0) ? width : ((width == -1) ? getMonitorWidth() : defaultWidth);
		defaultHeight = (height >= 0) ? height : ((height == -1) ? getMonitorHeight() : defaultHeight);

		this.Keyboard = new QKeyboardListener();
		this.Size = new Dimension(defaultWidth, defaultHeight);

		defaultWidth = width;
		defaultHeight = height;

		this.Frame.setPreferredSize(this.Size);
		this.Canvas.setPreferredSize(this.Size);

		this._GameState = _GameState;
		this.color = (color != null) ? color : Color.BLACK;

		this.Frame.setBackground(color);
		this.Canvas.setBackground(color);
		this.Frame.getContentPane().setBackground(color);

		this.Frame.setUndecorated(undecorated);
		this.Frame.setTitle(this.defaultName);

		this.Frame.addKeyListener((KeyListener) this.Keyboard);
		this.Canvas.addKeyListener((KeyListener) this.Keyboard);
		
		this.Frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				QCanvas.this.closeRequest(0, 0L);
			}
		});

		this.Frame.add(this.Canvas);
		this.Frame.pack();

		this.Canvas.createBufferStrategy(numBuffer);
		this.isInitialized = true;

		this.Frame.requestFocus();
		this.Canvas.requestFocus();

		onLoad();
		_GameState.enable();
		QConsole.print(QConsole.Status.INFO,
				String.format("%s has been enabled", new Object[] { _GameState.getClass().getSimpleName() }));
	}

	public void loop() {
		if (this._GameState == null) {
			return;
		}
		this._GameState.update();
	}

	public void draw() {
		if (this._GameState == null) {
			return;
		}

		BufferStrategy QSystem = this.Canvas.getBufferStrategy();
		Graphics gl = QSystem.getDrawGraphics();

		if (this.clearByGraphics) {
			gl.setColor(getDefaultBackgroundColor());
			gl.fillRect(0, 0, this.Canvas.getWidth(), this.Canvas.getHeight());
		}

		this._GameState.render(gl);
		gl.dispose();
		QSystem.show();
	}

	public long getFrameHWNd() {
		try {
			Field field = Component.class.getDeclaredField("peer");
			field.setAccessible(true);

			Object peer = field.get(this.Frame);
			field.setAccessible(false);

			if (peer == null)
				return 0L;

			Method method = peer.getClass().getMethod("getHWnd");
			return (long) method.invoke(peer);
		} catch (Exception e) {
			return 0L;
		}
	}

	public boolean isCloseing() {
		return this.isCloseing;
	}

	public boolean isInitialized() {
		return this.isInitialized;
	}

	public boolean isvisible() {
		return this.Frame.isVisible();
	}

	public boolean isClearByGraphics() {
		return this.clearByGraphics;
	}

	public String getDefaultTitle() {
		return this.defaultName;
	}

	public String getTitle() {
		return this.Frame.getTitle();
	}

	public QKeyboardListener getKeyboard() {
		return this.Keyboard;
	}

	public QAudioManger getAudio() {
		return this.Audios;
	}

	public Color getDefaultBackgroundColor() {
		return this.color;
	}

	public int getDefaultWidth() {
		return defaultWidth;
	}

	public int getDefaultHeight() {
		return defaultHeight;
	}

	public int getMonitorWidth() {
		return defaultMonitorWidth;
	}

	public int getMonitorHeight() {
		return defaultMonitorHeight;
	}

	public int getX() {
		return this.Frame.getX();
	}

	public int getY() {
		return this.Frame.getY();
	}

	public int getWidth() {
		return this.Frame.getWidth();
	}

	public int getHeight() {
		return this.Frame.getHeight();
	}

	public Canvas getCanvas() {
		return this.Canvas;
	}
}