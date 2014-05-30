package net.geekheads.server;

import net.geekheads.server.ThreadedServer;

import net.geekheads.queue.Queue;
import net.geekheads.queue.QueueException;
import net.geekheads.queue.SerializerException;

/**
 * Queue Server class. Uses the {@link QueueServerConfigurator} class to get its configuration values,
 * including thread pool configuration, queue timeout, and the implementations of the {@link Queue}
 * and the {@link MessageProcessorFactory}. It then reads messages from the queue, and uses
 * worker threads in the thread pool to process the messages via the {@link MessageProcessor} instances
 * created by the {@link MessageProcessorFactory}.
 *
 * @author Jack Lund
 */
public class QueueServer<T> extends ThreadedServer<T> {
	private Queue<T> queue;
	private long queueTimeout;
	
	public Queue<T> getQueue() {
		return queue;
	}

	public void setQueue(Queue<T> queue) {
		this.queue = queue;
	}

	public long getQueueTimeout() {
		return queueTimeout;
	}

	public void setQueueTimeout(long queueTimeout) {
		this.queueTimeout = queueTimeout;
	}

	/**
	 * Main method. Simply loops, pulling messages off the queue and sending them
	 * to the worker threads
	 */
	@Override
	protected void run() {
    	T msg = null;
		try {
			while (isRunning()) {
				try {
					msg = queue.get(queueTimeout);
				} catch (SerializerException e) {
					e.printStackTrace();
				}
				process(msg);
			}
		} catch (QueueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
