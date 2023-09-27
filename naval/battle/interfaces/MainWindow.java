package naval.battle.interfaces;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import naval.battle.*;

import java.awt.Toolkit;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	
	private Game game;
	private int currentDifficulty;

	private Image imageShips[];
	private Image fire, water, background1, background2;
	
	private GridPanel map1;
	private GridPanel map2;
	
	private JMenuBar menuBarGame;

	private JMenu menuGame;
	private JMenuItem newGameItem;
	private JMenuItem openGameItem;
	private JMenuItem saveGameItem;
	private JMenuItem exitGameItem;

	private JMenu levelGameMenu;
	private JRadioButtonMenuItem easyLevelItem;
	private JRadioButtonMenuItem mediumLevelItem;
	private JRadioButtonMenuItem difficultLevelItem;
	private JRadioButtonMenuItem currentLevelItem;
	
	private JMenu shootMenu;
	
	private JTextArea EventsBox;
	
	private JButton startGame;
	private JButton shoot;
	
	private Timer temp;

	public MainWindow(Game game) {
		super("Jogo de Batalha Naval");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/img/PatrolBoatH.png")));
		
		
		JButton startGame = new JButton("Iniciar Jogo");
		startGame.setSize(180, 25);
		startGame.setLocation(70, 322);
		getContentPane().add(startGame);
		startGame.setVisible(true);
		
		JButton shoot = new JButton("Atirar");
		shoot.setSize(180, 25);
		shoot.setLocation(450, 322);
		getContentPane().add(shoot);
		shoot.setVisible(true);
		
		this.game = game;
		this.currentDifficulty = game.getDifficulties();

		getContentPane().setLayout(new BorderLayout());
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		ActionListener makeMove = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (MainWindow.this.game.getState() == Game.PLAYER2) {
					int res = MainWindow.this.game.getPlayer(1).shoot();
					map1.repaint();

					if (res == 1) {
						temp.stop();
						MainWindow.this.game.setState(Game.PLAYER1);
					} else if ( res > 1) {
						if (MainWindow.this.game.getState() == Game.FINISHED) {
							temp.stop();
							showEvents();
						} else if (MainWindow.this.game.getPlayer(
								0 ).getShip(res).destroyed())
							showEvents();
					}
				}
			}
		};
		
		temp =  new Timer(1000, makeMove);
		
		openImages();
		addEventsBox();
		addGrids();
		addMenus();
	}

	private void openImages() {
		imageShips = new Image[10];
		String file1[] = new String[]
		        {"water", "fire", "sea1", "sea2"};
		String file2[] = new String[]
		        {"PatrolBoat", "Destroier", "Submarine",
				 "BattleBoat", "AircraftCarrier"};
		try {
			water = ImageIO.read(getClass().getClassLoader().getResource("img/"
					+ file1[0] + ".png"));
			fire = ImageIO.read(getClass().getClassLoader().getResource("img/"
					+ file1[1] + ".png"));
			background1 = ImageIO.read(getClass().getClassLoader().getResource("img/"
					+ file1[2] + ".png"));
			background2 = ImageIO.read(getClass().getClassLoader().getResource("img/"
					+ file1[3] + ".png"));
			for (int i=0; i<10; i++) {
				imageShips[i] = ImageIO.read(
					getClass().getClassLoader().getResource("img/"
							+ (i > 4
									? file2[i-5] + "V"
											: file2[i] + "H") + ".png"));
			}
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(0);
		}
	}

	private void addGrids() {
		JPanel maps = new JPanel(new GridLayout(1, 2, 30, 10));
		maps.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		map1 = new GridPanel(this,
				this.game.getPlayer(0), background1);
		map2 = new GridPanel(this,
				this.game.getPlayer(1), background2);
		
		maps.add(map1);
		maps.add(map2);
		getContentPane().add(maps, BorderLayout.NORTH);
	}

	private void addMenus() {
		menuGame = new JMenu("Menu");
		menuGame.setMnemonic('J');

		ActionsHandler ta = new ActionsHandler();
		newGameItem = new JMenuItem("Novo");
		newGameItem.setMnemonic('N');
		openGameItem = new JMenuItem("Abrir...");
		openGameItem.setMnemonic('A');
		saveGameItem = new JMenuItem("Salvar");
		saveGameItem.setMnemonic('S');
		exitGameItem = new JMenuItem("Sair");
		exitGameItem.setMnemonic('R');
		
		newGameItem.addActionListener(ta);
		openGameItem.addActionListener(ta);
		saveGameItem.addActionListener(ta);
		exitGameItem.addActionListener(ta);

		levelGameMenu = new JMenu("Nivel");
		levelGameMenu.setMnemonic('N');

		easyLevelItem = new JRadioButtonMenuItem("Facil");
		easyLevelItem.setMnemonic('F');
		easyLevelItem.addActionListener(ta);
		mediumLevelItem = new JRadioButtonMenuItem("Medio");
		mediumLevelItem.setMnemonic('M');
		mediumLevelItem.addActionListener(ta);
		mediumLevelItem.setSelected(true);
		difficultLevelItem = new JRadioButtonMenuItem("Dificil");
		difficultLevelItem.setMnemonic('D');
		difficultLevelItem.addActionListener(ta);
		
		currentLevelItem = easyLevelItem;

		ButtonGroup levelGroup = new ButtonGroup();
		levelGroup.add(easyLevelItem);
		levelGroup.add(mediumLevelItem);
		levelGroup.add(difficultLevelItem);

		levelGameMenu.add(easyLevelItem);
		levelGameMenu.add(mediumLevelItem);
		levelGameMenu.add(difficultLevelItem);

		menuGame.add(newGameItem);
		menuGame.add(openGameItem);
		menuGame.add(saveGameItem);
		menuGame.add(levelGameMenu);
		menuGame.addSeparator();
		menuGame.add(exitGameItem);

		menuBarGame = new JMenuBar();
		menuBarGame.add(menuGame);
		setJMenuBar(menuBarGame);
	}

	public Image getImageFire () {
		return fire;
	}
	
	public Image getImageWater() {
		return water;
	}

		public Image getImageShip(int id, int or) {
		switch (id) {
		case Ship.PATROL_BOAT: //Corveta
			return (or == Ship.VERTICAL ? imageShips[5] : imageShips[0]);
		case Ship.SUBMARINE:	//Submarino                             
			return (or == Ship.VERTICAL ? imageShips[7] : imageShips[2]);
		case Ship.BATTLEBOAT: //Fragata                                  
			return (or == Ship.VERTICAL ? imageShips[8] : imageShips[3]);
		case Ship.AIRCRAFT_CARRIER: //Destroyer                                   
			return (or == Ship.VERTICAL ? imageShips[9] : imageShips[4]);
		default:
			return null;
		}
	}

	public void updateGrid() {
		map1.repaint();
		map2.repaint();
	}

	private void addEventsBox() {

		JPanel eventsPanel = new JPanel(new GridLayout(1, 1));
		eventsPanel.setPreferredSize(new Dimension(630, 150));
		eventsPanel
				.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		EventsBox = new JTextArea();
		JScrollPane scrollingEvents = new JScrollPane(EventsBox);
		EventsBox.setEditable(false);

		eventsPanel.add(scrollingEvents);
		getContentPane().add(eventsPanel, BorderLayout.SOUTH);
	}

	public void showEvent (String msg) {
		EventsBox.append("> " + msg + "\n");
		EventsBox.setCaretPosition(EventsBox.getDocument().getLength() );		
	}

    public void showEvents () {
        Event e = game.getEvent();

        while (e != null) {
        	showEvent(e.getMensagem());

            e = game.getEvent();
        }
    }

	private class ActionsHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();

			if (src == exitGameItem) {
				System.exit(0);
			} else if (src == newGameItem) {
				game = new Game(currentDifficulty);
				showEvent("Nova batalha iniciada!");
				map1.reset(game.getPlayer(0));
				map2.reset(game.getPlayer(1));
			} else if (src == saveGameItem) {
				try {
					ObjectOutputStream exit = new ObjectOutputStream(
							new FileOutputStream("data" + File.separator + "salvedgames.dat"));
					exit.writeObject(game);
					exit.close();
				} catch (FileNotFoundException e1) {
					showEvent("Arquivo nao encontrado!");
					e1.printStackTrace();
				} catch (IOException e1) {
					showEvent("Nao foi possivel salvar!");
					e1.printStackTrace();
				}
				showEvent ("Jogo salvo com sucesso!");
			}else if (src == openGameItem) {
				ObjectInputStream inputGame;
				try {
					inputGame = new ObjectInputStream(
							new FileInputStream("data" + File.separator + "salvedgames.dat"));
					game = (Game)inputGame.readObject();
					
					if (game.getDifficulties() == Game.EASY) {
						easyLevelItem.setSelected(true);
						currentDifficulty = Game.EASY;
					} else if (game.getDifficulties() == Game.AVERAGE) {
						mediumLevelItem.setSelected(true);
						currentDifficulty = Game.AVERAGE;
					} else {
						difficultLevelItem.setSelected(true);
						currentDifficulty = Game.DIFFICULT;
					}
					
					map1.reset(game.getPlayer(0));
					map2.reset(game.getPlayer(1));
					showEvent ("Jogo aberto com sucesso!");
					inputGame.close();
				} catch (FileNotFoundException e1) {
					showEvent("Arquivo nao encontrado!");
					e1.printStackTrace();
				} catch (IOException e1) {
					showEvent("Nao foi possivel salvar!");
					e1.printStackTrace();
				} catch (ClassNotFoundException e2) {
					e2.printStackTrace();
				}
			} else if (src instanceof JRadioButtonMenuItem
					&& src != currentLevelItem) {
				JOptionPane.showMessageDialog(null,
						"O grau de dificuldade sera modificado no proximo jogo.",
						"Dificuldade", JOptionPane.INFORMATION_MESSAGE);
				currentDifficulty = (src == easyLevelItem ? Game.EASY
						: (src == mediumLevelItem ? Game.AVERAGE
								: Game.DIFFICULT));
				currentLevelItem = (JRadioButtonMenuItem)src;
			}
		}
	}
	
	public void waitingTime (){
		temp.start();
	}
}
