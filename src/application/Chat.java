package application;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import connection.message;
import model.User;
import sercure.AES;
import sercure.RSA;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.crypto.SecretKey;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

public class Chat extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int CONNECTION_SCREEN = 0;
	public static final int WELCOME_SCREEN = 1;
	public static final int LOGIN_SCREEN = 2;
	public static final int SIGNUP_SCREEN = 3;
	public static final int DASHBOARD_SCREEN = 4;
	public static String IV = "AAAAAAAAAAAAAAAA";

	private JPanel contentPane;
	private JPanel panelConnection;
	private JButton btnConnectionConnect;
	private JProgressBar progressConnect;
	private JMenuBar menuBar;
	private JMenu mnUser;
	private JMenuItem mntmHelpAbout;
	private JMenuItem mntmUserLogout;
	private JMenuItem mntmUserClose;
	private JMenu mnHelp;

	//server
	Socket serverSocket;
	String serverIP;
	int serverPort;
	byte[] serverPublicKey;
	ObjectOutputStream serverOutput;
	ObjectInputStream serverInput;
	
	//user
	SecretKey AESkey;
	User user;
	JList<User> userList;
	DefaultListModel<User> userListModel;
	int mHoveredJListIndex = -1;
	
	int conversationPort;
	int screen;
	int percent;
	private JPanel panelWelcome;
	private JLabel lblNewLabel_1;
	private JButton btnWelcomeSignup;
	private JButton btnWelcomeLogin;
	private JPanel panelLogin;
	private JLabel lblNewLabel_2;
	private JPanel panelSignup;
	private JLabel lblNewLabel_3;
	private JButton btnLoginSubmit;
	private JButton btnLoginCancel;
	private JButton btnSignupSubmit;
	private JButton btnSignupCancel;
	private JPanel panelDashboard;
	private JPanel panelUserList;
	private JTextField textSearch;
	private JLabel lblNewLabel_4;
	private JTextField textLoginUserName;
	private JLabel lblNewLabel_5;
	private JPasswordField passwordLogin;
	private JLabel lblNewLabel_6;
	private JLabel lblNewLabel_7;
	private JTextField textSignupUserName;
	private JPasswordField passwordSignup;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Chat frame = new Chat();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Chat() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 600);
        setResizable(false);                
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnUser = new JMenu("User");
		menuBar.add(mnUser);
		
		mntmUserLogout = new JMenuItem("Logout");
		mnUser.add(mntmUserLogout);
		
		mntmUserClose = new JMenuItem("Close");
		mnUser.add(mntmUserClose);
		
		mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		mntmHelpAbout = new JMenuItem("About");
		mnHelp.add(mntmHelpAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panelConnection = new JPanel();
		panelConnection.setBounds(10, 11, 424, 527);
		contentPane.add(panelConnection);
		panelConnection.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Connect to Server");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(12, 12, 400, 200);
		panelConnection.add(lblNewLabel);
		
		btnConnectionConnect = new JButton("Connect");
		btnConnectionConnect.setBounds(160, 223, 100, 30);
		panelConnection.add(btnConnectionConnect);
		
		progressConnect = new JProgressBar();
		progressConnect.setBounds(12, 264, 400, 30);
		panelConnection.add(progressConnect);
		
		panelWelcome = new JPanel();
		panelWelcome.setBounds(10, 11, 424, 527);
		contentPane.add(panelWelcome);
		panelWelcome.setLayout(null);
		
		lblNewLabel_1 = new JLabel("Welcome RSA Chat");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblNewLabel_1.setBounds(10, 11, 404, 200);
		panelWelcome.add(lblNewLabel_1);
		
		btnWelcomeSignup = new JButton("Signup");
		btnWelcomeSignup.setBounds(210, 222, 100, 30);
		panelWelcome.add(btnWelcomeSignup);
		
		btnWelcomeLogin = new JButton("Login");
		btnWelcomeLogin.setBounds(100, 222, 100, 30);
		panelWelcome.add(btnWelcomeLogin);
		
		panelLogin = new JPanel();
		panelLogin.setBounds(10, 11, 424, 527);
		contentPane.add(panelLogin);
		panelLogin.setLayout(null);
		
		lblNewLabel_2 = new JLabel("Login");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(10, 11, 404, 200);
		panelLogin.add(lblNewLabel_2);
		
		btnLoginSubmit = new JButton("Submit");
		btnLoginSubmit.setBounds(100, 304, 100, 30);
		panelLogin.add(btnLoginSubmit);
		
		btnLoginCancel = new JButton("Cancel");
		btnLoginCancel.setBounds(210, 304, 100, 30);
		panelLogin.add(btnLoginCancel);
		
		lblNewLabel_4 = new JLabel("User Name");
		lblNewLabel_4.setBounds(10, 222, 100, 30);
		panelLogin.add(lblNewLabel_4);
		
		textLoginUserName = new JTextField();
		textLoginUserName.setBounds(100, 222, 314, 30);
		panelLogin.add(textLoginUserName);
		textLoginUserName.setColumns(10);
		
		lblNewLabel_5 = new JLabel("Password");
		lblNewLabel_5.setBounds(10, 260, 100, 30);
		panelLogin.add(lblNewLabel_5);
		
		passwordLogin = new JPasswordField();
		passwordLogin.setBounds(100, 263, 314, 30);
		panelLogin.add(passwordLogin);
		
		panelSignup = new JPanel();
		panelSignup.setBounds(10, 11, 424, 527);
		contentPane.add(panelSignup);
		panelSignup.setLayout(null);
		
		lblNewLabel_3 = new JLabel("Singup");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(10, 11, 404, 200);
		panelSignup.add(lblNewLabel_3);
		
		btnSignupSubmit = new JButton("Submit");
		btnSignupSubmit.setBounds(100, 304, 100, 30);
		panelSignup.add(btnSignupSubmit);
		
		btnSignupCancel = new JButton("Cancel");
		btnSignupCancel.setBounds(210, 304, 100, 30);
		panelSignup.add(btnSignupCancel);
		
		lblNewLabel_6 = new JLabel("User Name");
		lblNewLabel_6.setBounds(10, 222, 100, 30);
		panelSignup.add(lblNewLabel_6);
		
		lblNewLabel_7 = new JLabel("Password");
		lblNewLabel_7.setBounds(10, 263, 100, 30);
		panelSignup.add(lblNewLabel_7);
		
		textSignupUserName = new JTextField();
		textSignupUserName.setBounds(100, 222, 314, 30);
		panelSignup.add(textSignupUserName);
		textSignupUserName.setColumns(10);
		
		passwordSignup = new JPasswordField();
		passwordSignup.setBounds(100, 263, 314, 30);
		panelSignup.add(passwordSignup);
		
		panelDashboard = new JPanel();
		panelDashboard.setBounds(10, 11, 424, 527);
		contentPane.add(panelDashboard);
		panelDashboard.setLayout(null);
		
		panelUserList = new JPanel();
		panelUserList.setBounds(0, 52, 424, 475);
		panelDashboard.add(panelUserList);
		
		userListModel = new DefaultListModel<>();
		userList = new JList<>(userListModel);
		userList.setFixedCellHeight(60);
		userList.setFixedCellWidth(400);		

		JScrollPane scrollPane = new JScrollPane(userList);
		panelUserList.add(scrollPane);

		textSearch = new JTextField();
		textSearch.setBounds(10, 11, 404, 30);
		panelDashboard.add(textSearch);
		textSearch.setColumns(10);
		
		initialize();
	}

	void initialize(){
		try {
			//connect to server
			serverIP="34.126.64.18";
			serverPort=8002;

			conversationPort=8888;
			screen=Chat.CONNECTION_SCREEN;
			percent=0;
			
			user=null;
//			conversationSoket=null;
			
			switchScreen();
			addEvents();
			new ConversationServerThread().start();
		}catch(Exception ex) {
       		ex.printStackTrace();
		}
	}
	
	void addEvents() {
		userList.setCellRenderer(new DefaultListCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				// TODO Auto-generated method stub
				User user=(User)value;
				String image="user";
				if(user.getState()=="offline")
					image="client";
				ImageIcon imageIcon=new ImageIcon((new ImageIcon(this.getClass().getResource("/images/" + image + ".png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH)));
				setIcon(imageIcon);
				setText(user.getName());
		        Color backgroundColor = mHoveredJListIndex == index ? Color.blue : list.getBackground();
	            setBackground(backgroundColor);
	            setForeground(list.getForeground());
				return this;
			}
		});
		userList.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
        	    Point p = new Point(e.getX(),e.getY());
        	    int index = userList.locationToIndex(p);
        	    if (index != mHoveredJListIndex) {
        	      mHoveredJListIndex = index;
        	      userList.repaint();
        	    }
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		userList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);
                int index = userList.locationToIndex(e.getPoint());
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    //handle double click event.
                    try {
                    	User client=userList.getSelectedValue();
                    	System.out.println("Client is connect>> " + client.getName());
                    	Socket socket = new Socket(client.getIp(), conversationPort);
                        Conversation conversation=new Conversation(null, socket, "client", user, client);
                        conversation.setVisible(true);
                    }catch(Exception ex) {
    		       		ex.printStackTrace();
                    }
               }
			}
		});
		mntmUserLogout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				user=null;
