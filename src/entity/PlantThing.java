package entity;

import java.util.List;

import layers.ingame.InGameLayer;

public class PlantThing extends Enemy {

	private CircleEffect slowEffect;
	
	private double circleSize;
	
	public PlantThing(InGameLayer game, int x, int y, EntityType entityType, int healthPoints,
			List<Player> players, double circleSize) {
		super(game, x, y, 0, entityType, healthPoints, players);
		this.circleSize = circleSize;
		slowEffect = new CircleEffect(game, x, y, circleSize);
	}

	@Override
	public void update()
	{
		updateSpawning();
		updateInvincible();
	}
	
	@Override
	public void takeDamage(int amount)
	{
		if(invincible) return;
		healthPoints -= amount;
		if(healthPoints <= 0)
		{
			this.setDead(true);
			gameLayer.getToBeRemoved().add(slowEffect);
		}
		else {
			invincible = true;
		}
	}
	
	public void setCircleSize(double newSize) { circleSize = newSize; }
	public double getCircleSize() { return circleSize; }

}
