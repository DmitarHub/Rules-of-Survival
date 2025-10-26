package entity;

import java.util.List;

import layers.ingame.InGameLayer;

public class Pirate extends Enemy {
	
	private int firingCooldown = 210;
	private int cooldownCounter = 0;
	private Player targetPlayer;

	public Pirate(InGameLayer game, int x, int y, EntityType entityType, int healthPoints, List<Player> players) {
		super(game, x, y, 0, entityType, healthPoints, players);
		cooldownCounter = (int) (Math.random() * firingCooldown);
		spriteCounter = cooldownCounter % 30;
		spriteNumber = (cooldownCounter / 30) % 2 + 1;
		int direCnt = (cooldownCounter / 60);
		Direction[] directions = Direction.values();
		direction = directions[(direCnt + 1) % directions.length];
	}

	
	
	@Override
	public void update()
	{

		updateSpawning();
		updateInvincible();
		
		if(!spawning && !dead)
		{
			handleFiringCycle();
		}

		animationCounter();
	}
	
	@Override
	public void animationCounter()
	{
		if(++spriteCounter > 30)
		{
			spriteNumber++;
			spriteCounter = 0; 
			if(spriteNumber == 3)
			{
				spriteNumber = 1;
				Direction[] directions = Direction.values();
				direction = directions[(direction.ordinal() + 1) % directions.length];
			}
		}
	}
	
	public void handleFiringCycle()
	{
		if(++cooldownCounter >= firingCooldown)
		{
			cooldownCounter = 0;
			chooseTarget();
			if(targetPlayer != null)
			{
				gameLayer.getRocketMarkers().add(new RocketMarker(targetPlayer.getX(), targetPlayer.getY(), gameLayer.getTileSize(), gameLayer, this));
			}
		}
	}


	private void chooseTarget()
	{
		int minDistance = Integer.MAX_VALUE;
		for(Player p : players)
		{
			int distance = (int)getDistanceToPlayer(p);
			if(minDistance > distance)
			{
				minDistance = distance;
				targetPlayer = p;
			}
		}
	}




}
