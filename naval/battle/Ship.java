package naval.battle;

import java.awt.Point;
import java.io.Serializable;

public class Ship implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int PATROL_BOAT = 2; //Corveta
	public static final int SUBMARINE = 4; //Submarino
	public static final int BATTLEBOAT = 8; //Fragata
	public static final int AIRCRAFT_CARRIER = 16; //Destroyer
	

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	private String name;
	private int size;
	private int id;

	private Player player;
	private Point position;
	private int orientation;

	private Ship(String name, int size, int identifier, Player player) {
		this.name = name;
		this.size = size;
		this.id = identifier;

		this.player = player;
		this.position = null;
		this.orientation = HORIZONTAL;
	}

    public static Ship buildShip(int id, Player player) {
		switch (id) {
            case PATROL_BOAT:
                return new Ship("Corveta", 2, 2, player);
            case AIRCRAFT_CARRIER:                                             
                return new Ship("Destroyer", 5, 16, player);
            case SUBMARINE:                                             
                return new Ship("Submarino", 3, 4, player);
            case BATTLEBOAT:                                           
                return new Ship("Fragata", 4, 8, player);
            default:
                return null;
		}
    }

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public int getId() {
		return id;
	}

	public Player getPlayer() {
		return player;
	}

	public Point getPosition() {
		return position;
	}
	
	public Point[] getArrayPosition() throws NullPointerException {
		Point[] arrayPos = new Point[size];
		int i = position.x;
		int j = position.y;
		int k = 0;
		
		while(k < size) {
			arrayPos[k++] = new Point(i, j);
			if (orientation == VERTICAL)
				j++;
			else
				i++;	
		}
		return arrayPos;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setPosition(Point pos) {
		position = pos;
	}
	
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public boolean destroyed() {
		for (Point p: getArrayPosition()) {
			if (player.getBoard().getPosition(p.x, p.y) > 0) {
				return false;
			}
		}
		return true;
	}
}
