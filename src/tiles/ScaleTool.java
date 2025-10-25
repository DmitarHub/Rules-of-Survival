package tiles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ScaleTool {

	public BufferedImage scaleImage(BufferedImage original, int x, int y) {
		BufferedImage scaledImage = new BufferedImage(x,y,original.getType());
		Graphics2D graphics = scaledImage.createGraphics();
		graphics.drawImage(original,0,0,x,y,null);
		graphics.dispose();

		return scaledImage;
	}
}