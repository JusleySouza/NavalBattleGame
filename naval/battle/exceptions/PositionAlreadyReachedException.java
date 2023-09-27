package naval.battle.exceptions;

@SuppressWarnings("serial")
public class PositionAlreadyReachedException extends Exception {

	public PositionAlreadyReachedException() {
		super("Posicao ja atingida!");
	}
}