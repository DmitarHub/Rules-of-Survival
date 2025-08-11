package tiles;

import java.awt.Image;

public class Tile {

	private Image image;
	private boolean collision;
	private String name;

	public Image getImage() {
		return image;
	}
	public void setImage(Image scaled) {
		this.image = scaled;
	}
	public boolean isCollision() {
		return collision;
	}
	public void setCollision(boolean collision) {
		this.collision = collision;
	}

	public String getName()
	{
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
