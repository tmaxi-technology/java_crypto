package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import connection.message;
import model.Message;
import model.User;

import javax.swing.JTextField;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

public class Conversation extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JButton btnNewButton;
	private JPanel panel;
	private JList<Message> messageList;
	private DefaultListModel<Message> messageListModel;

	boolean isWelcome=false;
	private JLabel lblTitle;
	private JLabel lblNewLabel;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
//					TestHi frame = new TestHi();
//					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Conversation(Socket user, Socket client, String title, User userInfo, User clientInfo) {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		if(user!=null)
			setBounds(200, 200, 600, 500);
		else if(client!=null)
			setBounds(200, 100, 600, 500);

        setResizable(false);                
		if(user!=null)
			setTitle("Conversation to "+clientInfo.getName());
		else if(client!=null)
			setTitle("Conversation to "+userInfo.getName());
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		if(!isWelcome)
			setVisible(false);
		
		textField = new JTextField();
		textField.setBounds(10, 418, 454, 32);
		contentPane.add(textField);
		textField.setColumns(10);
		
		btnNewButton = new JButton("Send");
		btnNewButton.setBounds(474, 418, 100, 32);
		contentPane.add(btnNewButton);
		
		panel = new JPanel();
		panel.setBounds(10, 54, 564, 328);
		contentPane.add(panel);

		messageListModel = new DefaultListModel<Message>();
		messageList = new JList<Message>(messageListModel);
		messageList.setFixedCellHeight(40);
		messageList.setFixedCellWidth(550);		
		JScrollPane scrollPane = new JScrollPane(messageList);
		panel.add(scrollPane);
		messageList.setCellRenderer(new DefaultListCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				// TODO Auto-generated method stub
				Message message=(Message)value;
				ImageIcon imageIcon=new ImageIcon((new ImageIcon(this.getClass().getResource("/images/" + "user" + ".png")).getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				if(message.getIsSendBy()==Message.USER) {
					setHorizontalAlignment(RIGHT);
					setIcon(null);
				}
				else {
					setHorizontalAlignment(LEFT);
					setIcon(imageIcon);
				}
		        setText(message.getContent());
	            setBackground(Color.WHITE);
				return this;
			}
		});
		
		lblTitle = new JLabel("Chat");
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 564, 32);
		contentPane.add(lblTitle);
		
		lblNewLabel = new JLabel("Type your message here");
		lblNewLabel.setBounds(10, 393, 174, 26);
		contentPane.add(lblNewLabel);
		try {
			if(user!=null)
				new ChatThread(user).start();
			else if(client!=null) {
				new ChatThread(client).start();
			}
		}catch(Exception ex) {
       		ex.printStackTrace();
		}
	}

    class ChatThread extends Thread {
    	Socket socket;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

    	public ChatThread(Socket socket) throws IOException{
			// TODO Auto-generated constructor stub
    		this.socket=socket;
    		outputStream = new ObjectOutputStream(socket.getOutputStream());
    		inputStream = new ObjectInputStream(socket.getInputStream());
		}
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
//    		super.run();
    		btnNewButton.addActionListener(new ActionListener() {

    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				// TODO Auto-generated method stub
    				try {
                        message toSend = new message(textField.getText().getBytes());
	    				messageListModel.addElement(new Message(textField.getText(), Message.USER));
                        textField.setText(null);
                        outputStream.writeObject(toSend);
    				}catch(Exception ex) {
    		       		ex.printStackTrace();					
    				}
    			}			
    		});
    		new ChatReceiveFromThread().start();
    	}
    	
        class ChatReceiveFromThread extends Thread {
        	@Override
        	public void run() {
        		// TODO Auto-generated method stub
//        		super.run();
        		while(true){
	        		try {
	        			if(!isWelcome) {
	        				setVisible(true);
	        				isWelcome=true;
	        			} else {
		        			message m = (message) inputStream.readObject();
		        			System.out.println(new String(m.getData()));
		    				messageListModel.addElement(new Message(new String(m.getData()), Message.CLIENT));
	        			}
	        		}catch(Exception ex) {
			       		ex.printStackTrace();		
			       		return;
	        		}               
        		}
        	}
        }
    }
    class UserThread extends Thread {
    	Socket socket;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        boolean welcome=false;
    	public UserThread(Socket socket) throws IOException{
			// TODO Auto-generated constructor stub
    		this.socket=socket;
    		outputStream = new ObjectOutputStream(socket.getOutputStream());
    		inputStream = new ObjectInputStream(socket.getInputStream());
		}
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
//    		super.run();    
    		btnNewButton.addActionListener(new ActionListener() {

    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				// TODO Auto-generated method stub
    				try {
                        message toSend = new message(textField.getText().getBytes());
	    				messageListModel.addElement(new Message(textField.getText(), Message.USER));
                        textField.setText(null);
                        outputStream.writeObject(toSend);
    				}catch(Exception ex) {
    		       		ex.printStackTrace();					
    				}
    			}			
    		});
    		new UserReceiveFromThread().start();
    	}
    	        
        class UserReceiveFromThread extends Thread {
        	@Override
        	public void run() {
        		// TODO Auto-generated method stub
//        		super.run();
        		while(true){
	        		try {
	        			if(!isWelcome) {
	        				setVisible(true);
	        				isWelcome=true;
	        			} else {
		        			message m = (message) inputStream.readObject();
		        			System.out.println(new String(m.getData()));
		    				messageListModel.addElement(new Message(new String(m.getData()), Message.CLIENT));
	        			}
	        		}catch(Exception ex) {
			       		ex.printStackTrace();		
			       		return;
	        		}               
        		}
        	}
        }
    }    
    
    class ClientThread extends Thread {
    	Socket socket;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
    	public ClientThread(Socket socket) throws IOException{
			// TODO Auto-generated constructor stub
    		this.socket=socket;
    		outputStream = new ObjectOutputStream(socket.getOutputStream());
    		inputStream = new ObjectInputStream(socket.getInputStream());
		}
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
//    		super.run();    
    		btnNewButton.addActionListener(new ActionListener() {

    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				// TODO Auto-generated method stub
    				try {
                        message toSend = new message(textField.getText().getBytes());
	    				messageListModel.addElement(new Message(textField.getText(), Message.USER));
                        textField.setText(null);
                        outputStream.writeObject(toSend);
    				}catch(Exception ex) {
    		       		ex.printStackTrace();					
    				}
    			}			
    		});
    		new ClientReceiveFromThread().start();
    	}
    	
        
        class ClientReceiveFromThread extends Thread {
        	@Override
        	public void run() {
        		// TODO Auto-generated method stub
//        		super.run();
        		while(true){
	        		try {
	        			message m = (message) inputStream.readObject();
	        			System.out.println(new String(m.getData()));
	    				messageListModel.addElement(new Message(new String(m.getData()), Message.CLIENT));
	        		}catch(Exception ex) {
			       		ex.printStackTrace();		
			       		return;
	        		}               
        		}
        	}
        }
    }    
}
