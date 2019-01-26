package cs455.overlay.wireformats;

import java.io.*;

/**
 * Note that type of register message is 0
 */
public class Register extends MessageNodeEvent implements Event{

	public Register(String ip, int port) {
		super(0, ip, port);
	}

	public Register(DataInputStream din) throws IOException{
		super(din);
	}
}
