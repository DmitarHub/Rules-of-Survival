package layers.ingame;

import entity.AliveEntity;

public class CollisionCheck {

	private InGameLayer gameLayer;

	public CollisionCheck(InGameLayer game)
	{
		this.gameLayer = game;
	}

	public void checkTile(AliveEntity entity)
	{
		int left = entity.getX() + entity.getHitbox().getOffsetX();
		int right = entity.getX() + entity.getHitbox().getOffsetX() + entity.getHitbox().getHitboxWidth();
		int top = entity.getY() + entity.getHitbox().getOffsetY();
		int bottom = entity.getY() + entity.getHitbox().getOffsetY() + entity.getHitbox().getHitboxHeight();


		int leftCol = left/gameLayer.getTileSize();
		int rightCol = right/gameLayer.getTileSize();
		int topRow = top/gameLayer.getTileSize();
		int bottomRow = bottom/gameLayer.getTileSize();

		switch (entity.getDirection())
		{
        case UP -> topRow = (top - entity.getSpeed()) / gameLayer.getTileSize();
        case DOWN -> bottomRow = (bottom + entity.getSpeed()) / gameLayer.getTileSize();
        case LEFT -> leftCol = (left - entity.getSpeed()) / gameLayer.getTileSize();
        case RIGHT -> rightCol = (right + entity.getSpeed()) / gameLayer.getTileSize();
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
            int tileNum = gameLayer.getTileManager().getMap()[pos[1]][pos[0]];
            if (gameLayer.getTileManager().getTiles()[tileNum].isCollision())
            {
            	entity.setCollisionOn(true);
                break;
            }
        }

	}

	public boolean checkBlockedTile(int y, int x)
	{
		int row = y / gameLayer.getTileSize();
		int column = x / gameLayer.getTileSize();
		int tileNum = gameLayer.getTileManager().getMap()[row][column];
		if(gameLayer.getTileManager().getTiles()[tileNum].isCollision()) return true;

		return false;
	}
}
