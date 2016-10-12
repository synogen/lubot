package se.synogen.lubot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
	
	private static HashMap<String, UserStatistics> users;
	
	// TODO
	// - statistics about users (actively check every few minutes if the user list has changed since no join event seems to exist)
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
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
			// write user statistics
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("userstatistics"));
			output.writeObject(users);
			output.close();
			lubot.close();
		}
	}
	
	public static String getUser() {
		return user;
	}

	public static HashMap<String, UserStatistics> getUsers() {
		return users;
	}
}
