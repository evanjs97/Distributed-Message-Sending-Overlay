package cs455.overlay.wireformats;

import java.io.*;

/**
 * Note that type of register message is 0
 */
public class Register extends MessageNodeEvent implements Event{

	/**
	 *
	 * @param ip
	 * @param port
	 * @param type type 0 = register, type 1 = deregister
	 */
	public Register(String ip, int port, int type) {
		super(Math.min(1, type), ip, port);
	}

	public Register(DataInputStream din, int type) throws IOException{
		super(din, type);

	}
}
