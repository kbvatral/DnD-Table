import java.awt.LayoutManager;
import javax.swing.JPanel;

//this class has no functionality additional to JPanel, but we create it so that we can tell if the user
//clicked on a major (JPanel) or minor (minorSquare) grid square using instanceof

public class minorSquare extends JPanel {
	public minorSquare(){
		super();
	}
	public minorSquare(LayoutManager layout){
		super(layout);
	}
}
