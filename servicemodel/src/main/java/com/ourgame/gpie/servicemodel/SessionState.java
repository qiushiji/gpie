package com.ourgame.gpie.servicemodel;

import java.util.Set;

import org.apache.mina.core.session.IoSession;

public class SessionState {

	private IoSession session;

	public SessionState(IoSession session) {
		this.session = session;
	}

	public long getId() {
		return this.session.getId();
	}

	public Object get(Object key) {
		return this.session.getAttribute(key);
	}

	public Set<Object> getAllKeys() {
		return this.session.getAttributeKeys();
	}

	public Object get(Object key, Object defaultValue) {
		return this.session.getAttribute(key, defaultValue);
	}
}
