package cs455.overlay.wireformats;

import java.io.*;

public class TrafficSummary extends MessageNodeEvent implements Event{

	//private final int type = 10;

	private int sendTracker;
	private int receiveTracker;
	private int relayTracker;
	private long sendSummation;
	private long receiveSummation;

	public TrafficSummary(String ip, int port, int sendTracker, int receiveTracker, int relayTracker, long sendSummation, long receiveSummation) {
		super(10, ip, port);
		this.sendTracker = sendTracker;
		this.receiveTracker = receiveTracker;
		this.relayTracker = relayTracker;
		this.sendSummation = sendSummation;
		this.receiveSummation = receiveSummation;
	}

	public TrafficSummary(DataInputStream din) throws IOException{
		super(din, 10);

		sendTracker = din.readInt();
		receiveTracker = din.readInt();
		relayTracker = din.readInt();
		sendSummation = din.readLong();
		receiveSummation = din.readLong();
	}

	public int getSendTracker() { return sendTracker; }
	public int getReceiveTracker() { return receiveTracker; }
	public int getRelayTracker() { return relayTracker; }

	public long getReceiveSummation() {
		return receiveSummation;
	}
	public long getSendSummation() {
		return sendSummation;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledData;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutStream));

		dout.writeInt(type);

		byte[] ipBytes = ip.getBytes();
		int ipLength = ipBytes.length;
		dout.writeInt(ipLength);
		dout.write(ipBytes);
		dout.writeInt(port);

		dout.writeInt(sendTracker);
		dout.writeInt(receiveTracker);
		dout.writeInt(relayTracker);
		dout.writeLong(sendSummation);
		dout.writeLong(receiveSummation);

		dout.flush();
		marshalledData = baOutStream.toByteArray();

		baOutStream.close();
		dout.close();
		return marshalledData;
	}
}
