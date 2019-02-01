package cs455.overlay.dijkstra;

import cs455.overlay.util.OverlayEdge;
import cs455.overlay.util.OverlayNode;

import java.util.*;

public class RoutingCache {

	private HashMap<String, LinkedList<OverlayNode>> cache;

	public RoutingCache(Set<OverlayNode> nodes, OverlayNode start) {
		cache = new HashMap<>();
		for(OverlayNode node : nodes) {
			if(node != start) {
				LinkedList<OverlayNode> path = new LinkedList<>();
				OverlayNode prev = node.getPrev();
				while(prev != null) {
					path.addFirst(prev);
					prev = prev.getPrev();
				}
				String key = node.getIp() + ":" + node.getPort();
				cache.put(key,path);
			}
		}
	}

	public LinkedList<OverlayNode> getPath(OverlayNode dest) {
		String key = dest.getIp() + ":" + dest.getPort();
		return cache.get(key);
	}

	public OverlayNode getRandom() {
		Random rand = new Random();
		Object[] entries = cache.entrySet().toArray();
		return (OverlayNode) entries[rand.nextInt(entries.length)];

	}
}
