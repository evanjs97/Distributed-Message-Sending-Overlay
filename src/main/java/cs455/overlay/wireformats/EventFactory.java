package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Singleton Instance Design Pattern
 * Only 1 EventFactory can be created in any JVM instance
 */
public class EventFactory {
	private static EventFactory eventFactory;
	private Queue<Event> eventQueue = new LinkedList<>();
	/**
	 * private constructor can only be called from this class
	 */
	private EventFactory(){}

	/**
	 * creates instance of EventFactory on class creation
	 * @return
	 */
	static {
		eventFactory = new EventFactory();
	}

	public boolean hasEvent() {
		return eventQueue.isEmpty();
	}

	public Event nextEvent() {
		return eventQueue.poll();
	}


	public static EventFactory getInstance() {
		return eventFactory;
	}

	/**
	 * Creates an event from any valid input byte array
	 * @param marshalledBytes the byte array received over the network
	 * @return an instance of an Event which is of the class type specified by the type present in marshalled bytes
	 * @throws IOException if there are issues with the input streams
	 */
	public Event getEvent(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream =
				new ByteArrayInputStream(marshalledBytes);
		DataInputStream din =
				new DataInputStream(new BufferedInputStream(baInputStream));
		int type = din.readInt();
		Event e = null;
		switch (type) {
			case 0:
				e = new Register(din, type);
				break;
			case 1:
				e = new Register(din, type);
				break;
			case 2:
				e = new RegisterResponse(din);
				break;
			case 3:
				e = new MessagingNodesList(din);
				break;
			case 4:
				e = new Register(din, type);
				break;
		}
		baInputStream.close();
		din.close();
		return e;
	}

}
