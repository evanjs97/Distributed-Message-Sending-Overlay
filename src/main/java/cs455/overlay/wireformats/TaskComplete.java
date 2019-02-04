package cs455.overlay.wireformats;

import java.io.DataInputStream;
import java.io.IOException;

public class TaskComplete extends MessageNodeEvent{

	public TaskComplete(String ip, int port) {
		super(8, ip, port);
	}

	public TaskComplete(DataInputStream din) throws IOException {
		super(din, 8);
	}
}
