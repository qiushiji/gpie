package com.ourgame.gpie.servicemodel;

public class ServiceHost extends ServiceObject {

	@Override
	public long getDefaultOpenTimeout() {
		return 0;
	}

	@Override
	public long getDefaultCloseTimeout() {
		return 0;
	}

	@Override
	protected void onOpen(long timeout) {
	}

	@Override
	protected void onClose(long timeout) {
	}

	@Override
	protected void onAbort() {
	}
}
