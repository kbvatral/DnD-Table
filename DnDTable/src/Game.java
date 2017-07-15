import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Game extends JFrame implements MouseListener, MouseMotionListener, ActionListener, ComponentListener
{
	//================================================================================
	// Variable Declarations
	//================================================================================

	/**
	 * The panel which controls z positioning of elements.
	 * It is a child of outerPane in the CENTER placement.
	 * layeredPane holds the major and minor grids and all grid elements,
	 * as well as handling drag events
	 */
	FilledLayeredPane layeredPane;
	/**
	 * The outermost layer which is setup using borderLayout.
	 * outerPane holds all other components on screen.
	 */
	JPanel outerPane;
	/**
	 * The panel which hold the grid squares.
	 * gameBoard is a child of layeredPane at z-depth of majorGrid
	 * This layer is setup using gridlayout to contain the squares.
	 * Its children are used for positioning of grid locked elements.
	 */
	JPanel gameBoard;
	/**
	 * The panel to hold the minor grid
	 * minorGrid is a child of layeredPane at the DEFAULT_LAYER.
	 * It is setup using gridLayout to contain the squares and
	 * its children are used for positioning absolute elements
	 */
	JPanel minorGrid;
	/**
	 * The panel to hold all the piece elements.
	 * Pieces are removed from this panel when being dragged.
	 * pieceLayer is a child of layeredPane at the MODAL_LAYER.
	 * It is setup using null layout, but positions for its children are determined
	 * from the major and minor grids
	 */
	JPanel pieceLayer;
	/**
	 * The bottom panel which houses the global add buttons.
	 * addPanel is a child of outerPane in the SOUTH placement
	 */
	JPanel addPanel;
	/**
	 * The right panel to display info about the currently selected element.
	 * infoPanel is a child of outerPane in the EAST placement.
	 * It is setup using cardLayout to allow a new card to be displayed for each selected element
	 */
	JPanel infoPanel;
	/**
	 * The panel in which elements are placed while being dragged.
	 * dragPanel is a child of layeredPane at the DRAG_LAYER.
	 * It is setup using null layout.
	 * It is utilized to fill the DRAG_LAYER and allow elements to be absolutely positioned.
	 */
	JPanel dragPanel;

	/**
	 * Integer which sets a z layer value for the major grid
	 * It is defined 5 higher than the DEFAULT_LAYER which houses the minor grid
	 */
	public static final Integer majorGrid = JLayeredPane.DEFAULT_LAYER+5;

	int rows = 10; //size of the grid
	int columns = 10; //we are going to take user input for these later
	/**
	 * An ArayList of JPanels which store the major grid squares.
	 * Each square is a child of gameBoard
	 */
	ArrayList <JPanel> squares; //JPanel arraylist to hold the square positions
								//We use and arraylist so that the user can change its size later
	double grid_size_x;
	double grid_size_y;

	int minor_grid_num = 100; //size of the minor grid (must be perfect square)
							 //this sets the resolution of absolute positioning
	ArrayList <minorSquare> minSquares; //JPanel arraylist to hold the minor grid square positions

	ArrayList <Piece> pieces;
	Piece tempPiece; //a temporary storage variable for drag events
	int xAdjustment; //int to store mouse movements
	int yAdjustment; //int to store mouse movements

	CardLayout infoCards;
	Component selectedPiece; //a temporary storage variable for the selected piece
	infoCard defaultCard; //an empty panel to put in the infoPanel when no piece is selected
	ArrayList <infoCard> cards; //an arraylist to hold the cards for each piece
	int idCounter; //a counter to help with generation of unique id's for pieces and cards

	JButton addPiece; //add button
	JButton savePiece; //save button

	JMenuBar menubar;
	JMenu fileMenu;
	JMenu addMenu;
	JMenuItem addPieceItem;
	JMenuItem savePieceItem;

     public Game()
        {
    	 //================================================================================
		 // Initializations
		 //================================================================================

    	 setUIFont(new FontUIResource(new Font("MS Mincho",Font.BOLD, 18))); //set the ui font
    	 //tryNimbus(); //attempt to apply the nimbus look and feel

    	 /// outerPane ///
    	 outerPane = new JPanel();
    	 outerPane.setLayout(new BorderLayout());
    	 outerPane.addComponentListener(this);
    	 getContentPane().add(outerPane); //add it to the frame

    	 /// layeredPane ///
    	 layeredPane = new FilledLayeredPane();
    	 layeredPane.addMouseListener(this);
    	 layeredPane.addMouseMotionListener(this);
    	 outerPane.add(layeredPane,BorderLayout.CENTER); //we add it to the center of our main panel

    	 /// gameBoard ///
    	 gameBoard = new JPanel();
    	 gameBoard.setLayout( new GridLayout(rows, columns, 0, 0) ); //Columns, rows, spacing, spacing
    	 //gameBoard.setBackground(Color.WHITE); //this allows a specification of the color behind the grid
    	 gameBoard.setOpaque(false);
    	 gameBoard.setBorder(BorderFactory.createLineBorder(Color.black));
    	 layeredPane.add(gameBoard); //add the gameBoard to the center of our main layer
    	 layeredPane.setLayer(gameBoard, majorGrid); //five z above the default layer
    	 squares = new ArrayList <JPanel>(); //an arraylist to store the board squares
    	 									 //we use arraylist to allow the user to change the grid size later

    	 minSquares = new ArrayList <minorSquare>(); //an arraylist to store the minor grid squares

    	 /// For Loop ///
    	 // We are looping across a grid of size defines by rows and columns and
    	 // inserting that many JPanels to be used as grid positions
    	 ////////////////
    	 for(int i=0;i<rows*columns;i++){
    		 int min_sqrt = (int) Math.sqrt(minor_grid_num); //setup rows and columns for minor grid
    		 squares.add(new JPanel(new GridLayout(min_sqrt,min_sqrt,0,0))); //add a panel to the array
    		 											  //we instantiate with gridlayout to make the minor grid
    		 squares.get(i).setBorder(BorderFactory.createLineBorder(Color.black)); //create the borders
    		 squares.get(i).setOpaque(false); //allows for a background behind the grid
    		 gameBoard.add(squares.get(i)); //add the panel to the board

    		 /// For Loop ///
        	 // We are looping across a grid of size defines by minor_grid_num and
        	 // inserting that many JPanels to be used as minor grid positions
        	 ////////////////
    		 for(int j=0;j<minor_grid_num;j++){
    			 minSquares.add(new minorSquare(new BorderLayout())); //add a panel to the array
    			 												//we instantiate with borderLayout to make the piece go to the center of the panel
    			 //minSquares.get(i*minor_grid_num+j).setBorder(BorderFactory.createLineBorder(Color.red)); //create the borders
    			 minSquares.get(i*minor_grid_num+j).setOpaque(false);
    			 squares.get(i).add(minSquares.get(i*minor_grid_num+j)); //add the panel to the board
    		 }
    	 }

    	 /// pieceLayer ///
    	 pieceLayer = new JPanel();
    	 pieceLayer.setOpaque(false);
    	 pieceLayer.setLayout(null);
    	 layeredPane.add(pieceLayer);
    	 layeredPane.setLayer(pieceLayer, JLayeredPane.MODAL_LAYER);
    	 pieces = new ArrayList<Piece>(); //initialize the array of pieces

    	 /// addPanel ///
    	 addPanel = new JPanel();
    	 outerPane.add(addPanel,BorderLayout.SOUTH);

    	 addPiece = new JButton("Add Piece");
    	 addPiece.setActionCommand("addPiece"); //we use this string to reference the button in action handler
    	 addPiece.addActionListener(this);
    	 savePiece = new JButton("Save Piece");
    	 savePiece.setActionCommand("savePiece");
    	 savePiece.addActionListener(this);

    	 addPanel.add(addPiece);
    	 addPanel.add(savePiece);

    	 /// infoPanel ///
    	 infoCards = new CardLayout(); //define our cardlayout
    	 infoPanel = new JPanel(infoCards); //initialize the panel
    	 outerPane.add(infoPanel, BorderLayout.EAST); //add it to the side of the main panel

    	 defaultCard = new infoCard(-1);
    	 defaultCard.setLayout(new VerticalLayout());
    	 JLabel title = new JLabel("Global Options");
    	 title.setBorder(new EmptyBorder(0, 0, 20, 0));
    	 defaultCard.add(title);
    	 JCheckBox showGrid = new JCheckBox("Show Grid",true);
    	 showGrid.setActionCommand("showGrid");
    	 showGrid.addActionListener(this);
    	 defaultCard.add(showGrid);


    	 infoPanel.add(defaultCard, Integer.toString(-1)); //we will reference the default card by "-1"
    	 infoPanel.setPreferredSize(new Dimension(300,500));
    	 cards = new ArrayList<infoCard>();
    	 idCounter = 0;

    	 /// dragPanel ///
    	 dragPanel = new JPanel();
    	 dragPanel.setLayout(null);
    	 layeredPane.add(dragPanel);
    	 layeredPane.setLayer(pieceLayer, JLayeredPane.DRAG_LAYER);

    	 /// menuBar ///
    	 // This is the main menubar for the GUI
    	 menubar = new JMenuBar();
    	 fileMenu = new JMenu("File");

    	 addPieceItem = new JMenuItem("Add Piece");
    	 addPieceItem.addActionListener(this);
    	 addPieceItem.setActionCommand("addPiece");
    	 savePieceItem = new JMenuItem("Save Piece");
    	 savePieceItem.addActionListener(this);
    	 savePieceItem.setActionCommand("savePiece");

    	 menubar.add(fileMenu);
    	 fileMenu.add(addPieceItem);
    	 fileMenu.add(savePieceItem);
    	 setJMenuBar(menubar);

    	 grid_size_x = gameBoard.getWidth();
    	 grid_size_y = gameBoard.getHeight();

        }
     private static void setUIFont(FontUIResource f) //method to set the font for the panel
     {												 //I found this on the internet
         java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
         while (keys.hasMoreElements())
         {
             Object key = keys.nextElement();
             Object value = UIManager.get(key);
             if (value instanceof FontUIResource)
             {
                 UIManager.put(key, f);
             }
         }
     }
     private static void tryNimbus(){
    	 try {
    	     for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    	         if ("Nimbus".equals(info.getName())) {
    	             UIManager.setLookAndFeel(info.getClassName());
    	             break;
    	         }
    	     }
    	 } catch (Exception e) {
    	     // If Nimbus is not available, you can set the GUI to another look and feel.
    	 }
     }

     //================================================================================
	 // Action Listener
	 //================================================================================

     public void actionPerformed(ActionEvent e) { //handles button events
    	    if ("addPiece".equals(e.getActionCommand())) { //add button
    	    	addPiece();
    	    }else if ("savePiece".equals(e.getActionCommand())) { //scale button
    	        //addPiece_old();
    	    }else if("gridLocked".equals(e.getActionCommand())){ //grid locked checkbox
    	    	toggleGridLocked(e);
    	    }else if("showGrid".equals(e.getActionCommand())){
    	    	showGrid(e);
    	    }
    	}

     //================================================================================
	 // Global Actions
	 //================================================================================

     /**
      * A method which receives an action event from the showGrid JCheckbox and then toggles the grid on and off
      * @param e - The action event associated with a toggle of the showGrid JCheckBox
      */
     public void showGrid(ActionEvent e){
    	 JCheckBox c = (JCheckBox)e.getSource(); //get the check box source
    	 //loop through all the grid squares
    	 for(int i=0;i<squares.size();i++){
    		 if(c.isSelected()){ //if we are showing, then draw the borders
    			 squares.get(i).setBorder(BorderFactory.createLineBorder(Color.black));
    		 }else{ //if not, then clear the borders
    			 squares.get(i).setBorder(null);
    		 }
    	 }
    	 gameBoard.repaint();
     }

     //================================================================================
	 // infoCard Actions
	 //================================================================================

     /**
      * A method which receives an action event and then toggles the grid lock for the piece associated with that action
      * @param e - An action event caused by a JCheckbox toggle
      */
     public void toggleGridLocked(ActionEvent e){
    	    Component c = (Component)e.getSource(); //cast the source to a component, should be a JCheckBox
	    	infoCard info = (infoCard)c.getParent(); //the parent of the check box is the infoCard
	    	/// For Loop ///
	    	//Loop through all the pieces until we find the one that matches the id of the infoCard
	    	//When we find it, change the value of grid locked for the piece with that id
	    	for(int i=0;i<pieces.size();i++){
	    		if(pieces.get(i).getCardId() == info.getAssociatedID()){ //have we found a match?
	    			if(pieces.get(i).getGridLocked()){		//if it is already grid locked
	    				pieces.get(i).setGridLocked(false); //set it to unlocked
	    				break;
	    			}else{
	    				pieces.get(i).setGridLocked(true);  //otherwise set it to locked
	    				break;
	    			}
	    		}
	    	}
     }

     //================================================================================
	 // Component Listener
	 //================================================================================

     public void componentResized(ComponentEvent e){
    	 scale_Pieces();
    	 relocate_pieces();
     }

     public void componentHidden(ComponentEvent e){}
     public void componentMoved(ComponentEvent e){}
     public void componentShown(ComponentEvent e){}

     //================================================================================
	 // Piece Methods
	 //================================================================================

     /**
      * Method to open the dialog to add a new piece.
      * The dialog is created with a JOptionPane containing a create_piece object.
      */
     public void addPiece(){

    	 create_piece dialog = new create_piece();
    	 int result = JOptionPane.showConfirmDialog(this, dialog, "Add New Piece", JOptionPane.PLAIN_MESSAGE);

    	 if (result == JOptionPane.OK_OPTION) {
    		 if(dialog.getFile() == null){ //check if the user selected a file
    			 JOptionPane.showMessageDialog(this, "No file was selected!", "Error", JOptionPane.ERROR_MESSAGE);
    		 }else{
	    	     try{
	    	    	 if(dialog.getSelection() == "Open Saved Piece    "){ //open a piece
	    	    		 System.out.println("Opening...");
	    	    	 }else{ //create a new piece
	    	    		 if(dialog.getName().trim().equals("")){ //check if the user entered a name
	    	    			 JOptionPane.showMessageDialog(this, "No name was entered!", "Error", JOptionPane.ERROR_MESSAGE);
	    	    		 }else{ //there were no errors
			    	    	 newPiece(dialog); //pass along the dialog to create the new piece
		    	    	}
	    	    	 }
	    	     }catch(Exception e){
	    	    	 JOptionPane.showMessageDialog(this, "Sorry, there was an unexpected error", "Error", JOptionPane.ERROR_MESSAGE);
	    	     }
    		 }
    	 }

     }
     /**
      * A method to create the new piece and add it to the board
      * @param dialog - A create_piece that is passed in when a piece is being created
      */
     public void newPiece(create_piece dialog){
    	 try{
	    	 File file = dialog.getFile(); //user selected file
	    	 String path = System.getProperty("user.dir");
	    	 path = path + "/bin/Resources/Images/" + file.getName(); //create path for resource
	    	 File new_file = new File(path); //create a file in the new location
	    	 Files.copy(file.toPath(), new_file.toPath(), StandardCopyOption.REPLACE_EXISTING); //copy existing file to new file

	    	 /// Create the Icon ///

	    	 JPanel panel = (JPanel)gameBoard.getComponent(0); //get the square where we want it
	    	 double panelHeight = (double)panel.getHeight(); //height of a grid square
	    	 double panelWidth = (double)panel.getWidth(); //width of a grid square

	    	 URL url = new_file.toURI().toURL(); //image location

	    	 ImageIcon pieceImage = new ImageIcon(url); //get the original image
	    	 double image_Width = (double)pieceImage.getIconWidth(); //find the width of the original
	    	 double image_Height = (double)pieceImage.getIconHeight(); //find the height of the original

	    	 //scale calculations
	    	 double scale_width = panelWidth/image_Width; //scale percentage for width and height
	    	 double scale_height = panelHeight/image_Height;
	    	 int newWidth = (int)Math.floor(image_Width*Math.min(scale_width,scale_height)); //we scale the image by a factor of whichever is smaller so that the whole thing will fit in a box
	    	 int newHeight = (int)Math.floor(image_Height*Math.min(scale_width,scale_height));

	    	 Piece new_piece = new Piece(new ImageIcon(pieceImage.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)),url); //pieces extend JLabel
	    	 idCounter++; //increment the idCounter by 1 for each created piece
	    	 new_piece.setCardId(idCounter); //set the piece's unique id to the counter

	    	 Point parentLocation = panel.getLocation(); //find location of square holding piece
	    	 new_piece.setLocation(parentLocation); //set the piece at that location
	    	 new_piece.setSize(newWidth, newHeight);
	    	 pieceLayer.add(new_piece); //add the piece
	    	 pieces.add(new_piece); //add the piece to the list of active pieces
	    	 pieceLayer.repaint(); //refresh the layer to show the added element

	    	 createPieceCard(dialog.getName(),true,""); //create the card with the specified name and default other settings

    	 }catch(Exception e){
	    	 JOptionPane.showMessageDialog(this, "Sorry, there was an unexpected error", "Error", JOptionPane.ERROR_MESSAGE);
    	 }


     }

     /**
      * A method to generate an infoCard for a piece.
      * This method is only called directly after newPiece,
      * otherwise contradictions with the piece ID could occur
      * @param name - A String that will represent the name of the piece
      * @param gridLocked - A boolean to determine if the piece is initially grid locked or not
      * @param otherText - A String to represent the text area in the infoCard
      */
     public void createPieceCard(String name, boolean gridLocked, String otherText){
    	 //setup card
    	 infoCard new_card = new infoCard(); //create a card for the new piece
    	 new_card.setLayout(new VerticalLayout());
    	 new_card.setAssociatedID(idCounter); //set the id: this is where possible contradictions occur if the id has changed (fix it later)
    	 JLabel title = new JLabel(name+" ("+Integer.toString(idCounter)+")");
    	 title.setBorder(new EmptyBorder(0, 0, 20, 0)); //sets a 20px padding below
    	 new_card.add(title); //dummy JLabel for debugging

    	 JCheckBox gridLockedBox = new JCheckBox("Grid Locked",gridLocked);
    	 gridLockedBox.setActionCommand("gridLocked");
    	 gridLockedBox.addActionListener(this);
    	 new_card.add(gridLockedBox);

    	 JTextArea otherInfo = new JTextArea(otherText,15,15); //create a text area for other info
    	 otherInfo.setLineWrap(true); //wrap lines
    	 otherInfo.setWrapStyleWord(true); //wrap lines at whitespace
    	 new_card.add(otherInfo);

    	 cards.add(new_card); //add the card to the arraylist of cards
    	 infoPanel.add(new_card, Integer.toString(idCounter)); //add the card to the infoPanel with reference idCounter
     }

     /**
      * A method to scale all the pieces to the size of the current grid
      */
     public void scale_Pieces(){
    	 for(int i=0;i<pieces.size();i++){
    		 Piece temp_piece = pieces.get(i); //get the piece we are working with

    		 URL url = temp_piece.getURL(); //image location
        	 ImageIcon pieceImage = new ImageIcon(url); //we get the original image so that it is max resolution
        	 double image_Width = (double)pieceImage.getIconWidth(); //find the width of the original
        	 double image_Height = (double)pieceImage.getIconHeight(); //find the height of the original

    		 JPanel panel = (JPanel)gameBoard.getComponent(0); //get a major grid square for sizing
        	 double panelHeight = (double)panel.getHeight(); //height of a grid square
        	 double panelWidth = (double)panel.getWidth(); //width of a grid square

        	 //scale calculations
        	 double scale_width = panelWidth/image_Width; //scale percentage for width and height
        	 double scale_height = panelHeight/image_Height;
        	 int newWidth = (int)Math.floor(image_Width*Math.min(scale_width,scale_height)); //we scale the image by a factor of whichever is smaller so that the whole thing will fit in a box
        	 int newHeight = (int)Math.floor(image_Height*Math.min(scale_width,scale_height));

        	 pieces.get(i).setIcon(new ImageIcon(pieceImage.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH))); //set the image to the new scaled image
        	 pieces.get(i).setSize(newWidth, newHeight); //update the size
        	 pieces.get(i).repaint(); //repaint the pieces so that the changes show
    	 }

     }

     /**
      * A method to move each piece to the scaled location when the grid changes
      */
     public void relocate_pieces(){
    	 Piece c; //temp variable to work with each piece
    	 int current_minor; //location of the piece on the minor grid
    	 Point loc;//location of grid square
    	 Point absolute_location;
    	 int current_xAdjust; //stored xAdjustment
    	 int current_yAdjust; //stored yAdjustment
    	 double new_grid_x = gameBoard.getWidth(); //width of the newly caled board
    	 double new_grid_y = gameBoard.getHeight(); //height of the newly scaled board

    	 double scale_x = new_grid_x/grid_size_x; //porportion of new scale to old scale
    	 double scale_y = new_grid_y/grid_size_y;

    	 for(int i=0;i<pieces.size();i++){
    		 //get the current position values from the piece
    		 c = pieces.get(i);
    		 current_minor = c.getMinorGridSquare();
    		 loc = minSquares.get(current_minor).getLocation();
    		 absolute_location = SwingUtilities.convertPoint(minSquares.get(current_minor).getParent(),loc,minSquares.get(current_minor).getParent().getParent()); //we convert relative to the piece frame

    		 //make the adjustment scale calculations
    		 //if the piece is grid locked, we center it in the square
    		 if(c.getGridLocked()){
    			 Icon pieceImage = c.getIcon();
           	  	 double image_Width = (double)pieceImage.getIconWidth(); //find the width of the original
           	  	 double image_Height = (double)pieceImage.getIconHeight(); //find the height of the original
           	  	 JPanel panel = (JPanel)gameBoard.getComponent(0); //get a major grid square for sizing
           	  	 double panelWidth = (double)panel.getWidth(); //width of a grid square
           	  	 double panelHeight = (double)panel.getHeight(); //height of a grid square
           	  	 xAdjustment = Math.abs((int)(image_Width-panelWidth))/2; //find the xAdjustment by half the space left over in the square
           	  	 yAdjustment = Math.abs((int)(image_Height-panelHeight))/2; //find the yAdjustment by half the space left over in the square
    		 //if its not grid locked, we scale up the old adjustment values
    		 }else{
    			 current_xAdjust = c.getxAdjustment();//get the current adjustments
        		 current_yAdjust = c.getyAdjustment();
    			 xAdjustment = (int) (current_xAdjust*scale_x);//scale them by the factor previously calculated
    		 	 yAdjustment = (int) (current_yAdjust*scale_y);
        		 //xAdjustment = 0;
        		 //yAdjustment = 0;
    		 }
    		 c.setLocation((int) (absolute_location.getX() + xAdjustment), (int) (absolute_location.getY() + yAdjustment)); //reset the piece
			 pieces.get(i).setxAdjustment(xAdjustment); //update the xadjustment
			 pieces.get(i).setyAdjustment(yAdjustment); //update the yadjustment;
    	 }
    	 pieceLayer.repaint(); //repaint the layer so that the changes show
    	 grid_size_x = gameBoard.getWidth(); //update the size of the grid
    	 grid_size_y = gameBoard.getHeight();
     }

     //================================================================================
	 // Drag Events
	 //================================================================================

     public void mousePressed(MouseEvent e){ //called when the mouse is pressed
		  tempPiece = null; //temp variable to hold the selected
		  //Component c =  gameBoard.findComponentAt(e.getX(), e.getY()); //find something at the click point
		 Component c = pieceLayer.findComponentAt(e.getX(), e.getY());

    	 showCard(c);

		 if (c instanceof JPanel || c instanceof minorSquare){ //if there is no piece where selected, then we do nothing
			 return;
		  }

		  tempPiece = (Piece)c; //cast what we found to a piece

		  Point parentLocation = c.getLocation(); //find location of the piece
		  xAdjustment = parentLocation.x - e.getX(); //normalization between mouse position and piece position
		  yAdjustment = parentLocation.y - e.getY(); //this keeps mouse from jumping to the corner of the piece
		  tempPiece.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment); //start the drag event

		  tempPiece.setSize(tempPiece.getWidth(), tempPiece.getHeight()); //normalize size
		  dragPanel.add(tempPiece);
		  //layeredPane.add(tempPiece); //add the  piece to the drag layer
		  //layeredPane.setLayer(tempPiece, JLayeredPane.DRAG_LAYER); //move it to the drag layer

		  }

	 //Move the piece around

	  public void mouseDragged(MouseEvent me) {
		 if (tempPiece == null){ //don't do anything if no piece is selected
			 return;
		 }
		 	tempPiece.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment); //adjust the position to match the mouse
		 }

	  //Drop the piece back onto the chess board

	  public void mouseReleased(MouseEvent e) {
		  if(tempPiece == null) return; //do nothing if there is no piece

		  tempPiece.setVisible(false); //temporarily make it invisible while we find where it should go

		  if(tempPiece.getGridLocked()){ //if the piece is locked to the major grid
			  Component c =  gameBoard.findComponentAt(e.getX(), e.getY()); //look for a grid jpanel at the mouse position
			  if(c instanceof minorSquare){ //if we select a minor grid square, then we get the parent major grid square
				  c = c.getParent();
			  }
			  Container parent = (Container)c; //cast the jpanel component to a container
			  								   //this is the major grid square

			  Component c_abs = parent.getComponent(0);//we are placing it at the first minor grid square
			  Container parent_abs = (Container)c_abs; //this is the minor grid square
			  Point parentLocation_abs = parent_abs.getLocation(); //this gets the position relative to the major grid square
			  Point absolute_location = SwingUtilities.convertPoint(parent,parentLocation_abs,parent.getParent()); //so we convert relative to the piece frame

			  // Here we set the offset of the absolute location so that the piece is centered in the square
		      Icon pieceImage = tempPiece.getIcon();
        	  double image_Width = (double)pieceImage.getIconWidth(); //find the width of the original
        	  double image_Height = (double)pieceImage.getIconHeight(); //find the height of the original
        	  JPanel panel = (JPanel)gameBoard.getComponent(0); //get a major grid square for sizing
        	  double panelWidth = (double)panel.getWidth(); //width of a grid square
        	  double panelHeight = (double)panel.getHeight(); //height of a grid square
        	  xAdjustment = Math.abs((int)(image_Width-panelWidth))/2; //find the xAdjustment by half the space left over in the square
        	  yAdjustment = Math.abs((int)(image_Height-panelHeight))/2; //find the yAdjustment by half the space left over in the square

			  int tempPiece_index=-1;
			  for(int i=0;i<pieces.size();i++){ //finding the index of the piece that is being moved
				  if(tempPiece.equals(pieces.get(i))){
					  tempPiece_index = i;
					  break;
				  }
			  }
			  if(tempPiece_index == -1)//the piece was not found above
				  return;

			  for(int i=0;i<minSquares.size();i++){ //finding the square that the piece is moved to
				  if(parent_abs.equals(minSquares.get(i))){
					  pieces.get(tempPiece_index).setMinorGridSquare(i); //update the stored position
					  pieces.get(tempPiece_index).setxAdjustment(xAdjustment); //update the xadjustment
					  pieces.get(tempPiece_index).setyAdjustment(yAdjustment); //update the yadjustment;
					  break;
				  }
			  }

			  tempPiece.setLocation((int)(absolute_location.getX()+xAdjustment),(int)(absolute_location.getY()+yAdjustment)); //set the piece at that location
			  pieceLayer.add(tempPiece); //add the piece

		  }else{ //if the piece is unlocked
			  Component c = findNearestMinorSquare(e.getX(),e.getY()); //we find a minor grid square at the mouse
			  Container parent = (Container)c; //cast the jpanel component to a container

			  Point parentLocation = parent.getLocation(); //find location of square
			  Point absolute_location = SwingUtilities.convertPoint(parent.getParent(),parentLocation,parent.getParent().getParent()); //we convert position relative to the piece frame

			  tempPiece.setLocation((int)(absolute_location.getX() + xAdjustment),(int)(absolute_location.getY() + yAdjustment)); //set the piece at that location
			  												//we use adjustments so that the piece does not jump when dropped

			  int tempPiece_index=-1;
			  for(int i=0;i<pieces.size();i++){ //finding the index of the piece that is being moved
				  if(tempPiece.equals(pieces.get(i))){
					  tempPiece_index = i;
					  break;
				  }
			  }
			  if(tempPiece_index == -1)//the piece was not found above
				  return;

			  for(int i=0;i<minSquares.size();i++){ //finding the square that the piece is moved to
				  if(parent.equals(minSquares.get(i))){
					  pieces.get(tempPiece_index).setMinorGridSquare(i); //update the stored position
					  pieces.get(tempPiece_index).setxAdjustment(xAdjustment); //update the xAdjustment
					  pieces.get(tempPiece_index).setyAdjustment(yAdjustment); //update the yAdjustment
					  break;
				  }
			  }

			  pieceLayer.add(tempPiece); //add the piece
		  }

		  tempPiece.setVisible(true); //now we make it visible to the user at the new location
	  }

	  /**
	  * A recursive method to handle if the user drops the piece of the line of a major grid square.
	  * We add 1 to the x and y coordinates until we find a minor grid square
	  * @param x - An int indicating the displacement in the x direction
	  * @param y - An int indicating the displacement in the y direction
	  */
	  public Component findNearestMinorSquare(int x, int y){
		  Component c = gameBoard.findComponentAt(x, y); //look for a grid jpanel at the mouse position
		  if(!(c instanceof minorSquare)){ //if what we got is not a minor grid square, then its a major grid square and we recurse with added x and y
			  c = findNearestMinorSquare(x+1,y+1); //we add 1 to each component and look again
		  }
		  return c;
	  }

	  public void mouseClicked(MouseEvent e) {
	  }

	  /**
	   * A method which gets passed components from a mouse press to handle selection of a piece for the infoPanel
	   * @param ca - The component selected by the mouse event
	   */
	  public void showCard(Component ca){
		  Piece tempP = null; //temp variable to hold the selected
		  //Component c = pieceLayer.findComponentAt(e.getX(), e.getY());
		  Component c = ca;

		  ///For Loop///
		  //We are looping through all the pieces and setting their borders back to null since we are selecting comething else
		  for(int i=0;i<pieces.size();i++){
			  pieces.get(i).setBorder(null);
		  }

		  if(c instanceof minorSquare){ //if we selected a minor grid square, we will replace it with a minor grid square
			  c = c.getParent();
		  }
		  if (c instanceof JPanel){ //if we have selected a grid square
			  infoCards.show(infoPanel, "-1"); //show the default card
		  }else if(c instanceof Piece){ //we have selected a piece
			  tempP = (Piece)c; //cast what we found to a piece
			  tempP.setBorder(BorderFactory.createLineBorder(Color.red,5)); //set the border to red to indicate selection
			  infoCards.show(infoPanel, Integer.toString(tempP.getCardId())); //change the shown card to match the selected piece
		  }
	  }

	  //Other mouse methods
	  //We need to define them because we are implementing MouseListener, MouseMotionListener
	  //So we define them as empty for now
	  //This can be changed for additional functionality later
	  public void mouseMoved(MouseEvent e) {
	  }
	  public void mouseEntered(MouseEvent e){
	  }
	  public void mouseExited(MouseEvent e) {
	  }

	 //================================================================================
	 // Main
	 //================================================================================

     public static void main(String[] args) //main function to run the program
     	{
        Game frame = new Game(); //create the game
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE); //end program when close is pressed
        //frame.pack(); //autoscale to fit all elements
        frame.setMinimumSize(new Dimension(1000,700));
        frame.setSize(1000, 700); //default size
        frame.setVisible(true); //let the user see it
        }
  }
