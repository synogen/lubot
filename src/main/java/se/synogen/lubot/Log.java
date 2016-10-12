package se.synogen.lubot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.swing.JTextArea;

public class Log {
	
	private static JTextArea guiLog; 
	
	public static void log(String text) {
		String logtext = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()) + " " + text;
		System.out.println(logtext);
		if (guiLog != null) {
			guiLog.append(logtext + "\n");
		}
	}
	
	public static void setGuiLog(JTextArea guiLog) {
		Log.guiLog = guiLog;
	}
}
