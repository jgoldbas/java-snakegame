
package snakeframe;

import java.awt.BorderLayout;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;


public class SnakeGame extends JFrame{
	private int[] snake_lengthx = new int[50];
	private int[] snake_lengthy = new int[50];
	private int[] apple_pos = new int[2];

	private BufferedImage snakeheaddown;
	private BufferedImage snakeheadright;
	private BufferedImage snakeheadleft;
	private BufferedImage snakeheadup;
	private BufferedImage snakebody; //segments for snake body
	private BufferedImage apple;
	
	private boolean right = true;
	private boolean left = false;
	private boolean up = false;
	private boolean down = false;
	
	private int lengthofsnake;
	//private int start = 0; // not needed anymore
	
    private Timer timer;
    private int MAX_DELAY = 200;
    private int delay = MAX_DELAY; //how fast the snake moves initially 
    private int STEP_DELAY = 10; // the step to increase the speed
    private int MIN_DELAY = 70; // the maximal speed the snake can go
    
    private int DOT_SIZE = 25;
    private int BOARD_WIDTH = 800;
    private int BOARD_HEIGHT = 725;
    
    //score of player 5 points per win
    private int WIN_SCORE = 5;
    private int player_score = 0;
    
    private Boolean lostGame = false;
    private Boolean restartGame = false;
	
    // global variable that references the Main panel
    private SnakePanel mainPanel;
    
