package game;

import player.Entity;

public class CollisionCheck {

	private Game game;

	public CollisionCheck(Game game)
	{
		this.game = game;
	}

	public void checkTile(Entity entity)
	{
		int left = entity.getX() + entity.getHitbox().getOffsetX();
		int right = entity.getX() + entity.getHitbox().getOffsetX() + entity.getHitbox().getHitboxWidth();
		int top = entity.getY() + entity.getHitbox().getOffsetY();
		int bottom = entity.getY() + entity.getHitbox().getOffsetY() + entity.getHitbox().getHitboxHeight();


		int leftCol = left/game.getTileSize();
		int rightCol = right/game.getTileSize();
		int topRow = top/game.getTileSize();
		int bottomRow = bottom/game.getTileSize();

		switch (entity.getDirection())
		{
        case UP -> topRow = (top - entity.getSpeed()) / game.getTileSize();
        case DOWN -> bottomRow = (bottom + entity.getSpeed()) / game.getTileSize();
        case LEFT -> leftCol = (left - entity.getSpeed()) / game.getTileSize();
        case RIGHT -> rightCol = (right + entity.getSpeed()) / game.getTileSize();
		}

	    int[][] tilePositions = switch (entity.getDirection())
	    		{
	        case UP -> new int[][]{{leftCol, topRow}, {rightCol, topRow}};
	        case DOWN -> new int[][]{{leftCol, bottomRow}, {rightCol, bottomRow}};
	        case LEFT -> new int[][]{{leftCol, topRow}, {leftCol, bottomRow}};
	        case RIGHT -> new int[][]{{rightCol, topRow}, {rightCol, bottomRow}};
	    };

		for (int[] pos : tilePositions)
		{
            int tileNum = game.getTileManager().getMap()[pos[1]][pos[0]];
            if (game.getTileManager().getTiles()[tileNum].isCollision())
            {
            	entity.setCollisionOn(true);
                break;
            }
        }

	}

	public boolean checkBlockedTile(int row, int column)
	{
		int tileNum = game.getTileManager().getMap()[row][column];
		if(game.getTileManager().getTiles()[tileNum].isCollision()) return true;

		return false;
	}
}
