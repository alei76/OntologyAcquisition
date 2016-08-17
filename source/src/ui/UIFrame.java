package ui;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.UIManager;

public class UIFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UIFrame() {
		setContentPane(new UIPanel());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		setLocation(ALLBITS, ALLBITS);
		setSize(800,620);
		setVisible(true);
		setUIFont( new javax.swing.plaf.FontUIResource("Microsoft JhengHei UI", Font.BOLD, 16) );
	}
	
	private void setUIFont(javax.swing.plaf.FontUIResource f) {
		Enumeration<?> keys = UIManager.getLookAndFeelDefaults().keys();
		while( keys.hasMoreElements() ) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}
	
}
