package cs455.overlay.wireformats;

public class Deregister implements Event{
	private final int type = 1;


	@Override
	public int getType() {
		return 0;
	}

	@Override
	public byte[] getBytes() {
		return new byte[0];
	}
}
