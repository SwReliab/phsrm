package phsrm.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import phsrm.common.SRMBufferedMsg;

public class LogPanel extends JPanel implements SRMBufferedMsg {

	JTextArea logText;
	static String ln = System.getProperty("line.separator");
	
	LogPanel() {
		setLayout(new BorderLayout());

		JLabel label = new JLabel("Messages");
		add(label, BorderLayout.NORTH);

		logText = new JTextArea();
		logText.setLineWrap(false);
		logText.setEnabled(true);
		logText.setRows(5);
		JScrollPane slogText = new JScrollPane(logText);
		add(slogText, BorderLayout.CENTER);
	}

	public void append(String str) {
		logText.append(str);
		logText.setCaretPosition(logText.getDocument().getLength());
	}

	public void appendln(String str) {
		logText.append(str + ln);
	}
	
	public void clear() {
		logText.setText("");
	}
}
