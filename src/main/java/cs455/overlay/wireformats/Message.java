package cs455.overlay.wireformats;

import cs455.overlay.util.OverlayNode;

import java.io.*;
import java.util.LinkedList;
import java.util.Random;

public class Message implements Event{
	private final int type = 6;
	private int payload;
	private LinkedList<OverlayNode> routingPlan;

	public Message(LinkedList<OverlayNode> path) {
		this.routingPlan = path;
		this.payload = new Random().nextInt();
	}

	public Message(DataInputStream din) throws IOException{
		routingPlan = new LinkedList<>();

		byte[] path;
		for(int i = 0; i < din.readInt(); i++) {
			int length = din.readInt();
			path = new byte[length];

			din.readFully(path);
			String[] split = new String(path).split(":");
			routingPlan.addFirst(new OverlayNode(split[0], Integer.parseInt(split[1])));
		}
		this.payload = din.readInt();
	}

	@Override
	public int getType() {
		return type;
	}

	public int getPayload() {
		return this.payload;
	}

	public boolean relay() {
		return !routingPlan.isEmpty();
	}

	public OverlayNode nextDest() {
		return routingPlan.pollFirst();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledData;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutStream));

		dout.writeInt(type);

		dout.writeInt(routingPlan.size());
		byte[] nodeBytes;

		for(OverlayNode node : routingPlan) {
			nodeBytes = (node.getIp() + ":" + node.getPort()).getBytes();
			dout.writeInt(nodeBytes.length);
			dout.write(nodeBytes);
		}
		dout.writeInt(payload);

		dout.flush();
		marshalledData = baOutStream.toByteArray();

		baOutStream.close();
		dout.close();
		return marshalledData;
	}
}
