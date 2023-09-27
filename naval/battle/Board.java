package naval.battle;

import java.awt.Point;
import java.io.Serializable;

public class Board implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int map[][];

	public Board(int width, int height) {
		map = new int[width][height];
		
		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map[i].length; j++)
				map[i][j] = 1;
	}

	public void addShip(Ship ship) throws NullPointerException {
		for (Point p : ship.getArrayPosition()) {
			map[p.x][p.y] = ship.getId();
		}
	}

	public boolean shipFits(Ship ship) {
		try {
			for (Point p : ship.getArrayPosition()) {
				if (p.x > map.length-1 || p.y > map[0].length-1
						|| map[p.x][p.y] > 1) {
					return false;				
				}
			}
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	public void setPosition(int x, int y, int type) {
		map[x][y] = type;
	}

	public int[][] getMap() {
		return map;
	}

	public int getPosition(int x, int y) {
		return map[x][y];
	}

	public boolean validPosition(int x, int y) {
		return (x < map.length && y < map[0].length
				&& x >= 0 && y >= 0 
				&& map[x][y] > 0);
	}
	
	@Override
	public String toString() {
		String tab = new String();
		
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++)
				tab += String.format("%4d", map[j][i]);
			tab += "\n";
		}
		
		return tab;
	}
}