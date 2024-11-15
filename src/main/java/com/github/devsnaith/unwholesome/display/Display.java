package com.github.devsnaith.unwholesome.display;

import com.github.devsnaith.unwholesome.core.QConsole;
import com.github.devsnaith.unwholesome.core.QEngine;
import com.github.devsnaith.unwholesome.core.QConsole.Status;
import com.github.devsnaith.unwholesome.core.canvas.GameState;
import com.github.devsnaith.unwholesome.core.canvas.QCanvas;
import com.github.devsnaith.unwholesome.io.QloadImage;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JOptionPane;

public class Display extends QCanvas {

	int maxWidth = getDefaultWidth();
	int maxHeight = getDefaultHeight();
	boolean debug = false;

	private WinDef.HWND hwnd;
	private int obsPID;
	private States State;

	private long taskmgrKillTime;

	public Display(boolean debug, QEngine Enginem, int obsPID) {
		super("UnWholesome");
		this.obsPID = obsPID;
		this.debug = debug;
	}

	public void initialize() {
		File meowFile = null;
		Properties properties = new Properties();
		try {
			String desktopPath = ".";
			meowFile = new File(String.valueOf(desktopPath) + /* OLD NAME -> "/meow.cat" */ "/meow.conf");

			QConsole.print(Status.INFO, "Reading configuraion file at " + meowFile.getAbsoluteFile());
			properties.load(new FileInputStream(meowFile));

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		if (meowFile != null && meowFile.exists()) {
			if (!debug)
				this.debug = (Integer.parseInt(properties.getProperty("debug", "0")) == 1);
			QConsole.onSuperEvent = () -> {
			};
			this.maxWidth = Integer.parseInt(properties.getProperty("width", String.valueOf(this.maxWidth)));
			this.maxHeight = Integer.parseInt(properties.getProperty("height", String.valueOf(this.maxHeight)));
		}

		this.State = new States(this, this.debug);
		initialize((GameState) this.State.getState(), 2, this.maxWidth, this.maxHeight, Color.BLACK, true);
		setParameters(new Object[] { this.State.getState(), Integer.valueOf(this.obsPID) });
		setIcon(QloadImage.getAllImage("/Icon.png"));
		this.State.initialize(true);
		getKeyboard().initializeKey(27);
		this.hwnd = new WinDef.HWND(new Pointer(getFrameHWNd()));
	}

	public void loop() {
		try {
			if (!debug && this.State.gameStarted) {
				setAlwaysOnTop(true);
				if(System.currentTimeMillis() > taskmgrKillTime) {
					User32.INSTANCE.ShowWindow(this.hwnd, 9);
					User32.INSTANCE.SetForegroundWindow(this.hwnd);
					taskmgrKillTime = System.currentTimeMillis() + 3000;
					String winPath = String.format("%s\\system32\\%s", 
						new Object[] { System.getenv("windir"), "taskkill.exe /f /im taskmgr.exe /t" });
						Runtime.getRuntime().exec(winPath);
				}
			}
		} catch (java.lang.UnsatisfiedLinkError | java.lang.NoClassDefFoundError | IOException e) {
			e.printStackTrace();
		}
		if (this.debug && getKeyboard().isPressed(27)) {
			super.closeRequest(0, 0L);
		}
		super.loop();
	}

	public void closeRequest(int exitCode, long delay) {
		if (!this.State.gameStarted) {
			super.closeRequest(exitCode, delay);
		}
		
		if (!debug && exitCode == 1) {
			try {
				Runtime.getRuntime().exec("explorer.exe");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"lol i'm sorry but error heppend while re-open system stuff, restart your pc.");
			}
			super.closeRequest(0, delay);
		}
	}
}