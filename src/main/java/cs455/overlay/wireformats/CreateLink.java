package cs455.overlay.wireformats;

import java.io.DataInputStream;
import java.io.IOException;

public class CreateLink extends MessageNodeEvent{
	public CreateLink(String ip, int port) {
		super(4, ip, port);
	}

	public CreateLink(DataInputStream din) throws IOException{
		super(din,4);
	}
}
