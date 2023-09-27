package naval.battle;

import java.awt.Point;
import java.util.ArrayList;

public class Machine extends Player {
	
	private static final long serialVersionUID = 1L;
	
	public static final int HIT_WATER = 0;
	public static final int HIT_SHIP = 1;
	
	public static final int LOST = -1;
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;

	private int state;
	private int direction;
	private Point lastHit;
	private ArrayList<Point> shipsAgreed;
	
	public Machine(Game game) {
		super(game);
		
		direction = LOST;
		shipsAgreed = new ArrayList<Point>();
		
		for (Ship ship : getFleetShips()) {
			positionShip(ship);
		}
	}
	

	public int shoot (){
		if (getGame().getDifficulties()==Game.EASY){
			return super.shoot();
		}else if(getGame().getDifficulties()==Game.AVERAGE){
			int i = (int)(Math.random()*2);

			if (i==1){
				return super.shoot();
			}
		}
		return shootSmart();
	}
	
	
	private void positionShip(Ship n) {
		int x = (int) (Math.random() * 10);
		int y = (int) (Math.random() * 10);
		int orientation = (int) (Math.random() * 2);

		Point pos = new Point(x, y);
		n.setPosition(pos);
		n.setOrientation(orientation);
		if (!getBoard().shipFits(n)) {
			positionShip(n);
		} else{
			getBoard().addShip(n);
		}
	}

	
	private int shootSmart(){
		if (state == 0){			
			int res = super.shoot();
			
			if (res != 1) {
				state = HIT_SHIP;
				lastHit = getShots().get(getShots().size()-1);
				shipsAgreed.add(lastHit);
			}
			return res;
		} else {			
			int y = lastHit.y;
			int x = lastHit.x;
			int attempts = 0;
			
			do {
				if (direction == LOST){
					direction = (int)(Math.random()*4);
				}

				y = lastHit.y;
				x = lastHit.x;
				
				attempts++;
				
				switch (direction) {
				case NORTH:
					y -= 1;				
					break;
				case SOUTH:
					y += 1;				
					break;
				case EAST:
					x += 1;				
					break;
				case WEST:
					x -= 1;				
					break;
				}
				if(!getOpponent().getBoard().validPosition(x, y)){
					if (attempts <= 4){
						direction = (direction + 1) % 4;
					} else if (shipsAgreed.lastIndexOf(lastHit) > 0){
						attempts = 0;
						direction = LOST;
						lastHit = shipsAgreed.get(
								shipsAgreed.lastIndexOf(lastHit)-1 );
					} else {
						state = HIT_WATER;
						return super.shoot();
					}
				}
			} while (!getOpponent().getBoard().validPosition(x, y));
			
			try {
				int res = super.shoot(x, y);
				if (res > 1) {
					if (getOpponent().getShip(res).destroyed()) {
						state = HIT_WATER;
						while (shipsAgreed.size() > 0 && state == HIT_WATER) {
							Point pt = shipsAgreed.get(shipsAgreed.size()-1);
							if (getOpponent().getBoard().getPosition(pt.x, pt.y)
									== -res) {
								shipsAgreed.remove(shipsAgreed.size()-1);
							} else {
								lastHit = pt;
								state = HIT_SHIP;
							}
						}
					} else {
						lastHit = getShots().get(getShots().size()-1);
						shipsAgreed.add(lastHit);
					}
				} else {
					direction = LOST;
				}
				return res;
				
			} catch (Exception e) { return 0; }
		
		}
	}
}