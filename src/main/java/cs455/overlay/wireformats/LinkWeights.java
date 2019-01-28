package cs455.overlay.wireformats;

import cs455.overlay.util.OverlayEdge;

import java.io.*;
import java.util.LinkedList;

public class LinkWeights implements Event{
	private final int type = 5;
	private LinkedList<OverlayEdge> links;

	public LinkWeights(LinkedList<OverlayEdge> links) {
		this.links = links;
	}

	public LinkWeights(DataInputStream din) throws IOException {
		int numLinks = din.readInt();

		byte[] linkInfo;
		for(int i = 0; i < numLinks; i++) {
			int length = din.readInt();
			linkInfo = new byte[length];

			din.readFully(linkInfo);
			String[] split = new String(linkInfo).split(" ");
			links.add(new OverlayEdge());
		}
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
		dout.writeInt(links.size());

		byte[] linkBytes;
		String from;
		String to;
		for(OverlayEdge link : links) {
			from = link.getEndpointFrom().getIp() + ":" + link.getEndpointFrom().getPort();
			to = link.getEndpointTo().getIp() + ":" + link.getEndpointTo().getPort();
			linkBytes = (from + " " + to + " " + link.getWeight()).getBytes();
			dout.writeInt(linkBytes.length);
			dout.write(linkBytes);
		}

		dout.flush();
		marshalledData = baOutStream.toByteArray();

		baOutStream.close();
		dout.close();
		return marshalledData;
	}
}
