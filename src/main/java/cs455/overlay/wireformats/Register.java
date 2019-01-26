package cs455.overlay.wireformats;

/**
 * Note that type of register message is 0
 */
public class Register implements Event{

	private final int type = 0;
	private String ip;
	private int port;

	public Register(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public int getType() {
		return type;
	}

	/**
	 * Marshall/encode the register message for transfer over TCP
	 * N
	 * @return a byte[] representation of this register object
	 */
	@Override
	public byte[] getBytes() {
		return null;
	}
}
