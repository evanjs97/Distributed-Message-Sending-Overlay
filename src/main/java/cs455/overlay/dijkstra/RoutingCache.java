package cs455.overlay.dijkstra;

import cs455.overlay.util.OverlayEdge;
import cs455.overlay.util.OverlayNode;

import java.util.*;

public class RoutingCache {

	private HashMap<String, LinkedList<OverlayNode>> cache;

	public RoutingCache(Set<OverlayNode> nodes, OverlayNode start) {
		cache = new HashMap<>();
		for(OverlayNode node : nodes) {
			if(!node.equals(start)) {
				LinkedList<OverlayNode> path = new LinkedList<>();
				OverlayNode prev = node;
				while(!prev.equals(start)) {
					path.addFirst(prev);
					prev = prev.getPrev();
				}
				String key = node.getIp() + ":" + node.getPort();
				cache.put(key,path);
			}
		}
	}

	public void print() {
		Iterator cacheIter = cache.entrySet().iterator();
		System.out.println("\n\n");
		while(cacheIter.hasNext()) {
			Map.Entry tuple = (Map.Entry) cacheIter.next();
			System.out.println(tuple.getKey() + ":  " + tuple.getValue());
		}System.out.println("\n\n");

	}

	public LinkedList<OverlayNode> getRandomPath() {
		Random rand = new Random();
		Object[] entries = cache.entrySet().toArray();
		Map.Entry tuple = (Map.Entry) entries[rand.nextInt(entries.length)];
		LinkedList<OverlayNode> copy = new LinkedList<>((LinkedList<OverlayNode>) tuple.getValue());
		return copy;
		//return cache.get(key);
	}

//	public String getRandom() {
//		Random rand = new Random();
//		Object[] entries = cache.entrySet().toArray();
//		Map.Entry tuple = (Map.Entry) entries[rand.nextInt(entries.length)];
//		return tuple.getKey();
//	}
}
