package cs455.overlay.wireformats;

import cs455.overlay.util.OverlayEdge;
import cs455.overlay.util.OverlayNode;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class MessagingNodesList implements Event{
	private final int type = 3;
	private LinkedList<OverlayNode> nodes;
	private int numConnections;

	/**
	 * MessagingNodes List constructor takes in list of edges and filters
	 * out those nodes that should the calling node need not connect to
	 * @param edges the edge list to be filtered
	 */
	public MessagingNodesList(ArrayList<OverlayEdge> edges) {
		this.nodes = new LinkedList<>();
		for(OverlayEdge edge : edges) {
			if(edge.getSend()) this.nodes.add(edge.getEndpointTo());
		}
		numConnections = nodes.size();
	}

	public MessagingNodesList(DataInputStream din) throws IOException{
		numConnections = din.readInt();
		nodes = new LinkedList<>();
		for(int i = 0; i < numConnections; i++) {
			int connectionLength = din.readInt();
			byte[] connection = new byte[connectionLength];
			din.readFully(connection);
			String[] address = new String(connection).split(" ");
			nodes.add(new OverlayNode(address[0],Integer.parseInt(address[1])));
		}
	}

	/**
	 * @return the type of this message '4'
	 */
	@Override
	public int getType() {
		return this.type;
	}

	public LinkedList<OverlayNode> getNodes() {
		return nodes;
	}

	public int getNumConnections() {
		return numConnections;
	}

	/**
	 * @return the message type, number of connections to be made and address + port of the nodes
	 * to connect to in byte form
	 * @throws IOException
	 */
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledData;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutStream));

		dout.writeInt(type);
		dout.writeInt(numConnections);

		for(OverlayNode node : nodes) {
			byte[] ebytes = (node.getIp() + " " + node.getPort()).getBytes();
			dout.writeInt(ebytes.length);
			dout.write(ebytes);
		}
		dout.flush();

		marshalledData = baOutStream.toByteArray();

		baOutStream.close();
		dout.close();
		return marshalledData;
	}
}
