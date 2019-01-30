package cs455.overlay.wireformats;

import java.io.*;
import java.util.Random;

public class Message implements Event{
	private final int type = 6;
	private int payload;

	public Message() {
		this.payload = new Random().nextInt();
	}

	public Message(DataInputStream din) throws IOException{
		this.payload = din.readInt();
	}

	@Override
	public int getType() {
		return type;
	}

	public int getPayload() {
		return this.payload;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledData;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutStream));

		dout.writeInt(type);
		dout.writeInt(payload);

		dout.flush();
		marshalledData = baOutStream.toByteArray();

		baOutStream.close();
		dout.close();

		return marshalledData;
	}
}
