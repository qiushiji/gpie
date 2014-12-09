package com.ourgame.gpie.servicemodel;

import org.apache.mina.core.session.IoSession;

import com.ourgame.gpie.NotImplementedException;

public abstract class ClientBase {

	public IoSession session;

	public void send(Message message) {
		throw new NotImplementedException();
	}

	public Message request(Message message) {
		throw new NotImplementedException();
	}

	public void open() {
		throw new NotImplementedException();
	}

	public void close() {
		throw new NotImplementedException();
	}
}
