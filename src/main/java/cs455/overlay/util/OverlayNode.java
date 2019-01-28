package cs455.overlay.util;


public class OverlayNode {
	private String ip;
	private int port;
	private int maxConnections;

	private OverlayNode[] connections;
	private OverlayEdge[] edges;
	private int size = 0;

	/**
	 * OverlayNode for setting up overlay
	 * @param ip of this node
	 * @param port this node listens over
	 * @param maxConnections exact number of connections this node should have
	 */
	public OverlayNode(String ip, int port, int maxConnections) {
		this.ip = ip;
		this.port = port;
		this.connections = new OverlayNode[maxConnections];
		this.edges = new OverlayEdge[maxConnections];
		this.maxConnections = maxConnections;
	}

	public OverlayNode(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	/**
	 * add edge indicating which other node this one has a connection to
	 * @param other the node this node will be connected to
	 * @param send whether this node has to initiate the connection, if true it initiates, if false other initiates
	 */
	public void addEdge(OverlayNode other, boolean send, int weight) {
		if (size < edges.length){
			edges[size] = new OverlayEdge(this, other, send, weight);
			size++;
		}
	}

	public OverlayEdge getEdge(int index) {
		return edges[index];
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return this.port;
	}

	public OverlayEdge[] getEdges() {
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
}
