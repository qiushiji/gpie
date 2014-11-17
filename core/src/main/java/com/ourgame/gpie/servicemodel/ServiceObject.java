package com.ourgame.gpie.servicemodel;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ourgame.gpie.date.TimeoutHelper;

public abstract class ServiceObject {

	private final static Logger logger = LoggerFactory.getLogger(ServiceObject.class);

	private Lock lock = new ReentrantLock();

	protected Lock getThisLock() {
		return this.lock;
	}

	public ServiceObjectState state;

	public ServiceObjectState getState() {
		return this.state;
	}

	public abstract long getDefaultOpenTimeout();

	public abstract long getDefaultCloseTimeout();

	private Set<ServiceObjectListener> listeners = new CopyOnWriteArraySet<ServiceObjectListener>();
	private boolean onOpeningCalled;
	private boolean onOpenedCalled;

	private ExceptionQueue exceptionQueue;

	private boolean aborted;
	private boolean closeCalled;
	private boolean onClosingCalled;
	private boolean onClosedCalled;

	private boolean raisedClosing;
	private boolean raisedClosed;
	private boolean raisedFaulted;

	public void open() {
		this.open(this.getDefaultOpenTimeout());
	}

	public void open(long timeout) {

		if (timeout < 0) {
			throw new IllegalArgumentException("timeout");
		}

		try {
			this.lock.lock();
			this.state = ServiceObjectState.Opening;
		} finally {
			this.lock.unlock();
		}

		boolean flag = true;

		try {
			TimeoutHelper helper = new TimeoutHelper(timeout);
			this.onOpening();
			if (!this.onOpeningCalled) {
				throw new RuntimeException("Method onOpening not called!");
			}

			this.onOpen(helper.getRemainingTime());

			this.onOpened();
			if (!this.onOpenedCalled) {
				throw new RuntimeException("Method onOpened not called!");
			}
			flag = false;
		} finally {
			if (flag) {
				if (logger.isWarnEnabled()) {
					logger.warn("ServiceObject open failed!");
				}
				this.fault();
			}
		}
	}

	protected void fault() {
		try {
			this.lock.lock();

			if (this.state == ServiceObjectState.Closed || this.state == ServiceObjectState.Closing) {
				return;
			}

			if (this.state == ServiceObjectState.Faulted) {
				return;
			}

			this.state = ServiceObjectState.Faulted;

		} finally {
			this.lock.unlock();
		}

		this.onFaulted();
	}

	void fault(Throwable throwable) {
		try {
			this.lock.lock();

			if (this.exceptionQueue == null) {
				this.exceptionQueue = new ExceptionQueue(this.lock);
			}

		} finally {
			this.lock.unlock();
		}

		if (throwable != null && logger.isInfoEnabled()) {
			logger.info("ServiceObject fault!", throwable);
		}

		this.exceptionQueue.addException(throwable);
		this.fault();
	}

	public void close() {
		this.close(this.getDefaultCloseTimeout());
	}

	public void close(long timeout) {

		if (timeout < 0) {
			throw new IllegalArgumentException("timeout");
		}

		ServiceObjectState state;
		try {
			this.lock.lock();
			state = this.state;
			if (state != ServiceObjectState.Closed) {
				this.state = ServiceObjectState.Closing;
			}
			this.closeCalled = true;
		} finally {
			this.lock.unlock();
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
				if (!this.onClosingCalled) {
					throw new RuntimeException("Method onClosing not called!");
				}

				this.onClose(helper.getRemainingTime());

				this.onClosed();
				if (!this.onClosedCalled) {
					throw new RuntimeException("Method onClosed not called!");
				}

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
			this.lock.lock();
			if (this.aborted || this.state == ServiceObjectState.Closed) {
				return;
			}
			this.aborted = true;
			this.state = ServiceObjectState.Closing;
		} finally {
			this.lock.unlock();
		}

		if (logger.isInfoEnabled()) {
			logger.info("Service Object aborted!");
		}

		boolean flag = true;
		try {
			this.onClosing();
			if (!this.onClosingCalled) {
				throw new RuntimeException("Method onClosing not called!");
			}

			this.onAbort();

			this.onClosed();
			if (!this.onClosedCalled) {
				throw new RuntimeException("Method onClosed not called!");
			}

			flag = false;
		} finally {
			if (flag && logger.isWarnEnabled()) {
				logger.warn("ServiceObject abort failed");
			}
		}
	}

	private void onOpening() {
		this.onOpeningCalled = true;
		if (logger.isTraceEnabled()) {
			logger.trace("ServiceObject opening!");
		}

		if (this.listeners.size() > 0) {
			try {
				for (ServiceObjectListener l : this.listeners) {
					l.onOpening(this);
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private void onOpened() {

		try {

			this.lock.lock();

			if (this.aborted || this.state != ServiceObjectState.Opening) {
				return;
			}

			this.state = ServiceObjectState.Opened;

		} finally {
			this.lock.unlock();
		}

		if (logger.isTraceEnabled()) {
			logger.trace("ServiceObject opened!");
		}

		if (this.listeners.size() > 0) {
			try {
				for (ServiceObjectListener l : this.listeners) {
					l.onOpened(this);
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private void onFaulted() {

		try {
			this.lock.lock();
			if (this.raisedFaulted) {
				return;
			}

			this.raisedFaulted = true;

		} finally {
			this.lock.unlock();
		}

		if (this.listeners.size() > 0) {
			try {
				for (ServiceObjectListener l : this.listeners) {
					l.onFaulted(this);
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private void onClosing() {
		this.onClosingCalled = true;
		try {
			this.lock.lock();
			if (this.raisedClosing) {
				return;
			}
			this.raisedClosing = true;
		} finally {
			this.lock.unlock();
		}

		if (logger.isTraceEnabled()) {
			logger.trace("ServiceObject closing!");
		}

		if (this.listeners.size() > 0) {
			try {
				for (ServiceObjectListener l : this.listeners) {
					l.onClosing(this);
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private void onClosed() {

		this.onClosedCalled = true;
		try {
			this.lock.lock();
			if (this.raisedClosed) {
				return;
			}
			this.raisedClosed = true;
			this.state = ServiceObjectState.Closed;

		} finally {
			this.lock.lock();
		}

		if (logger.isTraceEnabled()) {
			logger.trace("ServiceObject closed!");
		}

		if (this.listeners.size() > 0) {
			try {
				for (ServiceObjectListener l : this.listeners) {
					l.onClosed(this);
				}
			} catch (Exception e) {
				throw e;
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

	class ExceptionQueue {
		private Queue<Throwable> exceptions = new LinkedList<Throwable>();
		private Lock thisLock;

		ExceptionQueue(Lock thisLock) {
			this.thisLock = thisLock;
		}

		public void addException(Throwable exception) {
			try {
				this.thisLock.lock();
				exceptions.add(exception);
			} finally {
				this.thisLock.unlock();
			}
		}

		public Throwable getException() {
			try {
				this.thisLock.lock();
				if (exceptions.size() > 0) {
					return exceptions.poll();
				}

			} finally {
				this.thisLock.unlock();
			}

			return null;
		}
	}
}
