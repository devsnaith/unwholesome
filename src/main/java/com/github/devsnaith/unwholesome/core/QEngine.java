package com.github.devsnaith.unwholesome.core;

public class QEngine implements Runnable {

	private Thread QTimer;
	private int UPS = 30;

	private useEngine Object;

	public QEngine() {
		QConsole.print(QConsole.Status.INFO, "engine has been initialized");
	}

	public void setUPS(int UPS) {
		this.UPS = UPS;
	}

	public int getUPS() {
		return this.UPS;
	}

	public synchronized void build() {
		if (this.Object == null) {
			QConsole.print(QConsole.Status.SUPER, "no object have been added");
		} else if (this.QTimer != null) {
			QConsole.print(QConsole.Status.ERROR, "engine is alrady running");
			return;
		}

		this.QTimer = new Thread(this, "@devsnaith Engine, " + this.Object.getClass().getSimpleName());
		this.QTimer.start();

		QConsole.print(QConsole.Status.INFO, "engine has been started");
	}

	public synchronized void kill() throws InterruptedException {
		if (this.QTimer == null) {
			QConsole.print(QConsole.Status.ERROR, "engine is alrady stopped");
			return;
		}

		this.QTimer.interrupt();

		if (this.QTimer.isInterrupted()) {
			QConsole.print(QConsole.Status.INFO,
					String.format("\"%s\" has been Interrupted", new Object[] { this.QTimer.getName() }));
		} else {
			QConsole.print(QConsole.Status.SUPER,
					String.format("\"%s\" Error while interrupting engine", new Object[] { this.QTimer.getName() }));
		}

		try {
			this.QTimer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			QConsole.print(QConsole.Status.SUPER, String.format("\"%s\" InterruptedException : " + e.getMessage(),
					new Object[] { this.QTimer.getName() }));
		}
	}

	public void setObject(useEngine useEngine1) {
		this.Object = useEngine1;
		QConsole.print(QConsole.Status.INFO, String.format("linked between 'Engine <=> %s'",
				new Object[] { useEngine1.getClass().getSimpleName() }));
	}

	public void run() {
		long LastNanoTime = System.nanoTime();
		long LastCurrTime = System.currentTimeMillis(), PrintInfo = LastCurrTime;
		StringBuilder Builder = new StringBuilder();

		double delta = 0.0D;
		int update = 0;

		while (!this.QTimer.isInterrupted()) {

			long currentNanoTime = System.nanoTime();
			delta += (currentNanoTime - LastNanoTime) / (1000000000.0 / this.UPS);
			LastNanoTime = currentNanoTime;

			while (delta >= 0) {
				this.Object.loop();
				delta--;
				update++;
			}

			this.Object.draw();

			if (System.currentTimeMillis() - LastCurrTime >= 1000L) {
				LastCurrTime += 1000L;
				String Color = "\033[32m";
				if (update + 5 < this.UPS) {
					Color = "\033[0;31m";
				} else if (update + 1 < this.UPS) {
					Color = "\033[33m";
				} else if (update - 2 > this.UPS) {
					Color = "\033[35;1m";
				}

				Builder.append(String.valueOf(Color) + update + "\033[0m" + ", ");
				update = 0;

				if (System.currentTimeMillis() - PrintInfo >= 5000L) {
					PrintInfo = System.currentTimeMillis() + 5000L;
					QConsole.print(QConsole.Status.INFO,
							"UPS Information : " + Builder.toString().substring(0, Builder.toString().length() - 2));
					Builder = new StringBuilder();
				}
			}
		}
	}

	public static interface useEngine {
		void loop();
		void draw();
	}

}