//				resetField();
				screen=Chat.WELCOME_SCREEN;
				switchScreen();				
			}
		});

		mntmUserClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setVisible(false); //you can't see me!
				dispose(); //Destroy the JFrame object
			}
		});
		btnConnectionConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					btnConnectionConnect.setEnabled(false);
					new ProgressConnectThread().start();
					serverSocket = new Socket(serverIP, serverPort);
					System.out.println("connection accepted " + serverSocket.getInetAddress() + " :"  + serverSocket.getPort());	
					serverOutput = new ObjectOutputStream(serverSocket.getOutputStream());
					serverInput = new ObjectInputStream(serverSocket.getInputStream());

					AESkey=AES.generateAESkey();
					new ReceiveFromServer().start();
                    byte[] encoded_AES_key = RSA.encryptMessage(AESkey.getEncoded());
                    message toSend = new message(encoded_AES_key);
                    serverOutput.writeObject(toSend);
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		btnWelcomeLogin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				screen=Chat.LOGIN_SCREEN;
				switchScreen();
			}
		});
		btnLoginSubmit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					login();
				}catch(Exception ex) {
		       		ex.printStackTrace();
				}
			}
		});
		btnLoginCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				screen=Chat.WELCOME_SCREEN;
				switchScreen();
			}
		});
		btnWelcomeSignup.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				screen=Chat.SIGNUP_SCREEN;
				switchScreen();
			}
		});
		btnSignupSubmit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					signup();
				}catch(Exception ex) {
		       		ex.printStackTrace();
				}
			}
		});
		btnSignupCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				screen=Chat.WELCOME_SCREEN;
				switchScreen();
			}
		});
	}
	
	void switchScreen() {
//		lblWelcomeUser.setVisible(screen==Chat.DASHBOARD_SCREEN);
		menuBar.setVisible(screen==Chat.DASHBOARD_SCREEN);
		panelDashboard.setVisible(screen==Chat.DASHBOARD_SCREEN);
		panelWelcome.setVisible(screen==Chat.WELCOME_SCREEN);
		panelLogin.setVisible(screen==Chat.LOGIN_SCREEN);
		panelSignup.setVisible(screen==Chat.SIGNUP_SCREEN);
		panelConnection.setVisible(screen==Chat.CONNECTION_SCREEN);			
	}

	void login() throws Exception{
		JSONObject json = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("name",textLoginUserName.getText());
		data.put("pass",passwordLogin.getText());
		json.put("cmd","login");
		json.put("data",data);

		StringWriter out = new StringWriter();
		json.writeJSONString(out);
	      
		String jsonText = out.toString();
		try {
	        byte[] encoded_message = AES.encryptMessage(jsonText, AESkey);
	        message toSend = new message(encoded_message);
	        serverOutput.writeObject(toSend);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
    
	void signup() throws IOException {
		JSONObject json = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("name",textSignupUserName.getText());
		data.put("pass",passwordSignup.getText());
		json.put("cmd","signup");
		json.put("data",data);

		StringWriter out = new StringWriter();
		json.writeJSONString(out);
	      
		String jsonText = out.toString();
		try {
	        byte[] encoded_message = AES.encryptMessage(jsonText, AESkey);
	        message toSend = new message(encoded_message);
	        serverOutput.writeObject(toSend);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	void showDashboard(JSONObject response) {
		screen=Chat.DASHBOARD_SCREEN;
		JSONObject data=(JSONObject) response.get("data");
		user=new User(data);
//		lblWelcomeUser.setText("Hi "+user.get("name").toString().toUpperCase());
		String request= "{\r\n"
				+ "\"cmd\":\"get_list_user\",\r\n"
				+ "\"data\":{\r\n"
				+ "        \"state\":\"online/offline\"\r\n"
				+ "        }\r\n"
				+ "}";
		resetField();
		try {
	        byte[] encoded_message = AES.encryptMessage(request, AESkey);
	        message toSend = new message(encoded_message);
	        serverOutput.writeObject(toSend);
		}catch(Exception ex) {
			
		}
	}

	void resetField() {		
		passwordLogin.setText(null);
		textLoginUserName.setText(null);
		passwordSignup.setText(null);
		textSignupUserName.setText(null);
	}

    class ConversationServerThread extends Thread {
    	ServerSocket conversationSocket;
    	public ConversationServerThread() throws IOException{
			// TODO Auto-generated constructor stub
    		conversationSocket = new ServerSocket(conversationPort);
    		System.out.print("Conversation listening on the port " + conversationPort + ".");
		}
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		try {
    			while(true) {
    	    		Socket socket = conversationSocket.accept();  // accepting the connection.
    	    		System.out.println(">>Accept client from IP: " + socket.getRemoteSocketAddress().toString());
			        Conversation testHi=new Conversation(socket, null, "user", user, userList.getSelectedValue());
			        testHi.setVisible(false);
    			}
    		}catch(Exception ex) {
           		ex.printStackTrace();
    		}
    	}
    }

	class ReceiveFromServer extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try{
					message m = (message) serverInput.readObject();
					if(screen==Chat.CONNECTION_SCREEN) {
						serverPublicKey=m.getData();	
						screen=Chat.WELCOME_SCREEN;
						System.out.println(serverPublicKey);
						switchScreen();
					} else {
				        byte[] msg = AES.decryptMessage(m.getData(), AESkey);
				        JSONParser parser = new JSONParser();
				        JSONObject response = (JSONObject)parser.parse(new String(msg));
			            System.out.println("CLIENT: INCOMING Message From connection.Server   >> " + response);
			            switch((String)response.get("cmd")) {
				            case "signup":
				            	if((long)response.get("status_code")==200) {
				            		showDashboard(response);
				            	} else {
				            	}
				            	break;
				            case "login":
				            	if((long)response.get("status_code")==200) {
				            		showDashboard(response);
				            	} else {
				            	}
				            	break;
				            case "get_list_user":
				            	if((long)response.get("status_code")==200) {
						            System.out.println("List>>" + response);
						            JSONArray dataJsonArray = (JSONArray)response.get("data");
						            System.out.println(dataJsonArray);
						            for(int i=0; i<dataJsonArray.size(); i++) {
					            	   JSONObject dataObj = (JSONObject)dataJsonArray.get(i);
							           User newUser=new User(dataObj);
							           userListModel.addElement(newUser);
					            	}
					            	switchScreen();
				            	} else {
				            	}
				            	break;
			            }
					}
				}catch(Exception ex) {
		       		ex.printStackTrace();
		       		return;
				}
			}
		}
	}
	
	class ProgressConnectThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			percent=0;
			while (percent <= 100) {
	            try {
	            	if(screen!=Chat.CONNECTION_SCREEN) {
						Thread.sleep(500);			
	            		break;
	            	}
					Thread.sleep(100);			
					progressConnect.setValue(percent); 
					percent++;
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
			btnConnectionConnect.setEnabled(true);
		}
	}
}
