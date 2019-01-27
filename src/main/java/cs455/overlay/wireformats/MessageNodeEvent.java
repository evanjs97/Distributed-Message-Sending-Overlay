package cs455.overlay.wireformats;

import java.io.*;

public class MessageNodeEvent implements Event{
	protected int type;
	protected String ip;
	protected int port;

	public MessageNodeEvent(int type, String ip, int port) {
		this.type = type;
		this.ip = ip;
		this.port = port;
	}

	public MessageNodeEvent(DataInputStream din, int type) throws IOException{
		int identifierLength = din.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		din.readFully(identifierBytes);

		this.ip = new String(identifierBytes);
		this.port = din.readInt();
		this.type = type;
	}

	/**
	 * Marshall/encode the register message for transfer over TCP
	 * Order of retrieval is int=type, String=address, int=port
	 * @return a byte[] representation of this register object
	 */
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledData = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutStream));

		dout.writeInt(type);
		byte[] ipBytes = ip.getBytes();
		int ipLength = ipBytes.length;
		dout.writeInt(ipLength);
		dout.write(ipBytes);

		dout.writeInt(port);

		dout.flush();
		marshalledData = baOutStream.toByteArray();

		baOutStream.close();
		dout.close();
		return marshalledData;
	}

	@Override
	public int getType() { return this.type; }

	public String getIp() { return this.ip; }

	public int getPort() { return this.port; }

}
