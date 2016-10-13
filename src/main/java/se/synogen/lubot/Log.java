package se.synogen.lubot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.swing.JTextArea;

public class Log {
	
	private static JTextArea guiLog; 
	private static File logFile;
	
	public static void log(String text) {
		String logtext = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()) + " " + text;
		System.out.println(logtext);
		if (guiLog != null) {
			guiLog.append("\n" + logtext);
			guiLog.setCaretPosition(guiLog.getDocument().getLength());
		}
		if (logFile != null) {
			if (!logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileWriter logWriter;
			try {
				// log without buffering for now, won't have to worry about stuff not getting written and chat is really slow right now, too
				logWriter = new FileWriter(logFile, true);
				logWriter.append(logtext + "\n");
				logWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void setGuiLog(JTextArea guiLog) {
		Log.guiLog = guiLog;
	}
	
	public static void setLogFile(File logFile) {
		Log.logFile = logFile;
	}
}
