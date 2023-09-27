package naval.battle;

import naval.battle.interfaces.*;

public class Main {
	
	public static void main(String[] args) {
		Game newGame = new Game(Game.AVERAGE);

		MainWindow mainWindow = new MainWindow(newGame);
		mainWindow.pack();
		mainWindow.setVisible(true);
	}
}