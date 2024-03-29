package cs455.overlay.util;

public class OverlayEdge {
	private OverlayNode endpointFrom;
	private OverlayNode endpointTo;
	private int weight;
	private boolean send;

	/**
	 * overlay edge to be used in overlay creation by nodes
	 * @param from the node that holds this edge
	 * @param to the node that the edge holder connects to
	 * @param send if true 'from' node initiates connection, if false 'to' node will initiate
	 */
	public OverlayEdge(OverlayNode from, OverlayNode to, boolean send, int weight) {
		this.endpointFrom = from;
		this.endpointTo = to;
		this.send = send;
		this.weight = weight;
	}

	public OverlayNode getEndpointFrom() {
		return endpointFrom;
	}

	public OverlayNode getEndpointTo() {
		return endpointTo;
	}

	public boolean getSend() {
		return this.send;
	}

	public int getWeight() {
		return weight;
	}
}
