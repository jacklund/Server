package net.geekheads.server;

import static org.junit.Assert.*;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import net.geekheads.server.MessageProcessor;
import net.geekheads.server.MessageProcessorFactory;
import net.geekheads.server.ThreadedServer;

import org.junit.Test;

/**
 * Unit test, to make sure {@link ThreadedServer} only uses one {@link MessageProcessor} per thread.
 * 
 * @author Jack Lund
 *
 */
public class ThreadedServerTest {
	private static class MyServer extends ThreadedServer<String> {
		@Override
		protected void run() {
			for (int i = 0; i < 1000; ++i) {
				process("Foo");
			}
		}
	}

	private static class MyMessageProcessor implements MessageProcessor<String> {
		private static AtomicLong count = new AtomicLong();
		private long threadId = -1;
		@SuppressWarnings("unused")
		private long processorId;

		public MyMessageProcessor() {
			processorId = count.addAndGet(1);
		}
		
		public void process(String message) {
			long id = Thread.currentThread().getId();
			//System.out.println("Processor " + processorId + "(thread " + id + ") processing message");
			if (threadId != -1 && id != threadId) {
				// If this gets called from some thread other than the original one,
				// fail the test
				fail("Processor used in multiple threads");
			}
			threadId = id;
		}
	}
	
	private static class MyMessageProcessorFactory implements MessageProcessorFactory<String> {
		public MessageProcessor<String> create() {
			return new MyMessageProcessor();
		}
	}

	@Test
	public void test() {
		MyServer server = new MyServer();
		server.setMessageProcessorFactory(new MyMessageProcessorFactory());
		server.setPool(Executors.newCachedThreadPool());
		server.start();
	}

}
