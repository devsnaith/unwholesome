package com.github.devsnaith.unwholesome.io;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import com.github.devsnaith.unwholesome.core.QConsole;

public class QloadImage {
	private static BufferedImage Image;

	private static void LoadImage(String path) {
		try {
			Image = ImageIO.read(QloadImage.class.getResourceAsStream(path));
			QConsole.print(QConsole.Status.INFO,
					String.format("Image in the %s has been loaded", new Object[] { path }));
		} catch (IllegalArgumentException error) {
			QConsole.print(QConsole.Status.SUPER, "can't find image in " + path + ", " + error.getMessage());
		} catch (Exception error) {
			QConsole.print(QConsole.Status.ERROR, "Exception : " + error.getMessage());
		}
	}

	public static BufferedImage convertToBufferedImageObject(Image image, int colorType) {
		BufferedImage bufferedImageObject = new BufferedImage(image.getWidth(null), image.getHeight(null), colorType);
		Graphics gl = bufferedImageObject.getGraphics();
		gl.drawImage(image, 0, 0, null);
		gl.dispose();

		return bufferedImageObject;
	}
	
	public static BufferedImage getLastImage() {
		return Image;
	}

	public static BufferedImage getAllImage(String path) {
		LoadImage(path);
		return Image;
	}
}