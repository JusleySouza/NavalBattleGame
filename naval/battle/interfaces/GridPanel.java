package naval.battle.interfaces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import naval.battle.*;
import naval.battle.exceptions.PositionAlreadyReachedException;

@SuppressWarnings("serial")
public class GridPanel extends JPanel {
	public static final int DIM_SQUARE = 34;
	
	private MainWindow mainWindow;
	private Player player;
	private Image backgroundImage;
	private Dimension dimension;
	
	private int idCurrentShip;
	private int currentOrientation;
	private Point currentPosition;
	
	private boolean showShips;

	public GridPanel(MainWindow p, Player j, Image f) {
		mainWindow = p;
		player = j;
		backgroundImage = f;
		dimension = new Dimension(player.getBoard().getMap().length*DIM_SQUARE,
				player.getBoard().getMap()[0].length*DIM_SQUARE);
		setPreferredSize(dimension);

		currentPosition = new Point(0, 0);
		
		if (!(player instanceof Machine)) {
			idCurrentShip = 2;
			currentOrientation = Ship.HORIZONTAL;
			player.getShip(idCurrentShip).setPosition(currentPosition);
			mainWindow.showEvents();
			mainWindow.showEvent("Movimente o navio com o mouse e clique com o " +
                                   "botao esquerdo para posiciona-lo.\n" +
                                   "Para mudar a orientacao, clique com o botao direito.");
		}
		
		MouseHandler tm = new MouseHandler();
		addMouseListener(tm);
		addMouseMotionListener(tm);

		showShips = (player.getOpponent() instanceof Machine ? true : false);
	}

	public void reset(Player j) {
		this.player = j;
		idCurrentShip = 2;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, null);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
		for (int i = 1; i < 11; i++) {
			g.drawLine(i * 30, 0, i * 30, 300);
			g.drawLine(0, i * 30, 300, i * 30);
		}

		for (Ship ship : player.getFleetShips()) {
			if (ship.getPosition() != null)
				if (showShips || ship.destroyed())
					g.drawImage(mainWindow.getImageShip(ship.getId(),
							ship.getOrientation()),
							ship.getPosition().x*30,
							ship.getPosition().y*30, null);
		}
		
		for (Point pt : player.getOpponent().getShots()) {
			int value = player.getBoard().getPosition(pt.x, pt.y);
			if (value == -1) {
				g.drawImage(mainWindow.getImageWater(), pt.x*30, pt.y*30, null);
			} else if (value < 0) {
				g.drawImage(mainWindow.getImageFire(), pt.x*30, pt.y*30, null);
			}
		}
	}

	private class MouseHandler implements MouseListener, MouseMotionListener {
		GridPanel painel = GridPanel.this;
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (!(player instanceof Machine)
					&& player.getGame().getState() == Game.POSITIONING_SHIPS) {
				if ((e.getModifiers() & InputEvent.BUTTON3_MASK)
						== InputEvent.BUTTON3_MASK) {
					int oldOrientation = currentOrientation;
					currentOrientation = (currentOrientation == Ship.VERTICAL
							? Ship.HORIZONTAL
									: Ship.VERTICAL);
					
					player.getShip(idCurrentShip).setOrientation(currentOrientation);
					
					if (!player.getBoard().shipFits(
							player.getShip(idCurrentShip))) {
						player.getShip(idCurrentShip).setOrientation(
								oldOrientation);
					}

					painel.repaint();
				} else if (idCurrentShip <= 16 ) {
					try {
						player.getBoard().addShip(
								player.getShip(idCurrentShip));
						if (idCurrentShip == 16) {
							player.getGame().setState(Game.PLAYER1);
						} else
							idCurrentShip *= 2;
					} catch (NullPointerException npe) {}
				}
			} else if (player.getGame().getState() == Game.PLAYER1
					&& (player instanceof Machine)){
				Point pos = painel.currentPosition;
				
				try {
					int res = player.getOpponent().shoot(pos.x, pos.y);
					painel.repaint();
					if (res == 1) {
						player.getGame().setState(Game.PLAYER2);
						mainWindow.waitingTime();
					} else if ( res > 1){
							if (player.getShip(res).destroyed()) {							
								mainWindow.showEvents();
							}
							if (player.getGame().getState() == Game.FINISHED) {
								mainWindow.showEvents();
							}
					}
				} catch (PositionAlreadyReachedException ex) {
					mainWindow.showEvent(ex.getMessage());
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			Point pos = painel.currentPosition;
			if (pos.x != e.getX()/30 || pos.y != e.getY()/30) {
				currentPosition = new Point(e.getX() / 30, e
						.getY() / 30);
				if (player.getGame().getState() == Game.POSITIONING_SHIPS
						&& !(player instanceof Machine)) {
					Point oldPosition = player.getShip(idCurrentShip).getPosition();
					player.getShip(idCurrentShip).setPosition(currentPosition);
					if (player.getBoard().shipFits(
							player.getShip(idCurrentShip))) {
						painel.repaint();
					} else {
						player.getShip(idCurrentShip).setPosition(oldPosition);
					}
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) { mouseMoved(e); }
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
	}
}