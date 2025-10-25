package player;

import java.util.List;

import layers.ingame.InGameLayer;

public class PlantThing extends Enemy {

	private CircleEffect slowEffect;
	
	public PlantThing(InGameLayer game, int x, int y, EntityType entityType, int healthPoints,
			List<Player> players) {
		super(game, x, y, 0, entityType, healthPoints, players);
		slowEffect = new CircleEffect(game, x, y);
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


}
