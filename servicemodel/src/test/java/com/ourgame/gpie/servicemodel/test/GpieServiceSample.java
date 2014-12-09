package com.ourgame.gpie.servicemodel.test;

import com.ourgame.gpie.servicemodel.ServiceHost;
import com.ourgame.gpie.servicemodel.configuration.GpieConfiguration;

public class GpieServiceSample {
	public static void main(String[] args) {
		GpieConfiguration cfg = new GpieConfiguration("gpie-cfg.xml");
		ServiceHost host = new ServiceHost(cfg);
	}
}
