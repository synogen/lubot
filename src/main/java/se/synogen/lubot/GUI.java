package se.synogen.lubot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

public class GUI {

	private JFrame frmLubot;
	private JList<String> listChatUsers;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final GUI window = new GUI();
					window.frmLubot.setVisible(true);
					
					// start lubot
					new Thread(new Runnable() {
						public void run() {
							try {
								Lubot.init(window);
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
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		frmLubot.getContentPane().add(btnExit, BorderLayout.SOUTH);
		
		listChatUsers = new JList<String>();
		listChatUsers.setPreferredSize(new Dimension(100, 0));
		listChatUsers.setModel(new NickListModel());
		listChatUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		frmLubot.getContentPane().add(listChatUsers, BorderLayout.EAST);
		
		JTextArea txtChatLog = new JTextArea();
		txtChatLog.setWrapStyleWord(true);
		txtChatLog.setLineWrap(true);
		txtChatLog.setEditable(false);
		
		scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		scrollPane.add(txtChatLog);
		scrollPane.setViewportView(txtChatLog);
		
		frmLubot.getContentPane().add(scrollPane, BorderLayout.CENTER);
		Log.setGuiLog(txtChatLog);
	}

	public JList<String> getListChatUsers() {
		return listChatUsers;
	}
	
	public class NickListModel extends AbstractListModel<String>
	{
		private static final long serialVersionUID = 1L;
		
		public void fireContentsChanged() {
			super.fireContentsChanged(this, 0, getSize());
		}
		
		private ArrayList<UserStatistics> getActiveList() {
			ArrayList<UserStatistics> activeList = new ArrayList<UserStatistics>();
			for (UserStatistics stat : Lubot.getUsers().values()) {
				if (stat.isTracked()) {
					activeList.add(stat);
				}
			}
			return activeList;
		}
		
		public int getSize() {
			return getActiveList().size();
		}
		public String getElementAt(int index) {
			return getActiveList().get(index).getNick();
		}
	}
}
