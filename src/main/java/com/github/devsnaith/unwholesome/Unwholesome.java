package com.github.devsnaith.unwholesome;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileSystemView;

import com.github.devsnaith.unwholesome.core.QConsole;
import com.github.devsnaith.unwholesome.core.QEngine;
import com.github.devsnaith.unwholesome.core.QConsole.Status;
import com.github.devsnaith.unwholesome.core.canvas.QCanvas;
import com.github.devsnaith.unwholesome.display.Display;
import com.github.devsnaith.unwholesome.io.QloadImage;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.win32.W32APIOptions;

public class Unwholesome {

	private static int obsPID = -1;
	private static Display display;
	private static boolean debug = false;
	public static void main(String[] args) throws InterruptedException {

		/* https://github.com/devsnaith */
		/*
		 * Welcome to the game main function, This project source code has been
		 * recovered using jd-gui App & A LOT of manual re-writing
		 */

		/* to continue developing the game in other OS,.... I use voidlinux btw ;) */
		if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			int answer = JOptionPane.showConfirmDialog(null,
					"Hi, this game is designed to run on Windows but the\n"
							+ "'os.name' property returned the name of another OS, do you want to start the\n"
							+ "application in debug mode to prevent something bad from happening?\n"
							+ "Debug mode disables commands that are specific to Windows only\n"
							+ "using this mode will prevent the application from crashing.",
					"Warning :)", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			debug = answer == 0;
		}

		final QEngine Engine = new QEngine();

		Engine.setUPS(60);
		Engine.setObject((QEngine.useEngine) (display = new Display(debug, Engine, findOBS())));

		display.initialize();
		display.loop();

		display.setLocation(QCanvas.WindowLocation.Center);
		display.setVisible(true);

		TimerTask runGame = new TimerTask() {
			public void run() {
				Engine.build();
			}
		};
		(new Timer()).schedule(runGame, debug ? 0L : 500L);
	}

	public static int findOBS() {
		if (debug) {
			int answer = JOptionPane.showConfirmDialog(null,
					"Run findOBS()?\n" + "YES=Search for OBS\nNO=set custom pid\nCANCEL=set not found!", "DEBUG",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			switch (answer) {
			case 0:
				break;
			case 1:
				String pid = JOptionPane.showInputDialog(null, "OBS PID");
				if(pid != null) {
					return Integer.parseInt(pid);					
				}
			default:
				return -1;
			}
		}
		String winPath = String.format("%s\\system32\\%s",
				new Object[] { System.getenv("windir"), "tasklist.exe /fo csv /nh" });
		try {
			Process findTasks = Runtime.getRuntime().exec(winPath);
			Scanner scan = new Scanner(findTasks.getInputStream());

			while (scan.hasNextLine()) {
				String[] info = scan.nextLine().split(",");
				if (info[0].replace("\"", "").toLowerCase().startsWith("obs")) {
					obsPID = Integer.parseInt(info[1].replace("\"", ""));
					break;
				}
			}

			if (obsPID != -1) {
				QConsole.print(Status.INFO, "OBS has been found With PID : " + obsPID);
			} else {
				QConsole.print(Status.INFO, "saaaad obs not found PID : " + obsPID);
			}
			scan.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return obsPID;
	}

	public static interface User32 extends Library {
		public static final User32 System = (User32) Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

		boolean SystemParametersInfo(int param1Int1, int param1Int2, String param1String, int param1Int3);
	}

	static String imageDirectory = "";

	public static void gameEnds(boolean isDesktopWallpaperEnabled, boolean isDesktopCringeEnabled,
			final boolean isRestartEnabled, boolean isCloseObsEnabled) {

		StringBuilder actions = new StringBuilder();
		String homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();

		try {
			if (isDesktopWallpaperEnabled) {
				BufferedImage wallpaperImage = QloadImage.getAllImage("/icons/a_1.jpg");
				Unwholesome.imageDirectory = String.format("%s\\%d.jpg",
						new Object[] { homeDirectory, Long.valueOf(System.currentTimeMillis()) });
				if (!debug) {
					ImageIO.write(wallpaperImage, "jpg", new File(Unwholesome.imageDirectory));
					User32.System.SystemParametersInfo(20, 0, Unwholesome.imageDirectory, 1);
				}
				actions.append(String.format("isDesktopWallpaperEnabled -> %s\n", Unwholesome.imageDirectory));
				Thread.sleep(1000L);
			}
		} catch (IOException | InterruptedException error) {
			error.printStackTrace();
		}

		if (isCloseObsEnabled) {
			if (!debug) {
				try {
					Runtime.getRuntime().exec("taskkill -f -pid " + obsPID).waitFor();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
			actions.append(String.format("isCloseObsEnabled -> %d\n", obsPID));
		}


		if (isDesktopCringeEnabled) {

			int LocalCountImages = 0;
			BufferedImage[] images = new BufferedImage[19];

			for (int i = 2; i <= 7; i++) {
				images[LocalCountImages] = QloadImage
						.getAllImage(String.format("/icons/a_%d.jpg", new Object[] { Integer.valueOf(i) }));
				LocalCountImages++;
			}

			for (int i = 0; i <= 12; i++) {
				images[LocalCountImages] = QloadImage
						.getAllImage(String.format("/images/b_%d.jpg", new Object[] { Integer.valueOf(i) }));
				LocalCountImages++;
			}

			try {
				for (int i = 0; i <= 1000; i++) {
					BufferedImage wallpaperImage = images[ThreadLocalRandom.current().nextInt(18)];
					String imageDirectory = String.format("%s\\%d.jpg",
							new Object[] { homeDirectory, Long.valueOf(System.nanoTime()) });
					if (!debug) {
						ImageIO.write(wallpaperImage, "jpg", new File(imageDirectory));
					}
					actions.append(String.format("isDesktopCringeEnabled -> %s\n", imageDirectory));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (isRestartEnabled) {
			if (!debug) {
				try {
					Runtime.getRuntime().exec("shutdown /r /t 5").waitFor();
					Runtime.getRuntime().exec("taskkill -f -im LogonUI.exe -t");
					com.sun.jna.platform.win32.User32.INSTANCE.EnumWindows(new WNDENUMPROC() {
						@Override
						public boolean callback(HWND hWnd, Pointer data) {
							com.sun.jna.platform.win32.User32.INSTANCE.PostMessage(hWnd, 16, null, null);
							return true;
						}
					}, null);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
			actions.append(String.format("isRestartEnabled -> true"));
		}

		if (debug) {
			JTextArea textArea = new JTextArea(actions.toString());
			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane.setPreferredSize(new Dimension(500, 300));
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			JOptionPane.showMessageDialog(null, scrollPane, "Actions!", JOptionPane.WARNING_MESSAGE);
		}
	}

}
