package cs455.overlay.util;

import java.util.LinkedList;

public class OverlayNode {
	private String ip;
	private int port;
	private int maxConnections;

	private LinkedList<OverlayNode> connections;
	private LinkedList<OverlayEdge> edges;

	public OverlayNode(String ip, int port, int maxConnections) {
		this.ip = ip;
		this.port = port;
		this.connections = new LinkedList<>();
		this.maxConnections = maxConnections;
	}

	public void addConnection(OverlayNode other) {
		connections.add(other);
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return this.port;
	}

	public void clear() {
		connections.clear();
	}

	public int getCount() {
		return connections.size();
	}

	public boolean isFull() {
		if(getCount() >= maxConnections) return true;
		else return false;
	}

	public String toString() {
		String node = this.ip + ", " + this.port + ": ";
		for(OverlayNode onode: connections) {
			node += onode.getIp() + " ";
		}
		return node;
	}
}
