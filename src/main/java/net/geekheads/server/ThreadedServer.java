package net.geekheads.server;

import java.util.concurrent.ExecutorService;


/**
 * Threaded server base class. Requires a {@link MessageProcessorFactory}.
 * Creates a single instance of {@link MessageProcessor} per thread so that the MessageProcessor doesn't have to be thread safe. 
 * @author Jack Lund
 *
 */
public abstract class ThreadedServer<T> {
	protected ExecutorService pool;
	protected boolean running = false;
	protected static MessageProcessorHolder holder;

	// Class which keeps a single MessageProcessor per thread via thread local storage
	protected static class MessageProcessorHolder {
		private ThreadLocal<MessageProcessor<?>> processor;
		private MessageProcessorFactory<?> factory;

		public MessageProcessorHolder() {
			processor = new ThreadLocal<MessageProcessor<?>>() {
					@Override
					protected MessageProcessor<?> initialValue() {
						return factory.create();
					}
				};
		}
		
		public MessageProcessor<?> get() {
			return processor.get();
		}
		
		@SuppressWarnings("unchecked")
		public <T> MessageProcessorFactory<T> getMessageProcessorFactory() {
			return (MessageProcessorFactory<T>) factory;
		}
		
		public void setMessageProcessorFactory(MessageProcessorFactory<?> f) {
			factory = f;
		}
	}

	// Worker class. Defers work to the MessageProcessor
	protected static class Worker<T> implements Runnable {
		private T message;
		
		public Worker(T msg) {
			this.message = msg;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			((MessageProcessor<T>) holder.get()).process(message);
		}
	}
	
	public ThreadedServer() {
		holder = new MessageProcessorHolder();
	}

	public ExecutorService getPool() {
		return pool;
	}

	public void setPool(ExecutorService pool) {
		this.pool = pool;
	}

	public MessageProcessorFactory<T> getMessageProcessorFactory() {
		return holder.getMessageProcessorFactory();
	}

	public void setMessageProcessorFactory(
			MessageProcessorFactory<T> messageProcessorFactory) {
		holder.setMessageProcessorFactory(messageProcessorFactory);
	}

	/**
	 * Derived classes call this to have the thread pool process the next message.
	 * 
	 * @param message the message
	 */
	protected void process(T message) {
		if (message != null) pool.execute(new Worker<T>(message));
	}
	
	/**
	 * Are we still running?
	 * 
	 * @return true if we are
	 */
	protected boolean isRunning() {
		return running;
	}

	/**
	 * Start up the server.
	 */
	public void start() {
		running = true;
		run();
	}

	/**
	 * Business method for derived classes. They should do whatever setup in here, and then loop,
	 * getting messages (however they do) and passing them to {@code process()}, until
	 * {@code isRunning()} is no longer true.
	 */
	protected abstract void run();
}