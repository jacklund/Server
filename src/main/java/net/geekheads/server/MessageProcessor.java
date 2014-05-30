package net.geekheads.server;

/**
 * Interface for classes designed to process generic device message data.
 * <p>
 * This is designed to be the class which contains the business logic for handling incoming data.
 * <p>
 * The {@link Configurator} class is passed into the {@link MessageProcessorFactory}, which can then pass it into
 * the instances of {@code MessageProcessor} it creates; this allows it access to the system configuration, as well as
 * any additional data you need to use to set up the processing (e.g., database connections, host/ports for network connections,
 * queues for off-loading asynchronous tasks to a separate worker thread, etc).
 * 
 * @author Jack Lund
 *
 */
public interface MessageProcessor<T> {
	/**
	 * Process a message. There are no assumptions made about what this means.
	 * 
	 * @param message the data being handled.
	 */
	void process(T message);
}
