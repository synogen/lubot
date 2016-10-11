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
			Log.log(event.getUser().getNick() + " has joined");
		}
	}
	
	@Override
	public void onPart(PartEvent event) throws Exception {
		if (event.getChannel().getName().equals("#" + Lubot.getUser())) {
			Log.log(event.getUser().getNick() + " has left");
		}
	}
	
	@Override
	public void onConnect(ConnectEvent event) throws Exception {
		Log.log("Connected.");
	}
	
	@Override
	public void onGenericMessage(GenericMessageEvent event) throws Exception {
		Log.log(event.getUser().getNick() + ": " + event.getMessage());
		if (event.getMessage().startsWith("!hello")) {
			event.respond("Why hello there!");
		} else if (event.getMessage().startsWith("!lutime")) {
			event.respond("The local time is " + LocalDateTime.now().format(formatter));
		} else if (event.getMessage().startsWith("!uptime")) {
			event.respond("The stream has been up for " + DurationFormatUtils.formatDurationWords(Duration.between(START_TIME, LocalDateTime.now()).toMillis(), true, false) + " (since " + START_TIME.format(formatter) + ")");
		}
	}
	
}
