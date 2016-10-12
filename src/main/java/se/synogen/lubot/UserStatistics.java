package se.synogen.lubot;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

public class UserStatistics implements Serializable {

	private static final long serialVersionUID = 1;

	private String nick;
	
	private Duration totalChannelTime = Duration.ZERO;
	
	private Long totalCharactersWritten = 0l;
	
	private Thread timeTracker;
	
	private Instant lastTrackedTime;
	
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
			
		} else {
			lastTrackedTime = Instant.now();
			timeTracker = new Thread(new Runnable() {
				
				public void run() {
					while (true) {
						try {
							Thread.sleep(1000);
							Instant now = Instant.now();
							addChannelTime(Duration.between(lastTrackedTime, now));
							lastTrackedTime = now;
						} catch (InterruptedException e) {
							// add accumulated time before final exit
							Instant now = Instant.now();
							addChannelTime(Duration.between(lastTrackedTime, now));
							lastTrackedTime = now;
						}
						
					}
				}
			});
			timeTracker.start();
		}
	}
	
	public void stopTrackingTime() {
		if (timeTracker != null && timeTracker.isAlive()) {
			timeTracker.interrupt();
			timeTracker = null;
		}
	}
}
