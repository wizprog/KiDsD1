package components;

public class ClearThread implements Runnable {
	
	private long waitTime;
	private ResultRetrieverThreadPool rrtp;

	public ClearThread(long waitTime, ResultRetrieverThreadPool rrtp) {
		super();
		this.waitTime = waitTime;
		this.rrtp = rrtp;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}


	@Override
	public void run() {
		while(true) {
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
