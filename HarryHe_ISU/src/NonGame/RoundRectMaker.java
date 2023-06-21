package NonGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class RoundRectMaker {
	public static void main(String[] args) {
		try {
			int width = 440, height = 484;
			int border = 4;
			int roundAmount = 30;
			BufferedImage bufferedImage = new BufferedImage(width,  height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graph = bufferedImage.createGraphics();
//			graph.setColor(new Color(237, 163, 59));
//			graph.setColor(new Color(95, 193, 250));
//			graph.setColor(new Color(214, 48, 26));
			graph.setColor(new Color(47, 47, 47));
			graph.fillRoundRect(border, border, width - 2 * border - 1, height - 2 * border - 1, roundAmount, roundAmount);
			graph.setColor(new Color(255, 255, 255));
//			graph.setColor(new Color(105, 105, 105));
			graph.setStroke(new BasicStroke(8));
			graph.drawRoundRect(border, border, width - 2 * border - 1, height - 2 * border - 1, roundAmount, roundAmount);
			graph.dispose();

			ImageIO.write(bufferedImage, "png", new File("zoutput/roundrect.png"));

		} catch (Exception exc) {
			System.out.println("Interrupted: " + exc.getMessage());
			exc.printStackTrace();
		}
	}
}
