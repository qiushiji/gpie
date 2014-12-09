package com.ourgame.gpie.servicemodel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class ServiceHost extends ServiceObject {

	private URI address;
	private String name;

	private NioSocketAcceptor acceptor;

	public ServiceHost(String name) {
		this(name, null);
	}

	public ServiceHost(String name, URI address) {
		this.name = name;
		this.address = address;

		this.initialize();
	}

	private void initialize() {
		this.acceptor = new NioSocketAcceptor();
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
		try {
			this.acceptor.bind(new InetSocketAddress(this.address.getPath(), this.address.getPort()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void onClose(long timeout) {
		this.acceptor.dispose(true);
	}

	@Override
	protected void onAbort() {
	}
}
