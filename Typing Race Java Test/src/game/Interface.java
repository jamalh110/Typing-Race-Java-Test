package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.UUID;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;

public class Interface extends JFrame implements ActionListener, DocumentListener {
	// the main jpanel
	JPanel componentsPanel = new JPanel();
	// the main class doing the communication
	Main communicator = null;
	// create the components so you can access them from threads
	JLabel loginStatus;
	JTextField username;
	JTextField password;
	JButton login;
	JButton match;
	JLabel matchStatus;
	JLabel matchOpponent;
	JLabel matchMessage;
	JLabel passage;

	JTextField input;
	JTextArea matchUpdates;
	JScrollPane scrollPane;
	// loginkey
	String loginKey = null;
	// match sessionID
	UUID sessionID = null;
	// id of game placed in
	UUID matchID = null;
	// id of game thread
	UUID threadID = null;

	
	
	LinkedList<String> passageList = null;
	int passageLength = 0;
	LinkedList<String> writtenList = new LinkedList<String>();
	public Interface() {
		setSize(400, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("TYPE RACE TEST CLIENT");
		// set the jframe to border layout
		getContentPane().setLayout(new BorderLayout());
		// set the main jpanel to box layout top to bottom
		componentsPanel.setLayout(new BoxLayout(componentsPanel, BoxLayout.Y_AXIS));
		// make all the components
		loginStatus = new JLabel("not logged in");
		username = new JTextField("username");
		password = new JTextField("password");
		username.setMaximumSize(new Dimension(300, 30));
		password.setMaximumSize(new Dimension(300, 30));
		login = new JButton("login");
		login.setName("loginButton");
		login.addActionListener(this);
		match = new JButton("Make Match");
		match.setName("matchButton");
		match.addActionListener(this);
		matchOpponent = new JLabel("match not requested");
		matchMessage = new JLabel("match not requested");
		matchUpdates = new JTextArea("Match Status:\n");
		matchUpdates.setEditable(false);
		scrollPane = new JScrollPane(matchUpdates);
		// game components
		passage = new JLabel("passage");
		passage.setVisible(false);
		input = new JTextField();
		input.setMaximumSize(new Dimension(200, 30));
		input.setVisible(false);
		input.addActionListener(this);
		input.getDocument().addDocumentListener(this);
		matchStatus = new JLabel("waiting for opponent to connect");
		matchStatus.setVisible(false);
		// center allign all the components
		loginStatus.setAlignmentX(CENTER_ALIGNMENT);
		username.setAlignmentX(CENTER_ALIGNMENT);
		password.setAlignmentX(CENTER_ALIGNMENT);
		login.setAlignmentX(CENTER_ALIGNMENT);
		match.setAlignmentX(CENTER_ALIGNMENT);
		matchStatus.setAlignmentX(CENTER_ALIGNMENT);
		matchOpponent.setAlignmentX(CENTER_ALIGNMENT);
		matchMessage.setAlignmentX(CENTER_ALIGNMENT);
		matchUpdates.setAlignmentX(CENTER_ALIGNMENT);
		passage.setAlignmentX(CENTER_ALIGNMENT);
		// add all the components to the jpamel
		componentsPanel.add(loginStatus);
		componentsPanel.add(username);
		componentsPanel.add(password);
		componentsPanel.add(login);
		componentsPanel.add(match);

		// componentsPanel.add(matchStatus);
		// componentsPanel.add(matchOpponent);
		// componentsPanel.add(matchMessage);
		componentsPanel.add(scrollPane);
		componentsPanel.add(matchStatus);
		componentsPanel.add(passage);
		componentsPanel.add(input);
		// passage.setVisible(true);
		// input.setVisible(true);
		// add the jpanel to the center of the jframe content pane
		this.add(componentsPanel, BorderLayout.CENTER);

	}

	public void display() {
		this.setVisible(true);
	}

	// uses a different class for communication to server. this sets that class.
	public void setMain(Main main) {

		this.communicator = main;
	}

	// the main game loop. it gets all the info then loops though the match,
	// receives input, and gives data.
	public void gameLoop() {
		Thread loop = new Thread(new Runnable() {

			@Override
			public void run() {
				UUID result = communicator.matchRequest(loginKey);
				if (result != null) {
					sessionID = result;
					matchUpdates.append("Match requested\nYour match session id: " + sessionID.toString()
							+ "\nWaiting to be matched...\n");

				} else {
					System.out.println("match failed");
					matchUpdates.append("Failed\n");
					return;
				}
				String[] gameAndThread = getGame(sessionID);
				if (gameAndThread == null) {
					matchUpdates.append("Failed\n");
					return;
				}
				matchID = UUID.fromString(gameAndThread[0]);
				threadID = UUID.fromString(gameAndThread[1]);
				matchUpdates.append("Match ID Received: " + matchID + "\nConnecting to game...\n");
				boolean connected = connectMatch(matchID, threadID);
				if (!connected) {
					matchUpdates.append("Connection failed\n");
					return;
				}
				matchUpdates.append("Connected\nFetching opponent name\n");
				String oppName = getOpponentName(sessionID, matchID);
				if (oppName == null) {
					matchUpdates.append("Opponent Name fetch failed\n");
					return;
				}
				matchUpdates.append("Opponent username is: " + oppName + "\n");
				String passage = getPassage(sessionID, matchID);
				if (passage == null) {
					matchUpdates.append("Passage fetch failed\n");
					return;
				}
				Interface.this.passageList = splitList(passage);
				Interface.this.passageLength = passageList.size();
				Interface.this.passage.setText("<html>" + passage + "</html>");
				Interface.this.passage.setVisible(true);
				Interface.this.input.setVisible(true);
				Interface.this.matchStatus.setVisible(true);
				Interface.this.revalidate();
				while (true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String status = getStatus(sessionID, matchID);
					if (status == null) {
						matchUpdates.append("Status fetch failed\n");
						return;
					}
					if(status.contains("Code:0")) {
						continue;
					}
					else if(status.contains("Code:1")) {
						status = status.replace("Code:1", "");
						matchStatus.setText("Match will start in: " + ((int)((Integer.parseInt(status)/1000))));
						continue;
					}
					else if(status.contains("Code:2")) {
						matchStatus.setText("Game Started!");
						break;
					}
				}
				while(true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String status = getStatus(sessionID, matchID);
					if (status == null) {
						matchUpdates.append("Status fetch failed\n");
						return;
					}
					if(status.contains("Code:3")) {
						//shutdown loop, reset.
						String winnerID = status.replace("Code:3", "");
						UUID winnerUUID = UUID.fromString(winnerID);
						if(winnerUUID.equals(sessionID)) {
							JOptionPane.showMessageDialog(Interface.this, "YOU WON!", "Alert",0);
						}
						else {
							JOptionPane.showMessageDialog(Interface.this, "YOU LOST!", "Alert",0);
						}
						return;
					}
					else if(status.contains("Code:2")) {
						status = status.replace("Code:2", "");
						int oppPercentThrough = Integer.parseInt(status);
						int percentThrough = 100-(passageList.size()*100)/passageLength;
						try {
							Main.updateStatus.put(("Your percent through: "+percentThrough+"% | Opponent percent through: "+oppPercentThrough+"%"));
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
					}
				}
				//System.out.println("Game Started");
			}
			
		});
		loop.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// if login button is pressed
		if (e.getSource() == login) {
			Thread login = new Thread(new Runnable() {

				@Override
				public void run() {
					// gets the user's login key.
					String result = communicator.login(username.getText(), password.getText());

					if (!result.contains("failed")) {

						loginReceived(result);
					} else {
						System.out.println("Login failed");
						loginStatus.setText("Login Failed");
					}
				}

			});
			login.start();
			System.out.println("done");
		}
		// if match make button is pressed. starts the game loop
		if (e.getSource() == match) {
			this.writtenList = new LinkedList<String>();
			this.matchUpdates.setText("");
			gameLoop();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {

		if(e.getDocument().equals(input.getDocument())) {
			
			String input = this.input.getText();
			if(input.length()==0) {
				return;
			}
			if(input.substring(input.length() - 1).equals(" ")) {
				
				String withoutSpace = input.replace(" ", "");
				if(withoutSpace.equals(passageList.get(0))) {
					this.input.setForeground(Color.BLACK);
					String sendWord = passageList.removeFirst();
					writtenList.add(sendWord);
					Runnable doAssist = new Runnable() {
                        @Override
                        public void run() {
                        		Interface.this.input.setText("");
                        		Interface.this.input.grabFocus();
                        }
                     };
                     SwingUtilities.invokeLater(doAssist);
                     try {
						Main.updatePassage.put(updatePassage(Interface.this.writtenList, Interface.this.passageList));
					} catch (InterruptedException e1) {
						
						e1.printStackTrace();
					}
					//send to server
					Thread word = new Thread(new Runnable() {

						@Override
						public void run() {
						
							String result = communicator.sendWord(sessionID,matchID,sendWord);

							/*if (!result.contains("failed")) {

								//loginReceived(result);
							} else {
								System.out.println("Login failed");
								loginStatus.setText("Login Failed");
							}*/
						}

					});
					word.start();
				}
				else {
					this.input.setForeground(Color.RED);
				}
			}
			else {
				if(!isFront(input, passageList.get(0))) {
					this.input.setForeground(Color.RED);
				}
				else {
					this.input.setForeground(Color.BLACK);
				}
			}
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if(e.getDocument().equals(input.getDocument())) {
			
			String input = this.input.getText();
			if(input.length()==0) {
				return;
			}
			if(input.substring(input.length() - 1).equals(" ")) {
				
				String withoutSpace = input.replace(" ", "");
				if(withoutSpace.equals(passageList.get(0))) {
					this.input.setForeground(Color.BLACK);
					String sendWord = passageList.removeFirst();
					writtenList.add(sendWord);
					Runnable doAssist = new Runnable() {
                        @Override
                        public void run() {
                        		Interface.this.input.setText("");
                        		Interface.this.input.grabFocus();
                        }
                     };
                     SwingUtilities.invokeLater(doAssist);
                     try {
						Main.updatePassage.put(updatePassage(Interface.this.writtenList, Interface.this.passageList));
					} catch (InterruptedException e1) {
						
						e1.printStackTrace();
					}
					//send to server
					Thread word = new Thread(new Runnable() {

						@Override
						public void run() {
						
							String result = communicator.sendWord(sessionID,matchID,sendWord);

							/*if (!result.contains("failed")) {

								//loginReceived(result);
							} else {
								System.out.println("Login failed");
								loginStatus.setText("Login Failed");
							}*/
						}

					});
					word.start();
				}
				else {
					this.input.setForeground(Color.RED);
				}
			}
			else {
				if(!isFront(input, passageList.get(0))) {
					this.input.setForeground(Color.RED);
				}
				else {
					this.input.setForeground(Color.BLACK);
				}
			}
		}
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	String updatePassage(LinkedList<String> green, LinkedList<String> black) {
		//<p style="color:red">
		//Interface.this.passage.setText("<html>" + passage + "</html>");
		String greenString = "";
		for (int i = 0;i<green.size();i++) {
			greenString+=green.get(i)+" ";
		}
		String blackString = "";
		for (int i = 0;i<black.size();i++) {
			blackString+=black.get(i)+" ";
		}
		return "<html>"+"<span style = \"color:green\">"+greenString+"</span>"+"<span>"+blackString+"</span>"+"</html>";
	}
	
	public void loginReceived(String loginKey) {
		System.out.println("loginkey: " + loginKey);
		this.loginKey = loginKey;
		this.loginStatus.setText("Your Login Key: " + this.loginKey);
	}

	// returns a string array. first one is the match id, second is the thread id.
	// It loops until it times out or gets the game.
	public String[] getGame(UUID sessionID) {

		for (int i = 0; i < 30; i++) {
			String result = communicator.queryMatch(sessionID);

			if (result != null) {
				// regex for a space. the server returns "matchid threadid"
				String[] splitStr = result.split("\\s+");

				return splitStr;
			} else {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			}
		}
		return null;

	}

	// connects to the match. returns true if it does, false if not.
	public boolean connectMatch(UUID matchID, UUID threadID) {

		boolean result = communicator.connectToMatch(sessionID, matchID, threadID);

		if (result == true) {

			return true;
		} else {
			return false;

		}
	}

	// fetches the opponent name.
	public String getOpponentName(UUID sessionID, UUID matchID) {

		String result = communicator.getOpponentName(sessionID, matchID);

		if (result != null) {

			return result;
		} else {
			return null;

		}

	}

	public String getPassage(UUID sessionID, UUID matchID) {
		String result = communicator.getPassage(sessionID, matchID);

		if (result != null) {

			return result;
		} else {
			return null;

		}
	}

	public String getStatus(UUID sessionID, UUID matchID) {
		String result = communicator.getMatchStatus(sessionID, matchID);

		if (result != null) {

			return result;
		} else {
			return null;

		}
	}
	public LinkedList<String> splitList(String passage){
		String[] split = passage.split("\\s+");
		LinkedList<String> list = new LinkedList<String>();
		for(int i = 0;i<split.length;i++) {
			list.add(split[i]);
		}
		return list;
	}
	
	public static boolean isFront(String compare, String base) {
		if(compare.length()>base.length()) {
			return false;
		}
		for(int i = 0;i<compare.length();i++) {
			if(compare.charAt(i)!=base.charAt(i)) {
				return false;
			}
		}
		return true;
}
	
}
