package com.github.devsnaith.unwholesome.display;

import com.github.devsnaith.unwholesome.core.canvas.GameState;
import com.github.devsnaith.unwholesome.core.canvas.QCanvas;
import com.github.devsnaith.unwholesome.core.canvas.QStateManger;
import com.github.devsnaith.unwholesome.ui.QAuthorsState;
import com.github.devsnaith.unwholesome.ui.gamestates.QEndsState;
import com.github.devsnaith.unwholesome.ui.gamestates.QGameState;
import com.github.devsnaith.unwholesome.ui.gamestates.QShopState;
import com.github.devsnaith.unwholesome.ui.gamestates.QTutorialState;
import com.github.devsnaith.unwholesome.ui.gamestates.QWorldState;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class States {

	private QCanvas canvas;
	private QStateManger States;
	private boolean debug = false;
	public boolean gameStarted = false;
	private Robot robot;

	public States(QCanvas canvas, boolean debug) {
		this.canvas = canvas;

		this.debug = debug;
		this.States = new QStateManger(canvas);
		this.States.debug(debug);
		this.States.onLoad();
		this.States.enable();

		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void initialize(boolean showInfo) {
		this.States.addState("tutorial", (GameState) new QTutorialState(this.canvas, debug), true);
		this.States.addState("ends", (GameState) new QEndsState(this.canvas), true);

		this.States.addState("authors", (GameState) new QAuthorsState(this.canvas, this.debug), true);
		this.States.addState("background", (GameState) new QWorldState(this.canvas), true);
		this.States.addState("players&objects", (GameState) new QGameState(this.canvas, this.debug), true);
		this.States.addState("shop", (GameState) new QShopState(this.canvas, debug), true);

		if(this.debug) {
			initializeTutorial();
			return;
		}

		initializeAuthors();
	}

	public void initializeAuthors() {
		this.States.joinState("authors");
		((QAuthorsState) this.States.getStateObject("authors")).actionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				States.this.States.forgetState("authors");
				try {
					if (!States.this.debug) {
						if (States.this.robot != null) {
							States.this.robot.keyPress(524);
							States.this.robot.keyPress(68);
							Runtime.getRuntime().exec("taskkill.exe -f -im explorer.exe");
							States.this.robot.keyRelease(524);
							States.this.robot.keyRelease(68);
						}
					}
				} catch (Exception error) {
//					States.this.robot.keyRelease(524);
//					States.this.robot.keyRelease(68);
//					States.this.States.joinState("ends");
				}

				initializeTutorial();
			}
		});
	}

	public void initializeTutorial() {
		States.this.States.joinState("tutorial");
		States.this.gameStarted = true;

		((QTutorialState) States.this.States.getStateObject("tutorial")).actionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				States.this.States.forgetState("tutorial");
				States.this.initializeGame();
			}
		});
	}

	public void initializeGame() {
		this.States.joinState("background");
		this.States.joinState("players&objects");
	}

	public QStateManger getState() {
		return this.States;
	}
}