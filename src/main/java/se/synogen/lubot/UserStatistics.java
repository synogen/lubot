package se.synogen.lubot;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;

public class UserStatistics implements Serializable {

	private static final long serialVersionUID = 1;

	private String nick;
	
	private Duration totalChannelTime = Duration.ZERO;
	
	private Long totalCharactersWritten = 0l;
		
	private LocalTime lastTrackedTime;
	
	
	private transient Thread timeTracker;
	
	public UserStatistics(String nick) {
		this.nick = nick;
	}
	
	public void addChannelTime(Duration duration) {
		totalChannelTime = totalChannelTime.plusNanos(duration.toNanos());
	}
	
	public void addCharactersWritten(long amount) {
		totalCharactersWritten += amount;
	}
	
	public String getNick() {
		return nick;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}

	public void startTrackingTime() {
		if (timeTracker != null && timeTracker.isAlive()) {
			// already tracking
		} else {
			lastTrackedTime = LocalTime.now();
			timeTracker = new Thread(new Runnable() {
				
				public void run() {
					while (true) {
						try {
							Thread.sleep(1000);
							LocalTime now = LocalTime.now();
							addChannelTime(Duration.between(lastTrackedTime, now));
							lastTrackedTime = now;
						} catch (InterruptedException e) {
							// add accumulated time before final exit
							LocalTime now = LocalTime.now();
							addChannelTime(Duration.between(lastTrackedTime, now));
							lastTrackedTime = now;
						}
						
					}
				}
			}, nick + " tracking thread");
			timeTracker.start();
		}
	}
	
	public void stopTrackingTime() {
		if (timeTracker != null && timeTracker.isAlive()) {
			timeTracker.interrupt();
			timeTracker = null;
		}
	}
	
	public boolean isTracked() {
		return timeTracker != null && timeTracker.isAlive();
	}
	
	public Duration getTotalChannelTime() {
		return totalChannelTime;
	}
	
	public Long getTotalCharactersWritten() {
		return totalCharactersWritten;
	}
}
