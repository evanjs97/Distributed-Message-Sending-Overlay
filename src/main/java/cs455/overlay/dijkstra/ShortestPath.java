package cs455.overlay.dijkstra;

import cs455.overlay.util.OverlayEdge;
import cs455.overlay.util.OverlayNode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;

public class ShortestPath {
	private RoutingCache cache;
	Set<OverlayNode> nodes;

	public ShortestPath(LinkedList<OverlayEdge> edges) {
		this.nodes = new HashSet<>();
		for(OverlayEdge edge : edges) {
			OverlayNode node1 = edge.getEndpointFrom();
			OverlayNode node2 = edge.getEndpointTo();
			nodes.add(node1);
			nodes.add(node2);
			for(OverlayNode node : nodes) {
				if(node.getIp().equals(node1.getIp()) || node.getIp().equals(node2.getIp())) node.addEdge(edge);
			}
		}

		for(OverlayNode node : nodes) {
			System.out.println(node);
		}
		System.out.println("Link weights are received and processed. Ready to send messages.");
	}

	public void dijkstra() {

	}
}
