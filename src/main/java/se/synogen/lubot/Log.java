package se.synogen.lubot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Log {
	public static void log(String text) {
		System.out.println(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()) + " " + text);
	}
}
