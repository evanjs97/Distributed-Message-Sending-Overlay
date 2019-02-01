package cs455.overlay.util;


import java.util.ArrayList;

public class OverlayNode implements Comparable<OverlayNode>{
	private String ip;
	private int port;
	private int maxConnections;

	private ArrayList<OverlayNode> connections;
	private ArrayList<OverlayEdge> edges;
	private int size = 0;

	private OverlayNode dijPrev;
	private int distance;

	/**
	 * OverlayNode for setting up overlay
	 * @param ip of this node
	 * @param port this node listens over
	 * @param maxConnections exact number of connections this node should have
	 */
	public OverlayNode(String ip, int port, int maxConnections) {
		this.ip = ip;
		this.port = port;
		this.connections = new ArrayList<>();
		this.edges = new ArrayList<>();
		this.maxConnections = maxConnections;
	}

	public OverlayNode(String ip, int port) {
		this.connections = new ArrayList<>();
		this.edges = new ArrayList<>();
		this.ip = ip;
		this.port = port;

	}

	public void makeDijkstra() {
		this.dijPrev = null;
		this.distance = 2147483647;
	}

	public int getDistance() { return this.distance; }
	public OverlayNode getPrev() { return this.dijPrev; }
	public void setDistance(int distance) { this.distance = distance; }
	public void setPrev(OverlayNode prev) { this.dijPrev = prev; }

	/**
	 * add edge indicating which other node this one has a connection to
	 * @param other the node this node will be connected to
	 * @param send whether this node has to initiate the connection, if true it initiates, if false other initiates
	 */
	public void addEdge(OverlayNode other, boolean send, int weight) {
		edges.add(new OverlayEdge(this, other, send, weight));
		size++;
	}

	public void addEdge(OverlayEdge edge) {
		edges.add(edge);
	}

	public OverlayEdge getEdge(int index) {
		return edges.get(index);
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return this.port;
	}

	public ArrayList<OverlayEdge> getEdges() {
		return edges;
	}

	public int getCount() {
		return size;
	}

	public boolean isFull() {
		if(getCount() >= maxConnections) return true;
		else return false;
	}

	/**
	 * @return string representation of this node, used for testing
	 */
	public String toString() {
		String node = this.ip + ", " + this.port + ": ";
		for(OverlayEdge edge: edges) {
			node += edge.getEndpointTo().getIp() + " ";
		}
		return node;
	}

	@Override
	public int compareTo(OverlayNode o) {
		if(this.distance < o.distance) return -1;
		else if(this.distance > o.distance) return 1;
		else return 0;
	}

	@Override
	public int hashCode() {
		return (this.getIp() + ":" + this.getPort()).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof OverlayNode)) return false;
		else {
			OverlayNode node = (OverlayNode) o;
			String nodeAddress = node.getIp() + ":" + node.getPort();;
			String thisAddress = this.ip + ":" + this.port;
			return nodeAddress.equals(thisAddress);
		}
	}
}
