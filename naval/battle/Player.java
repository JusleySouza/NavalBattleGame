package naval.battle;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

import naval.battle.exceptions.PositionAlreadyReachedException;

public class Player implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Game game;
	private Board board;
	private Ship[] fleetShips;

	private ArrayList<Point> shots;

	private int remainingShipFleet;

	public Player(Game jogo) {
		this.game = jogo;
		this.board = new Board(10, 10);
		this.fleetShips = new Ship[4];
			
		this.shots = new ArrayList<Point>();
				
		fleetShips[0] = Ship.buildShip(Ship.PATROL_BOAT, this); //Corveta
		fleetShips[1] = Ship.buildShip(Ship.SUBMARINE, this); //Submarino
		fleetShips[2] = Ship.buildShip(Ship.BATTLEBOAT, this); //Fragata
		fleetShips[3] = Ship.buildShip(Ship.AIRCRAFT_CARRIER, this); //Destroyer
       
        for (int i = 0; i < fleetShips.length; i++)
			this.remainingShipFleet += fleetShips[i].getId();
	}
	
	public int shoot(int column, int line) throws PositionAlreadyReachedException{
		int currentValue = getOpponent().getBoard().getPosition(
				column, line);

		if (currentValue >= 1) {
			shots.add(new Point(column, line));
			getOpponent().getBoard().setPosition(column, line, -currentValue);
			if (currentValue > 1 
					&& getOpponent().getShip(currentValue).destroyed()) {
				getOpponent().destroyShip(currentValue);
			}
		} else {
			throw new PositionAlreadyReachedException();
		}
		return currentValue;
	}

	public int shoot() {
		int width = board.getMap()[0].length;
		int height = board.getMap().length;
		int x = (int)(Math.random()*width);
		int y = (int)(Math.random()*height);

		try {
			return shoot(x, y);
		} catch (Exception e) {
			return shoot();
		}
	}

	public void positionShip(Point pos, int id) {
		getShip(id).setPosition(pos);
		int i = pos.x;
		int j = pos.y;
		int k = 0;

		while(k < getShip(id).getSize()) {
			board.setPosition(pos.x, pos.y, id);
			
			if (getShip(id).getOrientation() == Ship.VERTICAL)
				j++;
			else
				i++;
			k++;			
		}
	}

	private void destroyShip(int id) {
		remainingShipFleet -= id;
		
		game.addEvent( getOpponent() instanceof Machine
                ? "O adversario afundou o seu " + getShip(id).getName().toLowerCase() + "!"
                : "Voce afundou o " + getShip(id).getName().toLowerCase() + " do adversario!" );
		
		if (remainingShipFleet == 0)
			game.setState(Game.FINISHED);
	}

	public Game getGame() {
		return game;
	}

	public Board getBoard() {
		return board;
	}

	public Ship[] getFleetShips() {
		return fleetShips;
	}
	
	public Ship getShip(int id) {
		for (int i = 0; i < fleetShips.length; i++) {
			if (fleetShips[i].getId() == id)
				return fleetShips[i];
		}
		return null;
	}

	public ArrayList<Point> getShots() {
		return shots;
	}

	public int getRemainingShipFleet() {
		return remainingShipFleet;
	}

	public Player getOpponent() {
		return (this == game.getPlayer(0))
				? game.getPlayer(1)
				: game.getPlayer(0);
	}
}
