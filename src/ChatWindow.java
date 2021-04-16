import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 * 
 */

/**
 * @author Julius "JLJ" Jacobsohn
 * 
 */
/*
 * else if (line.equals("n")) { // n // <string-1word> } else if
 * (line.equals("m")) { // m <string> }
 */
public class ChatWindow extends JFrame {
	private String ip, name, myName;
	private int port;
	private static JScrollBar  scrollbar1;

	private JTextPane paneChat;
	private JTextField textMessage;
	private JButton buttonSend;
	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;

	/*
	 * Try to connect with someone
	 */
	public ChatWindow(String name, String ip, int port, String myName) {
		super();
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.myName = myName;

		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(ip, port));
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			createThreads();
			out.print("n " + name + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initialize();
	}

	/*
	 * Someone tries to connect with you
	 */
	public ChatWindow(Socket client, String myName) {
		socket = client;
		this.myName = myName;
		try {
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			createThreads();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initialize();
	}

	private void createThreads() {
		Thread inputThread = new Thread() {
			public void run() {
				while (true) {
					try {
						String line = in.readLine().toString();
						System.out.println("Line_Client: " + line);
						if (!line.equals("")) {
							if (line.charAt(0) == 'x') {

								JOptionPane
										.showMessageDialog(null,
												"Verbindung wurde vom Server geschlossen");
								in.close();
								out.print("x \n");
								out.flush();
								out.close();
								socket.close();
								setVisible(false);
								dispose();
								// TODO: Terminate?
							} else if (line.charAt(0) == 'e') { // e
								String error = line.substring(2);
								JOptionPane.showMessageDialog(null, "Fehler:\n"
										+ error);
							} else if (line.charAt(0) == 'n') { //
								setTitle(line.substring(2));
							} else if (line.charAt(0) == 'm') { // e
								paneChat.setText(paneChat.getText() + "\n"
										+ name + ": " + line.substring(2));
							} else {
								System.out.println("Fehler: " + line);
								// TODO: Terminate?
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		inputThread.start();

	}

	private void initialize() {

		setLayout(null);
		setSize(500, 500);
		setResizable(false);
		setLocationRelativeTo(null);
		// setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(name);		
	

		paneChat = new JTextPane();
		paneChat.setBounds(10, 10, 472, 410);
		paneChat.setText("Willkommen im Chat mit "+name+".");
		paneChat.setEnabled(true);	

		textMessage = new JTextField();
		textMessage.setBounds(10, 430, 385, 30);
		textMessage.setText("Nachricht");
		add(textMessage);

		buttonSend = new JButton();
		buttonSend.setBounds(405, 430, 77, 30);
		buttonSend.setText("Senden");
		buttonSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//if (!textMessage.getText().equals("")) {
					paneChat.setText(paneChat.getText() + "\n" + myName + ": "
							+ textMessage.getText());
					out.print("m " + textMessage.getText() + "\n");
					out.flush();
					textMessage.setText("");
				//}

			}
		});

		JScrollPane scrollPaneChatWindow = new JScrollPane(
				paneChat);
		scrollPaneChatWindow.setBounds(10, 10, 472, 410);	
		//TODO: Autoscroll nach unten
		
		add(buttonSend);
		add(scrollPaneChatWindow);
		setVisible(true);
	}

}
