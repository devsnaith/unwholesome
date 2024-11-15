package com.github.devsnaith.unwholesome.graphics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class QTileset {
	private ArrayList<BufferedImage> Images;

	public QTileset(int width, int height, BufferedImage tileset) {
		this.Images = new ArrayList<>();
		for (int y = 0; y < tileset.getHeight(); y += height) {
			for (int x = 0; x < tileset.getWidth(); x += width) {
				this.Images.add(tileset.getSubimage(x, y, width, height));
			}
		}
	}
	
	public BufferedImage getDraw(int index) {
		return this.Images.get(index);
	}
	
	public int getSize() {
		return this.Images.size();
	}
}