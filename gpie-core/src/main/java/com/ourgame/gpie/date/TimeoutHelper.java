package com.ourgame.gpie.date;

public class TimeoutHelper {
	private long timeout;
	private boolean deallineSet = false;
	private long dealline;

	public TimeoutHelper(long timeout) {
		this.timeout = timeout;
		this.dealline = Long.MAX_VALUE;
		this.deallineSet = (timeout == Long.MAX_VALUE);
	}

	public long getRemainingTime() {
		if (!this.deallineSet) {
			this.setDealline();
			return this.timeout;
		}

		if (this.dealline == Long.MAX_VALUE) {
			return Long.MAX_VALUE;
		} else {
			long span = this.dealline - System.currentTimeMillis();
			return span >= 0 ? span : 0;
		}
	}

	private void setDealline() {
		this.dealline = System.currentTimeMillis() + this.timeout;
	}

	public long getOriginTimeout() {
		return this.timeout;
	}
}
