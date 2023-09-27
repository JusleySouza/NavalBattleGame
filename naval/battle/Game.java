package naval.battle;

import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int EASY = 0;
    public static final int AVERAGE = 1;
    public static final int DIFFICULT = 2;
     
    public static final int POSITIONING_SHIPS = 0;
    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = 2;
    public static final int FINISHED = 3;

    private Player[] players;

    private int difficulties;
    private int state;

    private ArrayList<Event> events;

    public Game(int dif) {
    	players = new Player[2];
    	players[0] = new Player(this);
    	players[1] = new Machine(this);

    	difficulties = dif;

    	events = new ArrayList<Event>();

    	setState(POSITIONING_SHIPS);
    }

    public void setState(int state) {
        if (state == POSITIONING_SHIPS) {
            this.addEvent("Prepare-se! A batalha vai comecar!");
        } else if (state == FINISHED) {
            if (this.getWinner() == players[0])
            	addEvent("A batalha terminou! Voce foi derrotado!");
            else
            	addEvent("A batalha terminou! Voce venceu!");
        }

        this.state = state;
    }

    public Player getPlayer(int i) {
        return players[i];
    }

    public int getDifficulties() {
        return difficulties;
    }

    public int getState() {
        return state;
    }

    public Player getWinner () {
        if (this.state != FINISHED)
            return null;

        return this.players[0].getRemainingShipFleet() == 0
            ? players[1]
            : players[0];           
    }

    public void addEvent (String message) {
        this.events.add(new Event(message));
    }

    public Event getEvent () {
        if (events.size() > 0)
            return events.remove(0);
        else
            return null;
    }
}