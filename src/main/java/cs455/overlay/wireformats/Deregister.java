package cs455.overlay.wireformats;

import java.io.DataInputStream;
import java.io.IOException;

public class Deregister extends MessageNodeEvent implements Event {

	public Deregister(String ip, int port) {
		super(1, ip, port);
	}

	public Deregister(DataInputStream din) throws IOException {
		super(din);
	}

}
