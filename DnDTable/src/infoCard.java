import javax.swing.BoxLayout;
import javax.swing.JPanel;

//This class is the cards that are shown in the infoPanel
//It extends JPanel so that it can hold the piece controls
//But we define it as a separate class so that we can store more specifics
public class infoCard extends JPanel
{
	private int associatedID; //the id used to reference the card in the cardlayout
	
	//================================================================================
	// Constructors
	//================================================================================
	/**
	* Constructs an instance of infoCard with no set parameters
	* Using this instance requires the use of the set methods before the component is drawn
	*/
	public infoCard(){
		super();
	}
	/**
	* Constructs an instance of infoCard with specified id
	* 
	* @param associatedID An int value indicating the associated id of the components
	*/
	public infoCard(int associatedID){
		super();
		this.associatedID = associatedID;
	}
	
	//================================================================================
	// Get Methods
	//================================================================================
	public int getAssociatedID(){
		return associatedID;
	}
	
	//================================================================================
	// Set Methods
	//================================================================================
	public void setAssociatedID(int id){
		associatedID = id;
	}
	
}
