package cs455.overlay.dijkstra;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.util.OverlayEdge;
import cs455.overlay.util.OverlayNode;

import java.util.*;

public class ShortestPath {
	private RoutingCache cache = null;
	Set<OverlayNode> nodes;
	private OverlayNode start;

	/**
	 * ShortestPath constructor, reconstructs overlay from edge list and finds shortest path to all other nodes
	 * @param edges the edge list to construct nodes list from
	 * @param start the node to start shortest path from
	 */
	public ShortestPath(LinkedList<OverlayEdge> edges, MessagingNode start) {
		this.nodes = new HashSet<>();

		for(OverlayEdge edge : edges) {
			OverlayNode node1 = edge.getEndpointFrom();
			OverlayNode node2 = edge.getEndpointTo();
			nodes.add(node1);
			nodes.add(node2);
			for(OverlayNode node : nodes) {
				if(node.equals(node1)) node1 =  node;
				if(node.equals(node2)) node2 = node;
				if(node.getIp().equals(start.getAddress()) && node.getPort() == start.getPort()) this.start = node;
			}
			node1.addEdge(new OverlayEdge(node1,node2,true,edge.getWeight()));
			node2.addEdge(new OverlayEdge(node2,node1,true,edge.getWeight()));


		}
		dijkstra();
		System.out.println("Link weights are received and processed. Ready to send messages.");
	}

	public LinkedList<OverlayNode> getRandomShortestPath() {
		return cache.getRandomPath();
	}

	/**
	 * dijkstra finds the shortest path from start to each other node in the graph
	 */
	private void dijkstra() {
		ArrayList<OverlayNode> set = new ArrayList<>();
		for(OverlayNode node : nodes) {
			node.makeDijkstra();
			if(node.equals(start)) {
				node.setDistance(0);
			}
			set.add(node);
		}

		while(!set.isEmpty()) {
			/**
			 * find min distance node from set then remove it
			 */
			OverlayNode node = set.get(0);
			for(OverlayNode nodeTest : set) {
				if(nodeTest.compareTo(node) < 0) node = nodeTest;
			}
			set.remove(node);

			/**
			 * look at each of min node's edges, check if dest node is not in set and distance is
			 * less than current distance of dest node, update its distance to new shorter one
			 */
			for(OverlayEdge edge : node.getEdges()) {
				OverlayNode edgeNode = edge.getEndpointTo();
//				if(edgeNode.equals(node)) edgeNode = edge.getEndpointFrom();
//				boolean isInSet = false;
//				for(OverlayNode nodeTest : set) {
//					if(edgeNode.equals(nodeTest)) isInSet = true;
//				}
//				set.contains(node)
				if(set.contains(edgeNode)) {
					int dist = node.getDistance() + edge.getWeight();
					if (dist < edgeNode.getDistance()) {
						edgeNode.setDistance(dist);
						edgeNode.setPrev(node);
					}
				}
			}
		}
		//nodes.remove(start);
		cache = new RoutingCache(nodes,start);
		cache.print();
	}


}
