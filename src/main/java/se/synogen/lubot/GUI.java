package se.synogen.lubot;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class GUI {

	private JFrame frmLubot;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmLubot.setVisible(true);
					
					// start lubot
					new Thread(new Runnable() {
						public void run() {
							try {
								Lubot.init();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLubot = new JFrame();
		frmLubot.setTitle("Lubot");
		frmLubot.setBounds(100, 100, 450, 300);
		frmLubot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextArea txtChatLog = new JTextArea();
		txtChatLog.setEditable(false);
		frmLubot.getContentPane().add(txtChatLog, BorderLayout.CENTER);
		Log.setGuiLog(txtChatLog);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		frmLubot.getContentPane().add(btnExit, BorderLayout.SOUTH);
		
		
	}

}
