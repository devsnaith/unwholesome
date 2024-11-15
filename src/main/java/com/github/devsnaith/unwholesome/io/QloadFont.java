package com.github.devsnaith.unwholesome.io;

import java.awt.Font;

import com.github.devsnaith.unwholesome.core.QConsole;

public class QloadFont {
	private static Font font;

	private static void LoadFont(String path) {
		try {
			font = Font.createFont(0, QloadFont.class.getResourceAsStream(path));
		} catch (Exception e) {
			QConsole.print(QConsole.Status.ERROR, "Exception : " + e.getMessage());
		}
	}

	public static Font getLastFont() {
		return font;
	}

	public static Font getFont(String path) {
		LoadFont(path);
		return font;
	}
}