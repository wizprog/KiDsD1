package components;

public class ClearThread implements Runnable {
	
	private long waitTime;
	private ResultRetrieverThreadPool rrtp;
	private boolean shutdown;

	public ClearThread(long waitTime, ResultRetrieverThreadPool rrtp) {
		super();
		this.waitTime = waitTime;
		this.rrtp = rrtp;
		this.shutdown = false;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	@Override
	public void run() {
		while(!this.shutdown) {
			try {
				Thread.sleep(this.waitTime);
				rrtp.clearWebResultData();
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}