	public SnakeGame() {
	
		super("Snake");
		this.loadImages();
        this.mainPanel = new SnakePanel();
        this.add(this.mainPanel, BorderLayout.CENTER);
    	
        JMenuBar menuBar = new JMenuBar();     
        //setJMenuBar(menuBar);
        menuBar.add(createFileMenu(this));
        //add menu and other panels and buttons
		this.add(menuBar, BorderLayout.NORTH);
	}
	//import images
	public  void loadImages() {
		
		try {
		    snakeheaddown = ImageIO.read(getClass().getClassLoader().getResourceAsStream("./snakeframe/snakeheaddown.png"));
			snakeheadup =ImageIO.read(getClass().getClassLoader().getResourceAsStream("./snakeframe/snakeheadup.png"));
			snakeheadright = ImageIO.read(getClass().getClassLoader().getResourceAsStream("./snakeframe/snakeheadright.png"));
			snakeheadleft = ImageIO.read(getClass().getClassLoader().getResourceAsStream("./snakeframe/snakeheadleft.png"));
			
			snakebody = ImageIO.read(getClass().getClassLoader().getResourceAsStream("./snakeframe/snakebody.png"));
			
			apple = ImageIO.read(getClass().getClassLoader().getResourceAsStream("./snakeframe/apple_julie.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error in uploading images!");
		}
		
	}

	
	public JMenu createFileMenu(SnakeGame frame)
	{
		JMenu menu = new JMenu("File");
		menu.add(createFileNewGameItem(frame));
		menu.add(createFileExitItem());
		return menu;
	}
	
	 /*
    Creates the File->Exit menu item and sets its action listener.
    @return the menu item  */
    public JMenuItem createFileExitItem()
    {
	    JMenuItem item = new JMenuItem("Exit Game");      
	    class MenuItemListener implements ActionListener
	    {
	       public void actionPerformed(ActionEvent event)
	       {
	          System.exit(0); 
	       }
	    }      
	    ActionListener listener = new MenuItemListener();
	    item.addActionListener(listener);
	    return item;
    }
    
    // New Game menu item 
    public JMenuItem createFileNewGameItem(SnakeGame frame)
    {
    	JMenuItem item = new JMenuItem("New Game");      
    	class MenuItemListener implements ActionListener
    	{
    		public void actionPerformed(ActionEvent event)
    		{ 
	    	  //get object of main frame and main panel.
	          //newGame();
	    	  //System.out.println("Before INIT: "+ delay);
	    	  frame.timer.stop();
	    	  frame.mainPanel.newGame();
    		}
    	}      
	    ActionListener listener = new MenuItemListener();
	    item.addActionListener(listener);
	    return item;
    }

    public JMenu createFontMenu()
    {
       JMenu menu = new JMenu("New Game");
      
       return menu;
    }  
	
    
    public class EatAppleThread extends Thread {
    	
    	private void newApple()
    	{	int space = 800/DOT_SIZE;
    		//want new random integer that goes from 0 to 800/applesize
    		int new_x = Math.abs((int)(Math.random() * space));
    		int new_y = Math.abs((int)(Math.random() * space - 4));//menu at the top takes some pixels, 
    		//so we need a smaller random number-> subtract 3 from y coordinate (only)
    		//System.out.println(new_x+ ":"+new_y);
    		apple_pos[0]= new_x * DOT_SIZE;
    		apple_pos[1]= new_y * DOT_SIZE;
    		//System.out.println(apple_pos[0]+ ":"+apple_pos[1]);
    		//System.out.println("------------------------------");
    		
    	}
    	
    	public void run()
    	{
    		newApple();
    	}
    }
    
	
	
	public class SnakePanel extends JPanel implements ActionListener
	{
		public SnakePanel()
		{
			newGame();
			
			addKeyListener(new SnakeKey());
		    setBackground(Color.black);
		    
	        setFocusable(true); //so you don't have to click the window 
		}
		
	private void eatApple()
	{
		//logic to check if the snake ate the apple
		if(snake_lengthx[0] == apple_pos[0] && snake_lengthy[0] == apple_pos[1]) {
			//increase speed
			//make snake longer with each apple eaten
			System.out.println("ATE APPLE!!");
			player_score += WIN_SCORE;
			System.out.println("score so far: " + player_score);
			 //newApple();
			 // Instead of calling eat Apple function, we create an EatAppleThread
			EatAppleThread appleThread = new EatAppleThread();
			appleThread.start();
			//wait until thread is done running to get the new apple coordinates
			while(appleThread.isAlive()) {
				//System.out.println("Thread is running ");
			}
			//System.out.println("Thread Done");
			//appleThread.exit();
			
			 lengthofsnake++;
			 //System.out.println("Before INIT: "+ delay);
			 timer.stop();
			 // calculate new delay
			 if (delay > MIN_DELAY)
			 {
				 delay -= STEP_DELAY;
			 }
			
			 timer = new Timer(delay, this);// new game initiate timer 
		     timer.start();
		}
	}
	
	//this is the endgame where the snake runs into itself 
	private void loseGame() 
	{
		int headx = snake_lengthx[0];
		int heady = snake_lengthy[0];
		for(int i = 1; i < lengthofsnake; i++) {
			if(headx == snake_lengthx[i] && heady == snake_lengthy[i]) {
				System.out.println("YOU LOST!");
				//Show pop up dialog to continue games yes, no:
				timer.stop(); //this freezes the snake where it is currently
				JFrame popupFrame = new JFrame();
				int response = JOptionPane.showConfirmDialog(popupFrame, "Game over! Your score was: " + player_score + ". Do you wish to play again?");
				if(response == 0)
				{
//					System.out.println("0");
					// the response is YES to start a new game:
					restartGame = true;
					break;
				}
				else if (response == 1)
				{ 
					//System.out.println("1");
					// The response is No, we close the game.
					  System.exit(0); 
				}
				else 
				{
					lostGame = true; 
					//repaint();
					break;
				}
			}	
			
		}
	}
	

	
	public void newGame() {
		//player score reset to 0
		player_score = 0;
		// still playing
		lostGame = false;
		restartGame = false;
		//initialize length
		lengthofsnake = 7;
		//initialize vars for snake direction
		right = true;
		up = false;
		left = false;
		down = false;
		// initialize delay to starting speed
		delay = MAX_DELAY;
		//initialize the snake
		initSnake();
		//relocate/create new apple
		//newApple();
		EatAppleThread appleThread = new EatAppleThread();
		appleThread.start();
		//wait until thread is done running to get the new apple coordinates
		while(appleThread.isAlive()) {
			//System.out.println("Thread is running " );
		}
		//System.out.println("Thread Done" );
		timer = new Timer(delay, this);// new game initiate timer 
        timer.start();
        //System.out.println("After init: " + delay);
	}
	
		
	@Override
	public void actionPerformed(ActionEvent arg0) {
			timer.start();
			
			//for snake to move want coordinates of body to go down
			//except for the head, everything else will get shifted one place
			// this way the body moves forward
			////System.out.println(snake_lengthx[0] + snake_lengthx[1] + snake_lengthx[2]);
			
			eatApple();
			loseGame();
			
			if(!lostGame) //"move" the snake
			{
				
			for(int i = lengthofsnake; i > 0; i--) { //every part of the snake except of the head gets shifted 
				snake_lengthx[i] = snake_lengthx[i - 1]; 
				snake_lengthy[i] = snake_lengthy[i - 1]; 
			}
			//System.out.println(snake_lengthx[0] + snake_lengthx[1] + snake_lengthx[2]);
			
			//where the head goes depends on the direction from the key pressed 
			if(right)
			{
				snake_lengthx[0] += DOT_SIZE; 
			}
			if(left) {
				snake_lengthx[0] -= DOT_SIZE;
				
			}
			if(up)
			{
				snake_lengthy[0] -= DOT_SIZE;
			}
			if(down) {
				
				snake_lengthy[0] += DOT_SIZE;
			}
			
			//to check for snake at borders of panel
			if(snake_lengthy[0] > BOARD_HEIGHT) {
				snake_lengthy[0] = 0;
			}
			if(snake_lengthy[0] < 0) {
				snake_lengthy[0] = BOARD_HEIGHT;
			}
			if(snake_lengthx[0] >= BOARD_WIDTH) {
				snake_lengthx[0] = 0;
			}
			if(snake_lengthx[0] < 0) {
				snake_lengthx[0] = BOARD_WIDTH-25;
			
			}
			}
			
			if(restartGame)
			{
				newGame();
			}
			//System.out.println(snake_lengthy[0]);
			repaint();
		}
	@Override
	public void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			paintSnake(g);
			
		}
	
	/*call initSnake every time we start a new game to place the snake in initial position on the board*/
	private void initSnake()
	{
		
		int headx = 125;
		int heady = 400;
		for(int i = 0; i < lengthofsnake; i++)
		{
			snake_lengthx[i] = headx;
			headx -= DOT_SIZE;
			snake_lengthy[i] = heady;
		}
		//newApple();
	}
	
	public void paintSnake (Graphics g) 
	{
	
		g.drawImage(apple, apple_pos[0], apple_pos[1], DOT_SIZE,DOT_SIZE, null);
		
		//add logic about apple
		
		for(int i = lengthofsnake - 1; i >= 0; i--)
		{
			//add logic about the orientation of the head right, up, left, down
			if(i == 0 && down)
			{
				g.drawImage(snakeheaddown, snake_lengthx[0], snake_lengthy[0], DOT_SIZE,DOT_SIZE, null);
			}
			if(i == 0 && right)
			{
				g.drawImage(snakeheadright, snake_lengthx[0], snake_lengthy[0], DOT_SIZE,DOT_SIZE, null);
			}
			if(i == 0 && up)
			{
				g.drawImage(snakeheadup, snake_lengthx[0], snake_lengthy[0], DOT_SIZE,DOT_SIZE, null);
			}
			if(i == 0 && left)
			{
				g.drawImage(snakeheadleft, snake_lengthx[0], snake_lengthy[0], DOT_SIZE,DOT_SIZE, null);
			}
			if(i != 0)
			{
				g.drawImage(snakebody, snake_lengthx[i], snake_lengthy[i], DOT_SIZE,DOT_SIZE, null);
			}
		}
		
		
		if(lostGame)  //if you lost the game paint the score
		{
			// "GAME OVER THIS IS YOUR SCORE"
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.BOLD, 60));
			g.drawString("Game Over", 200, 250);
			
			g.setFont(new Font("Arial", Font.BOLD, 40));
			g.drawString("Your score is: "+ player_score, 200, 350);
			player_score = 0;
		}
		g.dispose();//disposes of graphics component, throw away pointer that gets us this specific g
	}
	}
	 
	private class SnakeKey extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			int key_code = e.getKeyCode();
			switch(key_code) {
			case KeyEvent.VK_RIGHT:
				if(!left)
				{
					right = true;
					left = false;
				}
				else
				{
					right = false;
					left = true;
				}
				up = false;
				down = false;
				break; 
			case KeyEvent.VK_LEFT:
				if(!right)
				{
					left = true;
					right = false;
				}
				else {
					left = false;
					right = true;
				}
				up = false;
				down = false;
				break;
			case KeyEvent.VK_UP:
				if(!down) // if current direction not down
				{
					up = true;
					down = false;
				}
				else {
					up=false;
					down=true;
				}
				left = false;
				right = false;
				
				break;
			case KeyEvent.VK_DOWN:
				if(!up)
				{
					down = true;
					up = false;
				}
				else {
					down = false;
					up = true;
				}
				left = false;
				right = false;
				break;
			
				
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException {

        SnakeGame frame = new  SnakeGame();
        //remember this operation:
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        frame.setVisible(true); 
        frame.setSize(800,800);
  
 
	}
}
