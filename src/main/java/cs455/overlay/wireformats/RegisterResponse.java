package cs455.overlay.wireformats;

import java.io.*;

public class RegisterResponse implements Event{
	private final int type = 2;
	private byte status; //0 = success, 1 = failure
	private String info;

	public RegisterResponse(byte status, String info) {
		this.status = status;
		this.info = info;
	}

	public RegisterResponse(DataInputStream din) {

	}

	@Override
	public int getType() {
		return type;
	}



	/**
	 * Marshall/encode the registration response message for transfer over TCP
	 * N
	 * @return a byte[] representation of this register response object
	 */
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledData = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutStream));

		dout.writeInt(type);
		dout.writeByte(status);

		byte[] infoBytes = info.getBytes();
		int infoLength = infoBytes.length;
		dout.write(infoLength);
		dout.write(infoBytes);

		dout.flush();
		marshalledData = baOutStream.toByteArray();

		baOutStream.close();
		dout.close();
		return marshalledData;

	}


}
