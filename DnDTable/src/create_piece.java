import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class is a JPanel that is responsible for creating a piece to add to Game
 */
public class create_piece extends JPanel implements ActionListener, ItemListener {
	
	//================================================================================
	// Variable Declarations
	//================================================================================
	
	/**
	 * A JPanel to cold the comboBox
	 */
	private JPanel comboBoxPane;
	final static String NEWPANEL = "New Piece";
	final static String OPENPANEL = "Open Saved Piece    ";
	private String[] comboBoxItems = { OPENPANEL, NEWPANEL }; //options for the comboBox
	private JComboBox<String> cb; //selection comboBox
	
	/**
	 * A JPanel to hold the file path and browse buttons.
	 * We instantiate 2 of each object, one for open and one for new
	 */
	private JPanel filePane;
	private JLabel file_path; //a label to display the path to the currently selected file
	private JButton pick_file; //a button to launch the file picker
	private JPanel filePane2;
	private JLabel file_path2; //a label to display the path to the currently selected file
	private JButton pick_file2; //a button to launch the file picker
	
	/**
	 * A JPanel to hold the form to open a saved piece
	 */
	private JPanel openPane;
	
	/**
	 * A JPanel to hold the form to create a new piece
	 */
	private JPanel newPane;
	private JPanel labelPane;
	private static final JLabel name_field = new JLabel("Name:");
	private static final JLabel image_field = new JLabel("Image: ");
	private JPanel fieldPane;
	private JTextField name;
	
	/**
	 * A CardLayout to switch between the open and new cards
	 */
	private CardLayout type = new CardLayout();
	private JPanel cardPane;
	
	/**
	 * The file chooser that we use for selecting the template or save file
	 */
	private final JFileChooser fc;
	private File file;
	
	//================================================================================
	// Constructors
	//================================================================================
	public create_piece(){
		setLayout(new BorderLayout());
		
		//setup the combo box and its options
		comboBoxPane = new JPanel(); //use FlowLayout
		cb = new JComboBox<String>(comboBoxItems);
		cb.setEditable(false);
		cb.addItemListener(this); //we listen for changes and then change the card respectively
		comboBoxPane.add(cb);
		add(comboBoxPane, BorderLayout.PAGE_START); //make sure the combo box is first
		
		//setup the file choosing options
		filePane = new JPanel();
		file_path = new JLabel("No File Selected...");
		file_path.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 50));
		pick_file = new JButton("Browse");
		pick_file.addActionListener(this);
		filePane.add(file_path);
		filePane.add(pick_file);
		
		filePane2 = new JPanel();
		filePane2.setBorder(BorderFactory.createEmptyBorder(10,5,0,0));
		file_path2 = new JLabel("No File Selected...");
		file_path2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
		pick_file2 = new JButton("Browse");
		pick_file2.addActionListener(this);
		filePane2.add(file_path2);
		filePane2.add(pick_file2);
		
		//setup the open piece form
		openPane = new JPanel();
		openPane.add(filePane);
		
		//setup the new piece form
		newPane = new JPanel(new BorderLayout());
		labelPane = new JPanel(new VerticalLayout(25));
		labelPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 50));
		fieldPane = new JPanel(new VerticalLayout());
		fieldPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 50));
		newPane.add(labelPane, BorderLayout.WEST);
		newPane.add(fieldPane, BorderLayout.CENTER);
		labelPane.add(name_field);
		labelPane.add(image_field);
		name = new JTextField("", 15);
		fieldPane.add(name);
		fieldPane.add(filePane2);
		
		//setup the cardPane
		cardPane = new JPanel(type);
		cardPane.add(openPane, OPENPANEL);
		cardPane.add(newPane, NEWPANEL);
		add(cardPane, BorderLayout.CENTER);
		
		//setup the file chooser
		fc = new JFileChooser();
		file = null;
		
	}
	
	//================================================================================
	// Event Listeners
	//================================================================================
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == pick_file) {			 // if the user presses the browse button
			//set the initial directory
			String path = System.getProperty("user.dir");
		    path = path + "/bin/Resources/Pieces/";
		    File new_file = new File(path); 
			fc.setCurrentDirectory(new_file);
			
			int returnVal = fc.showOpenDialog(this); // then we launch the file picker
	        
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            file = fc.getSelectedFile();
	        	file_path.setText(file.getName()); //set the JLabel to the file name
	            //This is where a real application would open the file.
	            //System.out.println(file);
	        }
	   }else if(e.getSource() == pick_file2){
		   //set the initial directory
		   String path = System.getProperty("user.dir");
	       path = path + "/bin/Resources/Images/";
	       File new_file = new File(path); 
		   fc.setCurrentDirectory(new_file);
		   fc.addChoosableFileFilter(new ImageFilter());
		   fc.setAcceptAllFileFilterUsed(false);
		   fc.setAccessory(new ImagePreview(fc));
		   
		   int returnVal = fc.showOpenDialog(this); // then we launch the file picker
	        
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            file = fc.getSelectedFile();
	        	file_path2.setText(file.getName()); //set the JLabel to the file name
	        }
	   }
		
	}
	
	public void itemStateChanged(ItemEvent e){
		//if the combo box changes, we switch to the appropriate card
		type.show(cardPane, (String)e.getItem());
	}
	
	//================================================================================
	// Get Functions
	//================================================================================
	
	public File getFile(){
		return file;
	}
	public String getSelection(){
		return (String)cb.getSelectedItem();
	}
	public String getName(){
		return name.getText();
	}
	
}
