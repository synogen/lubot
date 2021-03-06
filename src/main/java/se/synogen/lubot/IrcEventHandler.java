package se.synogen.lubot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

/**
 * Handles all events from the IRC server
 * @author server
 *
 */
public class IrcEventHandler extends ListenerAdapter {
	
	private final LocalDateTime START_TIME;
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH':'mm':'ss 'on' EEEE, dd 'of' MMMM yyyy", Locale.ENGLISH);
	
	public IrcEventHandler() {
		START_TIME = LocalDateTime.now();
	}

	@Override
	public void onJoin(JoinEvent event) throws Exception {
		if (event.getChannel().getName().equals("#" + Lubot.getUser())) {
			String nick = event.getUser().getNick();
			Log.log(nick + " has joined");
			
			if (!Lubot.getUsers().containsKey(nick)) {
				UserStatistics statistics = new UserStatistics(nick);
				statistics.startTrackingTime();
				Lubot.getUsers().put(nick, statistics);
				((GUI.NickListModel)Lubot.getGui().getListChatUsers().getModel()).fireContentsChanged();
			} else {
				UserStatistics statistics = Lubot.getUsers().get(nick);
				statistics.startTrackingTime();
				((GUI.NickListModel)Lubot.getGui().getListChatUsers().getModel()).fireContentsChanged();
			}
		}
	}
	
	@Override
	public void onPart(PartEvent event) throws Exception {
		if (event.getChannel().getName().equals("#" + Lubot.getUser())) {
			String nick = event.getUser().getNick();
			Log.log(nick + " has left");
			
			if (Lubot.getUsers().containsKey(nick)) {
				UserStatistics statistics = Lubot.getUsers().get(nick);
				statistics.stopTrackingTime();
				((GUI.NickListModel)Lubot.getGui().getListChatUsers().getModel()).fireContentsChanged();
			}
		}
	}
	
	@Override
	public void onConnect(ConnectEvent event) throws Exception {
		Log.log("Connected.");
	}
	
	@Override
	public void onGenericMessage(GenericMessageEvent event) throws Exception {
		String nick = event.getUser().getNick();
		Log.log(nick + ": " + event.getMessage());
		if (Lubot.getUsers().containsKey(nick)) {
			UserStatistics statistics = Lubot.getUsers().get(nick);
			statistics.startTrackingTime();
			((GUI.NickListModel)Lubot.getGui().getListChatUsers().getModel()).fireContentsChanged();
			statistics.addCharactersWritten(event.getMessage().length());
		} else {
			UserStatistics statistics = new UserStatistics(nick);
			statistics.startTrackingTime();
			Lubot.getUsers().put(nick, statistics);
			((GUI.NickListModel)Lubot.getGui().getListChatUsers().getModel()).fireContentsChanged();
			statistics.addCharactersWritten(event.getMessage().length());
		}
		if (event.getMessage().startsWith("!hello")) {
			event.respond("Why hello there!");
		} else if (event.getMessage().startsWith("!lutime")) {
			event.respond("The local time is " + LocalDateTime.now().format(formatter));
		} else if (event.getMessage().startsWith("!uptime")) {
			event.respond("The stream has been up for " + DurationFormatUtils.formatDurationWords(Duration.between(START_TIME, LocalDateTime.now()).toMillis(), true, false) + " (since " + START_TIME.format(formatter) + ")");
		} else if (event.getMessage().startsWith("!stats")) {
			if (Lubot.getUsers().containsKey(nick)) {
				UserStatistics stats = Lubot.getUsers().get(nick);
				event.respond("You have been in stream chat for a total of " + DurationFormatUtils.formatDurationWords(stats.getTotalChannelTime().toMillis(), true, false) + ". During this time you have written a total of " + stats.getTotalCharactersWritten() + " characters.");				
			} else {
				event.respond("ERROR: No stats were found for you.");
			}
		}
	}
	
}
