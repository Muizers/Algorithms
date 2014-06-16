package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.OpPair;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.OpNode;
import tue.algorithms.utility.OpSegment;
import tue.algorithms.utility.Segment;

/**
 * Under development. Testing ground here.
 * @author Martijn
 */
public class SingleImplodingTryingFaster implements SingleImplementation {
	
	private static Comparator<OpPair<OpNode, Float>> nodeLikelinessComparator = new Comparator<OpPair<OpNode, Float>>() {
		
		@Override
		public int compare(OpPair<OpNode, Float> arg0,
				OpPair<OpNode, Float> arg1) {
			float likelihood0 = arg0.second;
			float likelihood1 = arg1.second;
			if (likelihood0 < likelihood1) {
				return 1;
			} else if (likelihood0 > likelihood1) {
				return -1;
			}
			return 0;
		}
		
	};
	
	private boolean tryOpenEnabled = true;
	
	private HashSet<OpSegment> foundSegments;
	
	private CurveType type = CurveType.CLOSED;
	private enum CurveType {
		CLOSED,
		OPEN
	}
	
	@Override
	public Segment[] getOutput(Node[] input) {
		return getOutput(input, GrahamConvexHull.getConvexHull(input));
	}
	
	private Segment[] getOutput(Node[] jnput, HashSet<Segment> convexHullA) {
		int ll = jnput.length;
		OpNode[] input = new OpNode[ll];
		for (int i = 0; i < ll; i++) {
			Node n = jnput[i];
			input[i] = new OpNode(n.id, n.x, n.y);
		}
		foundSegments = new HashSet<OpSegment>();
		for (Segment s : convexHullA) {
			foundSegments.add(s.toOpSegment());
		}
		HashSet<OpNode> nodesToDo = new HashSet<OpNode>();
		for (OpNode n : input) {
			nodesToDo.add(n);
		}
		for (OpSegment segment : foundSegments) {
			nodesToDo.remove(segment.node1);
			nodesToDo.remove(segment.node2);
		}
		List<OpSegment> likelinessesList1 = new ArrayList<OpSegment>(ll);
		List<OpNode> likelinessesList2 = new ArrayList<OpNode>(ll);
		List<Float> likelinessesList3 = new ArrayList<Float>(ll);
		for (OpSegment segment : foundSegments) {
			OpPair<OpNode, Float> nodeLikelinesses = buildNodeLikelinesses(segment, nodesToDo);
			likelinessesList1.add(segment);
			likelinessesList2.add(nodeLikelinesses.first);
			likelinessesList3.add(nodeLikelinesses.second);
		}
		int si = likelinessesList1.size();
		long largestMemoryUsed = 0;
		while (nodesToDo.size() > 0) {
			{
				Runtime runtime = Runtime.getRuntime();
				long memoryUsed = (runtime.totalMemory() - runtime.freeMemory());
				if (memoryUsed > largestMemoryUsed) {
					largestMemoryUsed = memoryUsed;
				}
			}
			OpSegment segmentWithSmallestLikeliness = null;
			OpNode nodeWithSmallestLikeliness = null;
			float smallestLikeliness = Float.MAX_VALUE;
			int smallestI = -1;
			for (int i = 0; i < si; i++) {
				float likeliness = likelinessesList3.get(i);
				if (likeliness < smallestLikeliness) {
					smallestLikeliness = likeliness;
					nodeWithSmallestLikeliness = likelinessesList2.get(i);
					smallestI = i;
				}
			}
			segmentWithSmallestLikeliness = likelinessesList1.get(smallestI);
			OpSegment newSegment1 = new OpSegment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.node1);
			OpSegment newSegment2 = new OpSegment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.node2);
			foundSegments.add(newSegment1);
			foundSegments.add(newSegment2);
			foundSegments.remove(segmentWithSmallestLikeliness);
			nodesToDo.remove(nodeWithSmallestLikeliness);
			likelinessesList1.remove(smallestI);
			likelinessesList2.remove(smallestI);
			likelinessesList3.remove(smallestI);
			int sip = si-1;
			if (nodesToDo.size() > 0) {
				for (int i = 0; i < sip; i++) {
					OpNode n = likelinessesList2.get(i);
					if (n.id == nodeWithSmallestLikeliness.id) {
						OpPair<OpNode, Float> nodeLikelinesses = buildNodeLikelinesses(likelinessesList1.get(i), nodesToDo);
						likelinessesList2.set(i, nodeLikelinesses.first);
						likelinessesList3.set(i, nodeLikelinesses.second);
					}
				}
				{
					likelinessesList1.add(newSegment1);
					OpPair<OpNode, Float> nodeLikelinesses = buildNodeLikelinesses(newSegment1, nodesToDo);
					likelinessesList2.add(nodeLikelinesses.first);
					likelinessesList3.add(nodeLikelinesses.second);
				}
				{
					likelinessesList1.add(newSegment2);
					OpPair<OpNode, Float> nodeLikelinesses = buildNodeLikelinesses(newSegment2, nodesToDo);
					likelinessesList2.add(nodeLikelinesses.first);
					likelinessesList3.add(nodeLikelinesses.second);
				}
				si++;
			}
			
		}
		if (tryOpenEnabled) {
			while(removeTooLong()){}
		}
		removeIntersections();
		supplementFromMST();
		Segment[] result = new Segment[foundSegments.size()];
		int i = 0;
		for (OpSegment os : foundSegments) {
			result[i] = os.toSegment();
			i++;
		}
		System.out.println("Largest memory used (MB): " + largestMemoryUsed/(1024*1024));
		return result;
	}
	
	private boolean removeTooLong() {
		float longestLength = Integer.MIN_VALUE;
		float oneToLongestLength = Integer.MIN_VALUE;
		OpSegment longestSegment = null;
		for (OpSegment segment : foundSegments) {
			float length = segment.length();
			if (length > longestLength) {
				oneToLongestLength = longestLength;
				longestLength = length;
				longestSegment = segment;
			} else if (length > oneToLongestLength) {
				oneToLongestLength = length;
			}
		}
		if (longestSegment != null) {
			if (longestLength > 1.4*oneToLongestLength) {
				foundSegments.remove(longestSegment);
				return true;
			}
		}
		return false;
	}
	
	private void removeIntersections() {
		Map<OpSegment, Set<OpSegment>> m = new HashMap<OpSegment, Set<OpSegment>>();
		for (OpSegment segment : foundSegments) {
			for (OpSegment oSegment : foundSegments) {
				if (!segment.equals(oSegment)) {
					if (segment.intersectsWith(oSegment)) {
						Set<OpSegment> h;
						if (!m.containsKey(segment)) {
							h = new HashSet<OpSegment>();
						} else {
							h = m.get(segment);
						}
						h.add(oSegment);
						m.put(segment, h);
					}
				}
			}
		}
		while (m.size() > 0) {
			int hc = -1;
			OpSegment hs = null;
			for (Entry<OpSegment, Set<OpSegment>> e : m.entrySet()) {
				int c = e.getValue().size();
				if (c > hc) {
					hc = c;
					hs = e.getKey();
				}
			}
			foundSegments.remove(hs);
			Set<OpSegment> fr = new HashSet<OpSegment>();
			for (Entry<OpSegment, Set<OpSegment>> e : m.entrySet()) {
				Set<OpSegment> s = e.getValue();
				s.remove(hs);
				if (s.size() == 0) {
					fr.add(e.getKey());
				}
			}
			for (OpSegment oo : fr) {
				m.remove(oo);
			}
			m.remove(hs);
		}
	}
	
	private OpPair<OpNode, Float> buildNodeLikelinesses(OpSegment segment, HashSet<OpNode> nodesToDo) {
		OpNode smallestNode = null;
		float smallestLikeliness = 1337.13371337f;
		for (OpNode n : nodesToDo) {
			float dx = segment.x2-segment.x1;
			float dy = segment.y2-segment.y1;
			float segmentLength = (float) Math.sqrt(dx*dx+dy*dy);
			float nx = n.x;
			float ny = n.y;
			OpNode sNode1 = segment.node1;
			dx = sNode1.x-nx;
			dy = sNode1.y-ny;
			float distance1 = (float) Math.sqrt(dx*dx+dy*dy);
			OpNode sNode2 = segment.node2;
			dx = sNode2.x-nx;
			dy = sNode2.y-ny;
			float distance2 = (float) Math.sqrt(dx*dx+dy*dy);
			float likeliness = (distance1*distance1+distance2*distance2)/(segmentLength*segmentLength);
			if (likeliness < smallestLikeliness || smallestLikeliness == 1337.13371337f) {
				smallestLikeliness = likeliness;
				smallestNode = n;
			}
		}
		return new OpPair<OpNode, Float>(smallestNode, smallestLikeliness);
	}
	
}
