package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import layers.ingame.InGameLayer;

import java.util.HashSet;
import java.util.LinkedList;

public class PathFinder {


	private InGameLayer gameLayer;
	private int deltaMove;
	
	public PathFinder(InGameLayer game, int deltaMove)
	{
		this.gameLayer = game;
		this.deltaMove = deltaMove;
	}

	public List<int[]> findPath(int startY, int startX, int goalY, int goalX) {
	    HashSet<String> visited = new HashSet<>();
	    List<int[]> path = new ArrayList<>();
	    Queue<PathNode> queue = new LinkedList<>();

	    queue.add(new PathNode(startY, startX));
	    Map<String, String> parent = new HashMap<>();

	    while (!queue.isEmpty()) {
	        PathNode current = queue.poll();
	        int cy = current.getRow();
	        int cx = current.getColumn();
	        if (visited.contains(cy + "," + cx)) continue;

	        if (Math.abs(cx - goalX) < deltaMove && Math.abs(cy - goalY) < deltaMove) {
	            path.add(new int[]{cy, cx});
	            String key = cy + "," + cx;
	            while (parent.containsKey(key)) {
	                String[] p = parent.get(key).split(",");
	                int py = Integer.parseInt(p[0]);
	                int px = Integer.parseInt(p[1]);
	                path.add(new int[]{py, px});
	                key = parent.get(key);
	            }
	            Collections.reverse(path);
	            return path;
	        }

	        visited.add(cy + "," + cx);

	        for (int[] dir : directionDelta()) {
	            int ny = cy + dir[0] * deltaMove;
	            int nx = cx + dir[1] * deltaMove;

	            if (visited.contains(ny + "," + nx)) continue;
	            if (gameLayer.getCollisionCheck().checkBlockedTile(ny, nx)) continue;
	            parent.put(ny + "," + nx, cy + "," + cx);
	            queue.add(new PathNode(ny, nx));
	        }
	    }
	    return Collections.emptyList();
	}

//	private int getHeuristic(int startRow, int startCol, int goalRow, int goalCol)
//	{
//		return (int)Math.sqrt(Math.pow((startCol - goalCol), 2) + Math.pow((startRow - goalRow), 2));
//	}

//	private int search(PathNode pathNode, int goalCol, int goalRow, int gCost,
//			int threshold, HashSet<String> visited, List<int[]> path, int maxDepth)
//	{
//
//
//		if(Math.abs(pathNode.getColumn() - goalCol) < deltaMove && Math.abs(pathNode.getRow() - goalRow) < deltaMove)
//		{
//			path.add(new int[]{pathNode.getRow(), pathNode.getColumn()});
//			return -1;
//		}
//
//		pathNode.setgCost(gCost);
//		pathNode.sethCost(getHeuristic(pathNode.getRow(), pathNode.getColumn(), goalRow, goalCol));
//		pathNode.setfCost(pathNode.getgCost() + pathNode.gethCost());
//
//		if(pathNode.getgCost() > maxDepth) return Integer.MAX_VALUE;
//
//		if(pathNode.getfCost() > threshold)
//		{
//			return pathNode.getfCost();
//		}
//
//		visited.add(pathNode.getRow() + ", " + pathNode.getColumn());
//
//		int min = Integer.MAX_VALUE;
//
//		for(int[] directions : directionDelta())
//		{
//				int newY = pathNode.getRow() + directions[0] * deltaMove;
//				int newX = pathNode.getColumn() + directions[1] * deltaMove;
//				if(gameLayer.getCollisionCheck().checkBlockedTile(newY, newX)) continue;
//				if(visited.contains(newY + ", " + newX)) continue;
//				PathNode child = new PathNode(newY, newX);
//
//				int searchResult = search(child, goalCol, goalRow, gCost + 1, threshold, visited, path, maxDepth);
//
//				if(searchResult == -1)
//				{
//					path.add(new int[]{child.getRow(), child.getColumn()});
//					return -1;
//				}
//				if(min > searchResult) min = searchResult;
//		}
//
//		return min;
//	}
	
	private int[][] directionDelta()
	{
		return new int[][] {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
	}

}
