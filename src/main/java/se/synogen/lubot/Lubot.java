package se.synogen.lubot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

/**
 * Main lubot class used to start the bot
 * @author server
 *
 */
public class Lubot {

	private static String user;
	private static String auth;
	
	// TODO
	// - statistics about users (actively check every few minutes if the user list has changed since no join event seems to exist)
	
	public static void main(String[] args) throws IOException {
		// load configuration from file
		System.out.println("Loading configuration...");
		File configFile = new File("config.txt");
		if (configFile.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(configFile));
			user = reader.readLine();
			auth = reader.readLine();
			reader.close();
			System.out.println("Configuration loaded");
		} else {
			System.out.println("No configuration found");
			System.exit(0);
		}
		
		
		// init configuration
		Configuration config = new Configuration.Builder()
				.addServer("irc.twitch.tv")
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
			System.out.println("Connecting...");
			lubot.startBot();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Exiting lubot...");
			lubot.close();
		}
	}

}
