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
	 * @param type type 0 = register, type 1 = deregister, type 4 = message node link connection
	 */
	public Register(String ip, int port, int type) {
		super(type, ip, port);
	}

	public Register(DataInputStream din, int type) throws IOException{
		super(din, type);

	}
}
