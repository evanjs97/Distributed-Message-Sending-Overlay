package cs455.overlay.util;

public class OverlayEdge {
	private OverlayNode endpointA;
	private OverlayNode endpointB;
	private int weight;

	public OverlayEdge(OverlayNode A, OverlayNode B) {
		this.endpointA = A;
		this.endpointB = B;
		this.weight = (int) (10 * Math.random());
	}
}
