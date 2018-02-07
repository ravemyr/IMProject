package Buttons;
import java.awt.event.*;
import javax.swing.*;
public class SettingsButton extends JButton implements ActionListener{
	public void actionPerformed(ActionEvent e){
		
	}
	public void chooseColor(){
		new JColorChooser();
	}
	public void chooseName(){
		
	}
	public SettingsButton(){
		super();
		this.setText("Settings");
	}
}
