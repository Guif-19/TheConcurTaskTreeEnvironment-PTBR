import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

@SuppressWarnings("serial")
public class LeftComposant extends JSplitPane implements ActionListener{
	
	MainFrame owner;
	State state;
	
	ButtonListComposant buttonlistcomposant;
	JButton show;
	JButton hide;
	JPanel panelShow;
	JPanel panelHide;
	
	final Dimension DIMBUTTON = new Dimension(30,30);
	
	public enum State {
		HIDE, SHOW
	}
	
	public LeftComposant( MainFrame owner, DrawingArea da){
		
		this.owner = owner;
		this.setOpaque(false);
		
		this.setDividerSize(1);
		
		panelShow = new JPanel();
		BoxLayout box = new BoxLayout(panelShow, BoxLayout.Y_AXIS);
		
		panelShow.setLayout(box);

		hide = new JButton();	
		hide.setIcon(new ImageIcon(getClass().getResource("images/showHide.jpg")));
		hide.setToolTipText("Ocultar barra de ferramentas");
		hide.setSize(DIMBUTTON);
		hide.addActionListener(this);
		hide.setAlignmentY(Component.TOP_ALIGNMENT);
		hide.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelShow.add(hide);
		
		panelShow.add(Box.createVerticalStrut(10));
		
		buttonlistcomposant = new ButtonListComposant(da);
		
		panelShow.add(buttonlistcomposant);
		
		panelHide = new JPanel(new GridLayout(1, 1));
		
		show = new JButton();
		show.setIcon(new ImageIcon(getClass().getResource("images/showHide.jpg")));
		show.setToolTipText("Mostrar barra de ferramentas");
		show.setSize(DIMBUTTON);
		show.addActionListener(this);
		
		panelHide.add(show);
		
		state = State.SHOW;
		
		changeInterface();
	}
	
	public void changeInterface(){

		switch (state) {
		case SHOW:
			this.setTopComponent(panelShow);
			this.setBottomComponent(null);
			this.setSize( (int)(0.085*owner.getWidth()), owner.getHeight());
			this.repaint();
			break;
		case HIDE:
			this.setTopComponent(panelHide);
			this.setBottomComponent(null);
			this.setSize((int)(0.085*owner.getWidth()), (int) (owner.getHeight()*0.055));
			this.repaint();
			break;
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == hide){
			state = State.HIDE;
			changeInterface();
		}else{
			state = State.SHOW;
			changeInterface();
		}
	}

}
