package com.github.devsnaith.unwholesome.core.canvas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import com.github.devsnaith.unwholesome.core.QConsole;

public class QStateManger extends GameState {

	private HashMap<String, GameState> States;
	private ArrayList<GameState> _GameStates;
	private boolean debug = false;
	private Font defaultFont;

	public void enable() {
		try {
			String defaultFontName = "/Fonts/Roboto-Light.ttf";
			Font loadFont = Font.createFont(0, getClass().getResourceAsStream(defaultFontName));
			this.defaultFont = loadFont.deriveFont(0, 12.0F);
		} catch (FontFormatException | java.io.IOException e) {
			@SuppressWarnings("deprecation")
			String[] FontsName = Toolkit.getDefaultToolkit().getFontList();
			if (FontsName.length > 0) {
				this.defaultFont = new Font(FontsName[0], 0, 12);
				QConsole.print(QConsole.Status.INFO, "default font for engine is " + FontsName[0]);
			}
		}
	}

	public void disable() {
		for (String key : this.States.keySet()) {
			forgetState(key);
		}
	}

	public QStateManger(QCanvas Canvas) {
		super(Canvas);
	}

	public void onLoad() {
		this.States = new HashMap<>();
		this._GameStates = new ArrayList<>();
	}

	public void debug(boolean debug) {
		this.debug = debug;
	}

	public void addState(String StateName, GameState State, boolean initializeIt) {
		this.States.put(StateName, State);
		if (initializeIt) {
			State.onLoad();
		}
	}

	public void initializeState(String StateName) {
		((GameState) this.States.get(StateName)).onLoad();
	}

	public boolean isJoined(String StateName) {
		GameState State;
		if ((State = this.States.get(StateName)) != null && this._GameStates.contains(State)) {
			return true;
		}
		return false;
	}

	public void joinState(String StateName) {
		GameState State;
		if ((State = this.States.get(StateName)) == null) {
			return;
		}

		if (!this._GameStates.contains(State)) {
			State.enable();
			this._GameStates.add(State);

			QConsole.print(QConsole.Status.INFO, String.format("\033[32m[%s] %s-> %s has been enabled",
					new Object[] { getClass().getSimpleName(), "\033[0m", State.getClass().getSimpleName() }));
		}

	}

	public void forgetState(String StateName) {
		GameState State;
		if ((State = this.States.get(StateName)) == null) {
			return;
		}

		if (this._GameStates.contains(State)) {
			QConsole.print(QConsole.Status.INFO, String.format("\033[32m[%s] %s-> Disabling %s",
					new Object[] { getClass().getSimpleName(), "\033[0m", State.getClass().getSimpleName() }));
			State.disable();
			this._GameStates.remove(State);
		}
	}

	public Object getStateObject(String StateName) {
		return this.States.get(StateName);
	}

	public void update() {
		int state_number = -1;
		try {
			if (this._GameStates.size() > 0) {
				for (int index = 0; index < this._GameStates.size(); index++) {
					state_number = index;
					GameState Object = this._GameStates.get(index);
					Object.update();
				}
				return;
			}
		} catch (ArrayIndexOutOfBoundsException error) {
			error.printStackTrace();
			if (!getCanvas().isCloseing()) {
				QConsole.print(QConsole.Status.SUPER, "error while update a state number " + state_number);
			}
		}
	}

	public void render(Graphics gl) {
		if (this._GameStates.size() > 0) {
			for (int i = 0; i < this._GameStates.size(); i++) {
				GameState Object = this._GameStates.get(i);
				Object.render(gl);
			}
		}

		if (!this.debug) {
			return;
		}

		Graphics2D gl2 = (Graphics2D) gl;

		gl2.setFont(this.defaultFont);
		gl2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		gl2.setComposite(AlphaComposite.SrcOver.derive(1.0F));

		gl2.setColor(Color.WHITE);
		gl2.drawString("[ StateMnager ]", 10, gl2.getFontMetrics().getHeight() + 5);
		gl2.drawString("STATES_SIZE " + this.States.size(), 10, gl2.getFontMetrics().getHeight() * 2 + 5);

		int index = 0;
		for (String key : this.States.keySet()) {
			gl2.drawString(
					String.format("[ %s ] Key : %s",
							new Object[] { ((GameState) this.States.get(key)).getClass().getSimpleName(), key }),
					40, gl2.getFontMetrics().getHeight() * (3 + index) + 5);
			index++;
		}
	}
}