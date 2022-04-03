package timer;

public class TimerThread implements Runnable {
	
	private TimerController timerController;
	private long time;
	
	public TimerThread(long time, TimerController timerController) {
		this.timerController = timerController;
		this.time = time;
	}
	
	/**
	 * begin the timer
	 */
	private void start() {
		try {
			Thread.sleep(time);
			timerController.finishRunning(); // callback when timer is complete
		} catch (InterruptedException e) {// catch an interrupt
			timerController.finishRunning(); // callback because timer was stopped
		}
	}
	
	/**
	 * continuously wait until interrupt occurs
	 */
	public void run() {
		while(true) {
			try {
				Thread.sleep(999999999);
			} catch (InterruptedException e) { // catch an interrupt to start the timer
				start();
			}
		}
	}
}
