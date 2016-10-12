package se.synogen.lubot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
	// test
	private static String user;
	private static String auth;
	
	// TODO
	// - statistics about users (actively check every few minutes if the user list has changed since no join event seems to exist)
	
	public static void main(String[] args) throws IOException {
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
	
	public static String getUser() {
		return user;
	}

}
