package com.ourgame.gpie.servicemodel;

import com.ourgame.gpie.servicemodel.configuration.GpieConfiguration;


public class ServiceHost extends ServiceObject {
	
	public ServiceHost(GpieConfiguration cfg) {
	}
	
	@Override
	public long getDefaultOpenTimeout() {
		return 30L;
	}

	@Override
	public long getDefaultCloseTimeout() {
		return 30L;
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
