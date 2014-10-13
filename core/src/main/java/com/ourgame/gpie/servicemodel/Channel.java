package com.ourgame.gpie.servicemodel;

import java.net.InetAddress;

import com.ourgame.gpie.NotImplementedException;

public class Channel extends ServiceObject {

	private SessionState session;

	public Channel(ChannelFactory factory) {
	}

	public InetAddress getLocalAddress() {
		throw new NotImplementedException();
	}

	public InetAddress getRemoteAddress() {
		throw new NotImplementedException();
	}

	public SessionState getSession() {
		return this.session;
	}

	public void send(Message message) {
		throw new NotImplementedException();
	}

	public void send(Message message, long timeout) {
		throw new NotImplementedException();
	}

	public ServiceFuture sendAsync(Message message) {
		throw new NotImplementedException();
	}

	public ServiceFuture sendAsync(Message message, long timeout) {
		throw new NotImplementedException();
	}

	public Message request(Message message) {
		throw new NotImplementedException();
	}

	public Message request(Message message, long timeout) {
		throw new NotImplementedException();
	}

	public ServiceFuture requestAsync(Message message) {
		throw new NotImplementedException();
	}

	public ServiceFuture requestAsync(Message message, long timeout) {
		throw new NotImplementedException();
	}

	@Override
	public long getDefaultOpenTimeout() {
		return 30;
	}

	@Override
	public long getDefaultCloseTimeout() {
		return 30;
	}

	public long getDefaultRequestTimeout() {
		return 30;
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
