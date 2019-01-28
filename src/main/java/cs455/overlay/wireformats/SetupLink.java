package cs455.overlay.wireformats;


import java.io.IOException;

public class SetupLink implements Event{
	private final int type = 4;

	@Override
	public int getType() {
		return type;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return new byte[0];
	}
}
