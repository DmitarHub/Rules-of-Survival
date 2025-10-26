package layers.ingame;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import tiles.ScaleTool;

public enum Rules {
    SLOWED("SLOWED", "Your speed is decreased for this round!", loadIcon("SLOWED.PNG")),
    FREEZE("FREEZE", "You get frozen for 1 second every 7 seconds!", loadIcon("FREEZE.PNG")),
    ONE_HP("ONE HP", "You are one hit from death!", loadIcon("ONE_HP.PNG")),
    REVERSED_INPUTS("REVERSED INPUTS", "Your keybindings are reversed!", loadIcon("REVERSED_INPUTS.PNG")),
    FASTER_ENEMIES("FASTER ENEMIES", "Enemies are faster this round!", loadIcon("FASTER_ENEMIES.PNG")),
    DISARMED("DISARMED", "You can't attack for 0.45 seconds every 4 seconds!", loadIcon("DISARMED.PNG")),
    WEAKENED_DEFENSE("WEAKENED DEFENSE", "You take 50% more damage this round!", loadIcon("WEAKENED_DEFENSE.PNG")),
    RANDOM_TELEPORT("RANDOM TELEPORT", "You randomly teleport a short distance every 5 seconds!", loadIcon("RANDOM_TELEPORT.PNG")),
    INCREASED_ENEMIES("INCREASED ENEMIES", "This round more enemies will spawn!", loadIcon("INCREASED_ENEMIES.PNG")),
	BIGGER_CIRCLE("BIGGER CIRCLE", "Plant Thing's slow range is bigger!", loadIcon("BIGGER_CIRCLE.PNG"));

	private final String name;
	private final String description;
    private final BufferedImage icon;

    Rules(String name, String description, BufferedImage icon) {
    	this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public BufferedImage getIcon() { return icon; }

    public static BufferedImage loadIcon(String path)
    {
    	try {
    		ScaleTool u = new ScaleTool();

    		BufferedImage icon = ImageIO.read(Rules.class.getResourceAsStream("/rules/" + path));
    		return u.scaleImage(icon, 100, 100);
    	} catch (IOException e) {
    		System.out.println("Greska u citanju fajla");
    		return null;
    	}
    }
}