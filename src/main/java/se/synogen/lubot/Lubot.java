package se.synogen.lubot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;

/**
 * Main lubot class used to start the bot
 * @author server
 *
 */
public class Lubot {

	private static String user;
	private static String auth;
	
	private static GUI gui;
	
	private static HashMap<String, UserStatistics> users = new HashMap<String, UserStatistics>();
	
	// TODO
	// - log chat to file (be careful to log rest of chat when lubot quits!)
	
	@SuppressWarnings("unchecked")
	public static void init(GUI gui) throws IOException, ClassNotFoundException {
		Log.setLogFile(new File("log.txt"));
		
		Lubot.gui = gui;
		
		// load configuration from file
		Log.log("Loading configuration...");
		File configFile = new File("config.txt");
		if (configFile.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(configFile));
			user = reader.readLine();
			auth = reader.readLine();
			reader.close();
			Log.log("Configuration loaded");
		} else {
			Log.log("No configuration found");
			System.exit(0);
		}
		
		// init configuration
		Configuration config = new Configuration.Builder()
				.addServer("irc.twitch.tv")
				.setOnJoinWhoEnabled(false) // twitch specific
				.setAutoNickChange(false) // twitch specific
				.addCapHandler(new EnableCapHandler("twitch.tv/membership")) // twitch specific
				.setAutoReconnect(true)
				.setAutoReconnectAttempts(20)
				.setAutoReconnectDelay(10000)
				.setName(user)
				.setLogin(user)
				.setServerPassword(auth)
				.addAutoJoinChannel("#" + user)
				.addListener(new IrcEventHandler())
				.buildConfiguration();
		
		// load user statistics
		if (new File("userstatistics").exists()) {
			ObjectInputStream input = new ObjectInputStream(new FileInputStream("userstatistics"));
			users = (HashMap<String, UserStatistics>)input.readObject();
			input.close();
		} else {
			users = new HashMap<String, UserStatistics>();
		}
		((GUI.NickListModel)Lubot.getGui().getListChatUsers().getModel()).fireContentsChanged();
				
		// shutdown hook for writing statistics
		Runtime.getRuntime().addShutdownHook(new ShutdownThread());
		
		// start bot
		PircBotX lubot = new PircBotX(config);
		try {
			Log.log("Connecting...");
			lubot.startBot();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		} finally {
			Log.log("Exiting lubot...");
			lubot.close();
		}
	}
	
	private static class ShutdownThread extends Thread {
		@Override
		public void run() {
			try {
				Lubot.writeUserStatistics();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void writeUserStatistics() throws FileNotFoundException, IOException {
		// write user statistics
		for (UserStatistics stats : getUsers().values()) {
			stats.stopTrackingTime();
		}
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("userstatistics"));
		output.writeObject(getUsers());
		output.close();
	}
	
	public static String getUser() {
		return user;
	}

	public static HashMap<String, UserStatistics> getUsers() {
		return users;
	}
	
	public static GUI getGui() {
		return gui;
	}
	
}
