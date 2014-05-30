package net.geekheads.server;

/**
 * Factory interface for creating instances of {@link MessageProcessor}.
 * 
 * @author Jack Lund
 *
 */
public interface MessageProcessorFactory<T> {
	/**
	 * Factory method.
	 * 
	 * @return the message processor
	 */
	public MessageProcessor<T> create();
}
