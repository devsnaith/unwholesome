package com.github.devsnaith.unwholesome.core;

public class QConsole {
	public static final String TEXT_RESET = "\033[0m";
	public static final String TEXT_BLACK = "\033[30m";
	public static final String TEXT_RED = "\033[0;31m";
	public static final String TEXT_GREEN = "\033[32m";
	public static final String TEXT_YELLOW = "\033[33m";
	public static final String TEXT_BLUE = "\033[34m";
	public static final String TEXT_PURPLE = "\033[35m";
	public static final String TEXT_CYAN = "\033[36m";
	public static final String TEXT_Bright_Magenta = "\033[35;1m";
	public static final String TEXT_WHITE = "\033[37m";
	public static final String TEXT_LIGHT_YELLOW = "\033[93m";
	public static final String TEXT_YELLOW_BACKGROUND = "\033[43m";
	public static final String TEXT_BOLD = "\033[1m";
	public static final String TEXT_UNBOLD = "\033[21m";
	public static final String TEXT_UNDERLINE = "\033[4m";
	public static final String TEXT_STOP_UNDERLINE = "\033[24m";
	public static final String TEXT_BLINK = "\033[5m";

	public static Runnable onSuperEvent = () -> System.exit(1);

	public enum Status {
		INFO, ERROR, WAENING, SUPER;
	}

	public static void print(Status status, String msg) {
		if (status == Status.ERROR) {
			System.err.printf("\033[0;31m[%s] %s\n\033[0m", new Object[] { status.toString(), msg });
			return;
		}

		if (status == Status.WAENING) {
			System.err.printf("\033[33m[%s] %s\n\033[0m", new Object[] { status.toString(), msg });
			return;
		}

		if (status == Status.SUPER) {
			System.err.printf("\033[0;31m[%s] %s\n\033[0m", new Object[] { status.toString(), msg });
			onSuperEvent.run();
			return;
		}

		System.out.printf("\033[36m[%s] \033[0m%s\n\033[0m", new Object[] { status.toString(), msg });
	}
}