package com.ourgame.gpie.servicemodel;

import com.ourgame.gpie.NotImplementedException;

public class ServiceFuture {

	public Object get() {
		throw new NotImplementedException();
	}

	public void set(Object result) {
		throw new NotImplementedException();
	}

	public void cancel() {
		throw new NotImplementedException();
	}

	public boolean isDone() {
		throw new NotImplementedException();
	}

	public Throwable getException() {
		throw new NotImplementedException();
	}

	public void await() {		
		throw new NotImplementedException();
	}

	public Object awaitUninterruptibly() {
		throw new NotImplementedException();
	}
}
