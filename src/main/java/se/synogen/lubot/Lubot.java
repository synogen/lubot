package se.synogen.lubot;

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

	public static void main(String[] args) {
		Configuration config = new Configuration.Builder()
				.addServer("irc.twitch.tv")
				.setAutoReconnect(true)
				.setAutoReconnectAttempts(20)
				.setAutoReconnectDelay(10000)
				.setName("synogen2")
				.setLogin("synogen2")
				.setServerPassword("")
				.addAutoJoinChannel("#synogen2")
				.addListener(new IrcEventHandler())
				.buildConfiguration();
				
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
