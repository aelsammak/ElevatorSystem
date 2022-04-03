package gui;

import javax.swing.JTextArea;

/**
 * This Text Manager class is responsible for writing the logs to the textpanel
 */
public class TextManager {
	
	private static final JTextArea textPanel = GUI.textPanel;
	
	/**
	 * print text to scroll panel
	 * @param text
	 */
	public static synchronized void print(String text) {
		textPanel.append(text+"\n");
	}
}
