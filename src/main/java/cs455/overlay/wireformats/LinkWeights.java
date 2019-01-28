package cs455.overlay.wireformats;

import cs455.overlay.util.OverlayEdge;

import java.io.IOException;
import java.util.LinkedList;

public class LinkWeights implements Event{
	private final int type = 5;
	private LinkedList<OverlayEdge> links;

	public LinkWeights(LinkedList<OverlayEdge> links) {
		this.links = links;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return new byte[0];
	}
}
