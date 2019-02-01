package cs455.overlay.dijkstra;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.util.OverlayEdge;
import cs455.overlay.util.OverlayNode;

import java.util.*;

public class ShortestPath {
	private RoutingCache cache = null;
	Set<OverlayNode> nodes;
	private OverlayNode start;

	public ShortestPath(LinkedList<OverlayEdge> edges, MessagingNode start) {
		this.nodes = new HashSet<>();

		for(OverlayEdge edge : edges) {
			OverlayNode node1 = edge.getEndpointFrom();
			OverlayNode node2 = edge.getEndpointTo();
			nodes.add(node1);
			nodes.add(node2);
			for(OverlayNode node : nodes) {
				if(node.getIp().equals(start.getAddress()) && node.getPort() == start.getPort()) this.start = node;
				if(node.getIp().equals(node1.getIp())) node.addEdge(edge);
				else if(node.getIp().equals(node2.getIp())) {
					node.addEdge(new OverlayEdge(node2,node1,true,edge.getWeight()));
				}
			}
		}
		dijkstra();
		for(OverlayNode node : nodes) {
			System.out.println(node);
		}
		System.out.println("Link weights are received and processed. Ready to send messages.");
	}

	public LinkedList<OverlayNode> getShortestPath(OverlayNode dest) {
		return cache.getPath(dest);
	}

	private void dijkstra() {
		SortedSet<OverlayNode> set = new TreeSet<>();
		for(OverlayNode node : nodes) {
			node.makeDijkstra();
			if(node.getIp().equals(start.getIp()) && node.getPort() == start.getPort()) node.setDistance(0);
			set.add(node);
		}

		while(!set.isEmpty()) {
			OverlayNode node = set.first();
			set.remove(node);

			for(OverlayEdge edge : node.getEdges()) {
				int dist = node.getDistance() + edge.getWeight();

				if(dist < edge.getEndpointTo().getDistance()) {
					edge.getEndpointTo().setDistance(dist);
					edge.getEndpointTo().setPrev(node);
				}
			}
		}
		cache = new RoutingCache(nodes,start);
	}

	public OverlayNode randomSink() {
		return cache.getRandom();
	}

}
