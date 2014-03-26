package me.thelunarfrog.frogannounce;

import me.thelunarfrog.frogannounce.exceptions.InvalidWorldException;

import java.util.List;

public class IndependentAnnouncement extends Announcement implements Runnable{
	private boolean isRunning;
	private int timer;

	public IndependentAnnouncement(final String text, final List<String> groups, final List<String> worlds, final List<String> commands, final int time){
		super(text, groups, worlds, commands);
		this.isRunning = true;
		this.timer = time;
	}

	@SuppressWarnings("unused") // for external use
	public int getInterval(){
		return this.timer;
	}

	@Override
	public boolean isTimedIndividually(){
		return true;
	}

	public boolean isRunning(){
		return this.isRunning;
	}

	public void stop(){
		this.isRunning = false;
		Thread.currentThread().interrupt();
	}

	@Override
	public void run(){
			try{
				while(this.isRunning()){
					Thread.sleep(this.timer);
					try{
						super.execute();
					}catch(InvalidWorldException e){
						//as much as I'm against I/O in classes, Runnable gives me little choice
						FrogAnnounce.getInstance().sendConsoleMessage(FrogAnnounce.Severity.SEVERE, e.getMessage());
					}
				}
			}catch(InterruptedException e){
				// do nothing
			}
	}
}