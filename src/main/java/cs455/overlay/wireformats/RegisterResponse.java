package cs455.overlay.wireformats;

import javax.xml.crypto.Data;
import java.io.*;

public class RegisterResponse implements Event{
	private final int type = 2;
	private byte status; //0 = success, 1 = failure
	private String info;

	public RegisterResponse(byte status, String info) {
		this.status = status;
		this.info = info;
	}

	public RegisterResponse(DataInputStream din) throws IOException{
		status = din.readByte();


		int identifierLength = din.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		din.readFully(identifierBytes);
		info = new String(identifierBytes);
	}

	@Override
	public int getType() {
		return type;
	}

	public void unMarshall(DataInputStream din) throws IOException{

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
		dout.writeInt(infoLength);
		dout.write(infoBytes);

		dout.flush();
		marshalledData = baOutStream.toByteArray();

		baOutStream.close();
		dout.close();
		return marshalledData;

	}

	public byte getStatus() {
		return this.status;
	}

	public String getInfo() {
		return this.info;
	}


}
