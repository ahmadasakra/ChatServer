import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
//2534
//java -jar '/home/jacobs00/Arbeitsfl√§che/Informatik TU Dortmund/RvS/Programmieraufgabe/TestClient.jar' 129.217.12.166

@SuppressWarnings("serial")
public class ChatClient extends JFrame implements WindowListener {
	JTextField textServeraddress;
	JTextField textUserName;
	JLabel labelServeraddress;
	JLabel labelClientName;
	JLabel labelConnectInfo;
	JButton buttonTable;
	JButton buttonLogin;
	JButton buttonConnectClient;
	Socket mySocket;
	private PrintWriter out;
	private BufferedReader in;
	JTable tableConnectedClients;

	String currentUser;
	String currentSelectedName, currentSelectedIP, currentSelectedPort;

	private ServerSocket clientAcceptor;

	public ChatClient() {
		super();
		setLayout(null);
		final String[] columNames = { "Name des Users", "IP-Adresse", "Port" };

		labelServeraddress = new JLabel("AB-Server IP");
		labelServeraddress.setBounds(10, 10, 100, 30);
		add(labelServeraddress);

		textServeraddress = new JTextField("192.168.2.103");
		textServeraddress.setBounds(120, 10, 100, 30);
		add(textServeraddress);

		labelClientName = new JLabel("Username");
		labelClientName.setBounds(10, 50, 100, 30);
		add(labelClientName);

		textUserName = new JTextField("Loginname");
		textUserName.setBounds(120, 50, 100, 30);
		add(textUserName);

		buttonLogin = new JButton("Login");
		buttonLogin.setBounds(230, 10, 100, 30);
		buttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					/*
					 * Socket erstellen
					 */
					mySocket = new Socket();
					mySocket.connect(new InetSocketAddress(textServeraddress
							.getText(), 2534));

					/*
					 * Output und InputStream herstellen
					 */
					out = new PrintWriter(mySocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(mySocket
							.getInputStream()));

					awaitClientConnection();

					Thread inputThread = new Thread() {
						public void run() {
							while (true) {
								try {
									String line = in.readLine().toString();
									System.out.println("Line: " + line);
									if (line.contains("x")) {

										JOptionPane
												.showMessageDialog(null,
														"Verbindung wurde vom Server geschlossen");
										in.close();
										out.print("x \n");
										out.flush();
										out.close();
										mySocket.close();
										setVisible(false);
										dispose();
										// TODO: Terminate?
									} else if (line.charAt(0) == 't') { // t
																		// <int>
										String[] lineSplit = line.split(" ");
										int t = Integer.parseInt(lineSplit[1]);
										final Object[][] tableContent = new Object[t][3];
										for (int i = 0; i < t; i++) {
											line = in.readLine().toString();
											String[] tableContentString = line
													.split(" ");
											tableContent[i][0] = tableContentString[0];
											tableContent[i][1] = tableContentString[1];
											tableContent[i][2] = tableContentString[2];
										}

										final JTable table = new JTable(tableContent, //TODO: Tabelleninitialisierung nicht ind er Schleife
												columNames);
										table.setCellSelectionEnabled(false);
										table.setEnabled(false);
										table.setBounds(10, 90, 477, 300);
										table.addMouseListener(new java.awt.event.MouseAdapter() {
										    @Override
										    public void mouseClicked(java.awt.event.MouseEvent evt) {
										        int row = table.rowAtPoint(evt.getPoint());
										        int col = table.columnAtPoint(evt.getPoint());
										        if (row >= 0 && col >= 0) {
										        	currentSelectedName = (String) tableContent[row][0];
										        	currentSelectedIP = (String) tableContent[row][1];
										        	currentSelectedPort = (String) tableContent[row][2];
										        	
										        	labelConnectInfo.setText("Chatte mit "+currentSelectedName);
										        	buttonConnectClient.setVisible(true);
										        }else{
										        	labelConnectInfo.setText("Bitte Nutzer in der Tabelle ausw‰hlen");
										        	buttonConnectClient.setVisible(false);
										        }
										    }
										});
										JScrollPane tableScroll = new JScrollPane(
												table);
										tableScroll.setBounds(10, 90, 477, 300);
										add(tableScroll);
										repaint();

									} else if (line.charAt(0) == 'e') { // e
										String[] errors = line.split(" ");
										String error = "";
										for (int i = 1; i < errors.length; i++) {
											error = error + errors[i] + " ";
										}
										JOptionPane.showMessageDialog(null,
												"Fehler:\n" + error);
									} else if (line.equals("s")) {
										System.out.println("Login erfolgreich");
									} else {
										System.out.println("Fehler: " + line);
										// TODO: Terminate?
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					};

					inputThread.start();

					/*
					 * Anmeldung beim AB-Server
					 */
					currentUser = textUserName.getText();

					out.print("n " + currentUser + " 4444 \n");
					out.flush();
					out.print("t\n");
					out.flush();

					// GUI-Elemente vom login ‰ndern
					textUserName.setVisible(false);
					textServeraddress.setVisible(false);
					buttonLogin.setVisible(false);

					labelServeraddress
							.setText("Angemeldet als: " + currentUser);
					labelServeraddress.setSize(400, 30);
					labelClientName.setText("Nutzerliste:");

					buttonTable.setVisible(true);

					labelConnectInfo = new JLabel(
							"Bitte Nutzer in der Tabelle ausw‰hlen");
					labelConnectInfo.setBounds(10, 400, 400, 30);
					add(labelConnectInfo);

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		});

		buttonConnectClient = new JButton("Ok");
		buttonConnectClient.setBounds(386, 400, 100, 30);
		buttonConnectClient.setVisible(false);
		buttonConnectClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ChatWindow(currentSelectedName, currentSelectedIP, Integer
						.parseInt(currentSelectedPort), currentUser);
			}
		});
		add(buttonConnectClient);

		buttonTable = new JButton("Aktualisieren");
		buttonTable.setBounds(366, 50, 120, 30);
		buttonTable.setVisible(false);
		buttonTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.print("t\n");
				out.flush();
			}
		});
		add(buttonTable);
		add(buttonLogin);

		// Window settings
		setSize(500, 500);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

	}

	private void awaitClientConnection() {
		try {
			clientAcceptor = new ServerSocket();
			clientAcceptor.bind(new InetSocketAddress(4444));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread waitThread = new Thread() {
			public void run() {
				while (true) {
					try {
						Socket client = clientAcceptor.accept();
						new ChatWindow(client, currentUser);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};

		waitThread.start();
	}

	public Object[] getObjectArray(String str) {
		return null;
	}

	public static void main(String[] args) {
		new ChatClient();
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			in.close();
			out.print("x \n");
			out.flush();
			out.close();
			mySocket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
