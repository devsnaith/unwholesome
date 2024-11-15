package com.github.devsnaith.unwholesome.graphics.drawers;

import java.awt.Image;
import java.util.ArrayList;

import com.github.devsnaith.unwholesome.core.QConsole;

public class QAnimation {
	private int countImage = 0;
	private ArrayList<Image> Images;
	private long LastCurrTime = -1L;
	private long sleepMs = 0L;

	private int maxWidth = 0;
	private int maxHeight = 0;

	private boolean loop = true;

	public QAnimation() {
		this.Images = new ArrayList<>();
	}

	public long resetTimer() {
		long LastCurrTime = System.currentTimeMillis();
		this.LastCurrTime = LastCurrTime;
		return LastCurrTime;
	}

	public void loop(boolean Command) {
		this.loop = Command;
	}

	public void loadImage(Image image) {
		this.Images.add(image);
		this.maxWidth = (this.maxWidth < image.getWidth(null)) ? image.getWidth(null) : this.maxWidth;
		this.maxHeight = (this.maxHeight < image.getHeight(null)) ? image.getHeight(null) : this.maxHeight;
	}

	public void sleep(long ms) {
		this.sleepMs = ms;
	}

	public long getCurrTime() {
		return System.currentTimeMillis() - this.LastCurrTime;
	}

	public void next() {
		this.LastCurrTime = (this.LastCurrTime == -1L) ? resetTimer() : this.LastCurrTime;
		if (System.currentTimeMillis() - this.LastCurrTime >= this.sleepMs) {
			this.LastCurrTime = -1L;
			this.countImage = (this.countImage < this.Images.size() - 1) ? (this.countImage + 1)
					: (this.loop ? 0 : this.countImage);
		}
	}

	public void before() {
		this.LastCurrTime = (this.LastCurrTime == -1L) ? resetTimer() : this.LastCurrTime;
		if (System.currentTimeMillis() - this.LastCurrTime >= this.sleepMs) {
			this.LastCurrTime = -1L;
			this.countImage = (this.countImage > 0) ? (this.countImage - 1)
					: (this.loop ? this.Images.size() : this.countImage);
		}
	}

	public void toMax() {
		this.countImage = this.Images.size() - 1;
	}

	public void toBeginning() {
		this.countImage = 0;
	}

	public void setImage(int index) {
		if (index < 0) {
			QConsole.print(QConsole.Status.WAENING, "the value cannot be less than 0");
			return;
		}
		if (index > this.Images.size() - 1) {
			QConsole.print(QConsole.Status.WAENING, "the value is bigger than image list size");
			return;
		}
		this.countImage = index;
	}

	public int getCurrentIndex() {
		return this.countImage;
	}

	public Image getImage(int index) {
		return this.Images.get(index);
	}

	public Image getDraw() {
		return this.Images.get(this.countImage);
	}

	public int getMaxWidth() {
		return this.maxWidth;
	}

	public int getMaxHeight() {
		return this.maxHeight;
	}

	public int getSize() {
		return this.Images.size();
	}

	public Image[] toArrayList() {
		return this.Images.<Image>toArray(new Image[0]);
	}
}