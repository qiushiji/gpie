package com.ourgame.gpie.servicemodel;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ourgame.gpie.date.TimeoutHelper;

public abstract class ServiceObject {

	private Lock thisLock = new ReentrantLock();
	public ServiceObjectState state;

	public ServiceObjectState getState() {
		return this.state;
	}

	public abstract long getDefaultOpenTimeout();

	public abstract long getDefaultCloseTimeout();

	private Set<ServiceObjectListener> listeners = new CopyOnWriteArraySet<ServiceObjectListener>();

	public void open() {
		this.open(this.getDefaultOpenTimeout());
	}

	public void open(long timeout) {

		if (timeout < 0) {
			throw new IllegalArgumentException("timeout");
		}

		try {
			this.thisLock.lock();
			this.state = ServiceObjectState.Opening;
		} finally {
			this.thisLock.unlock();
		}

		boolean flag = true;

		try {
			TimeoutHelper helper = new TimeoutHelper(timeout);

			this.onOpening();
			this.onOpen(helper.getRemainingTime());
			this.onOpened();
			flag = false;
		} finally {
			if (flag) {
				this.fault();
			}
		}
	}

	protected void fault() {
		try {
			this.thisLock.lock();

			if (((this.state == ServiceObjectState.Closed) || (this.state == ServiceObjectState.Closing))
					|| (this.state == ServiceObjectState.Faulted)) {
				return;
			}

			this.state = ServiceObjectState.Faulted;

		} finally {
			this.thisLock.unlock();
		}

		this.onFaulted();
	}

//	void fault(Exception exception) {
//		try {
//			this.thisLock.lock();
//
//		} finally {
//			this.thisLock.unlock();
//		}
//
//		this.fault();
//	}

	public void close() {
		this.close(this.getDefaultCloseTimeout());
	}

	public void close(long timeout) {

		if (timeout < 0) {
			throw new IllegalArgumentException("timeout");
		}

		ServiceObjectState state;
		try {
			this.thisLock.lock();
			state = this.state;
			if (state != ServiceObjectState.Closed) {
				this.state = ServiceObjectState.Closing;
			}
		} finally {
			this.thisLock.unlock();
		}

		if (this.state == ServiceObjectState.Closing || this.state == ServiceObjectState.Closed) {
			return;
		} else if (this.state == ServiceObjectState.Created || this.state == ServiceObjectState.Opening
				|| this.state == ServiceObjectState.Faulted) {
			this.abort();
		} else if (this.state == ServiceObjectState.Opened) {
			boolean flag = true;
			try {
				TimeoutHelper helper = new TimeoutHelper(timeout);
				this.onClosing();

				this.onClose(helper.getRemainingTime());
				this.onClosed();

				flag = false;
				return;
			} finally {
				if (flag) {
					this.abort();
				}
			}
		}
	}

	protected abstract void onOpen(long timeout);

	protected abstract void onClose(long timeout);

	protected abstract void onAbort();

	public void abort() {
		try {
			this.thisLock.lock();
			if (this.state == ServiceObjectState.Closed) {
				return;
			}

			this.state = ServiceObjectState.Closed;
		} finally {
			this.thisLock.unlock();
		}

		boolean flag = true;
		try {
			this.onClosing();
			this.onAbort();
			this.onClosed();
			flag = false;
		} finally {
			if (flag) {
				// log error.
			}
		}
	}

	private void onOpening() {

		if (this.listeners.size() > 0) {
			for (ServiceObjectListener l : this.listeners) {
				l.onOpening(this);
			}
		}
	}

	private void onOpened() {

		try {

			this.thisLock.lock();

			if (this.state != ServiceObjectState.Opening) {
				return;
			}

			this.state = ServiceObjectState.Opened;

		} finally {
			this.thisLock.unlock();
		}
		if (this.listeners.size() > 0) {
			for (ServiceObjectListener l : this.listeners) {
				l.onOpened(this);
			}
		}
	}

	private void onFaulted() {

		if (this.listeners.size() > 0) {
			for (ServiceObjectListener l : this.listeners) {
				l.onFaulted(this);
			}
		}
	}

	private void onClosing() {
		if (this.listeners.size() > 0) {
			for (ServiceObjectListener l : this.listeners) {
				l.onClosing(this);
			}
		}
	}

	private void onClosed() {
		if (this.listeners.size() > 0) {
			for (ServiceObjectListener l : this.listeners) {
				l.onClosed(this);
			}
		}
	}

	public void registerListener(ServiceObjectListener listener) {
		this.listeners.add(listener);
	}

	public void removeListeners(ServiceObjectListener listener) {
		this.listeners.remove(listener);
	}

	public interface ServiceObjectListener {
		void onOpening(ServiceObject sender);

		void onOpened(ServiceObject sender);

		void onClosing(ServiceObject sender);

		void onClosed(ServiceObject sender);

		void onFaulted(ServiceObject sender);
	}

	public static class ServiceObjectListenerAdapter implements ServiceObjectListener {

		@Override
		public void onOpening(ServiceObject sender) {
		}

		@Override
		public void onOpened(ServiceObject sender) {
		}

		@Override
		public void onClosing(ServiceObject sender) {
		}

		@Override
		public void onClosed(ServiceObject sender) {
		}

		@Override
		public void onFaulted(ServiceObject sender) {
		}
	}
}
