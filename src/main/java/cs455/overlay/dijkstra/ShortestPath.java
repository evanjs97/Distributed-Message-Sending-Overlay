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
				if(node.equals(node1)) node.addEdge(edge);
				else if(node.equals(node2)) {
					node.addEdge(new OverlayEdge(node2,node1,true,edge.getWeight()));
				}
			}
		}
		dijkstra();
		System.out.println("Start: " + this.start);
		System.out.println("Printing nodes in set:");
		for(OverlayNode node : nodes) {
			System.out.println(node);
		}
		System.out.println("Link weights are received and processed. Ready to send messages.");
	}

	public LinkedList<OverlayNode> getRandomShortestPath() {
		return cache.getRandomPath();
	}

	private void dijkstra() {
		SortedSet<OverlayNode> set = new HashSet<>();
		for(OverlayNode node : nodes) {
			node.makeDijkstra();
			if(node.equals(start)) node.setDistance(0);
			set.add(node);
		}

		while(!set.isEmpty()) {
			OverlayNode node = null;
			for(OverlayNode nodeTest : set) {
				if(node <)
			}
			OverlayNode node = set.first();
			set.remove(node);

			for(OverlayEdge edge : node.getEdges()) {
				OverlayNode edgeNode = edge.getEndpointTo();
				if(set.contains(edgeNode)) {
					int dist = node.getDistance() + edge.getWeight();

					if (dist < edgeNode.getDistance()) {
						edgeNode.setDistance(dist);
						edgeNode.setPrev(node);
					}
				}
			}
		}
		cache = new RoutingCache(nodes,start);
		cache.print();
	}

//	public OverlayNode randomSink() {
//		return cache.getRandom();
//	}

}
