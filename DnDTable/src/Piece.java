import javax.swing.Icon;
import javax.swing.JLabel;
import java.net.URL;

public class Piece extends JLabel{
	
	//================================================================================
	// Variable Declarations
	//================================================================================
	
	private URL image_url; //url of the image for the piece
	private boolean gridLocked; //true if the piece is locked to the major grid
	private String name; //the name of the piece
	
	private int minorGridSquare; //the current minor grid location of the piece, used for resizing
	private int xAdjustment; //store the offset from the minorGridSquare position
	private int yAdjustment;
	
	private int cardId; //an ID unique to every piece that is used to reference its associated card in the infopanel
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Constructs and instance of Piece with all parameters specified
	 * @param im - An icon to represent the piece on the game board
	 * @param image_url - A URL of the location of the icon image
	 * @param gridLocked - A boolean to specify if the piece is to be grid locked or not
	 * @param cardId - A unique ID for each created piece
	 * @param name - A string to label the piece on the game board
	 */
	public Piece(Icon im, URL image_url, boolean gridLocked, int cardId, String name){ //constructor which specifies all variables
		super(im);
		this.image_url = image_url;
		this.gridLocked = gridLocked;
		this.cardId = cardId;
		this.name = name;
		minorGridSquare = 0; //every piece begins in the upper left corner
		xAdjustment = 0;
		yAdjustment = 0;
		
	}
	
	//// Constructors to handle non-specified variables ////
	
	public Piece(Icon im, URL u){
		super(im);
		image_url=u;
		gridLocked = true; //default grid locked to true
		cardId = -2; //default to -2 and allow setting by generation algorithm
		minorGridSquare = 0; //every piece begins in the upper left corner
		xAdjustment = 0;
		yAdjustment = 0;
	}
	
	//================================================================================
	// Get Functions
	//================================================================================
	
	public URL getURL(){
		return image_url;
	}
	public boolean getGridLocked(){
		return gridLocked;
	}
	public String getPieceName(){
		return name;
	}
	public int getMinorGridSquare(){
		return minorGridSquare;
	}
	public int getxAdjustment(){
		return xAdjustment;
	}
	public int getyAdjustment(){
		return yAdjustment;
	}
	public int getCardId(){
		return cardId;
	}
	
	//================================================================================
	// Set Functions
	//================================================================================
	
	public void setURL(URL u){
		image_url = u;
	}
	public void setGridLocked(boolean g){
		gridLocked = g;
	}
	public void setPieceName(String name){
		this.name = name;
	}
	public void setMinorGridSquare(int m){
		minorGridSquare = m;
	}
	public void setxAdjustment(int x){
		xAdjustment = x;
	}
	public void setyAdjustment(int y){
		yAdjustment = y;
	}
	public void setCardId(int c){
		cardId = c; //as these are unique id's, this should be called only once
	}
	/*public void constructCard(){
		gridLocked_box = new JCheckBox("Grid Locked");
		gridLocked_box.setSelected(getGridLocked());
		gridLocked_box.addItemListener(this);
	}*/
}
